package org.example.mentalblogh.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.mentalblogh.common.result.PageResult;
import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.common.result.ErrorCode;
import org.example.mentalblogh.entity.Image;
import org.example.mentalblogh.entity.ImageTag;
import org.example.mentalblogh.entity.ImageTagRel;
import org.example.mentalblogh.entity.User;
import org.example.mentalblogh.mapper.ImageMapper;
import org.example.mentalblogh.mapper.ImageTagMapper;
import org.example.mentalblogh.mapper.ImageTagRelMapper;
import org.example.mentalblogh.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageMapper imageMapper;
    private final ImageTagMapper imageTagMapper;
    private final ImageTagRelMapper imageTagRelMapper;
    private final UserMapper userMapper;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public ImageController(ImageMapper imageMapper, ImageTagMapper imageTagMapper,
                           ImageTagRelMapper imageTagRelMapper, UserMapper userMapper) {
        this.imageMapper = imageMapper;
        this.imageTagMapper = imageTagMapper;
        this.imageTagRelMapper = imageTagRelMapper;
        this.userMapper = userMapper;
    }

    // ===== 获取图片标签列表 =====
    @GetMapping("/tags")
    public Result<List<ImageTag>> getTags(Authentication authentication) {
        checkImagePermission(authentication);
        return Result.success(imageTagMapper.selectList(
                new LambdaQueryWrapper<ImageTag>().orderByAsc(ImageTag::getId)));
    }

    // ===== 图片列表（分页，支持标签筛选和搜索） =====
    @GetMapping
    public PageResult<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) String q,
            Authentication authentication) {
        checkImagePermission(authentication);
        Page<Image> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Image> query = new LambdaQueryWrapper<>();
        query.orderByDesc(Image::getCreatedAt);

        // 标签筛选
        if (tagId != null) {
            List<Long> ids = imageTagRelMapper.selectList(
                    new LambdaQueryWrapper<ImageTagRel>().eq(ImageTagRel::getTagId, tagId)
            ).stream().map(ImageTagRel::getImageId).collect(Collectors.toList());
            if (ids.isEmpty()) {
                return PageResult.success(new Page<>(page, pageSize));
            }
            query.in(Image::getId, ids);
        }

        // 关键词搜索
        if (q != null && !q.trim().isEmpty()) {
            query.like(Image::getDescription, q.trim())
                 .or().like(Image::getFilename, q.trim());
        }

        Page<Image> imagePage = imageMapper.selectPage(pageParam, query);
        Page<Map<String, Object>> result = new Page<>(imagePage.getCurrent(), imagePage.getSize(), imagePage.getTotal());
        result.setRecords(imagePage.getRecords().stream().map(img -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", img.getId());
            m.put("url", img.getUrl());
            m.put("filename", img.getFilename());
            m.put("size", img.getSize());
            m.put("mimeType", img.getMimeType());
            m.put("userId", img.getUserId());
            m.put("description", img.getDescription());
            m.put("likeCount", img.getLikeCount());
            m.put("createdAt", img.getCreatedAt() != null ? img.getCreatedAt().toString() : null);

            // 获取关联标签
            List<ImageTag> tags = imageTagRelMapper.selectList(
                    new LambdaQueryWrapper<ImageTagRel>().eq(ImageTagRel::getImageId, img.getId())
            ).stream().map(rel -> imageTagMapper.selectById(rel.getTagId()))
             .filter(Objects::nonNull).collect(Collectors.toList());
            m.put("tags", tags);

            return m;
        }).collect(Collectors.toList()));

        return PageResult.success(result);
    }

    // ===== 图片详情 =====
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id, Authentication authentication) {
        checkImagePermission(authentication);
        Image img = imageMapper.selectById(id);
        if (img == null) {
            return Result.error(404, "图片不存在");
        }
        Map<String, Object> m = new HashMap<>();
        m.put("id", img.getId());
        m.put("url", img.getUrl());
        m.put("filename", img.getFilename());
        m.put("storedPath", img.getStoredPath());
        m.put("size", img.getSize());
        m.put("mimeType", img.getMimeType());
        m.put("userId", img.getUserId());
        m.put("description", img.getDescription());
        m.put("likeCount", img.getLikeCount());
        m.put("createdAt", img.getCreatedAt() != null ? img.getCreatedAt().toString() : null);

        List<ImageTag> tags = imageTagRelMapper.selectList(
                new LambdaQueryWrapper<ImageTagRel>().eq(ImageTagRel::getImageId, img.getId())
        ).stream().map(rel -> imageTagMapper.selectById(rel.getTagId()))
         .filter(Objects::nonNull).collect(Collectors.toList());
        m.put("tags", tags);

        return Result.success(m);
    }

    // ===== 上传图片 =====
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, Object>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "tagIds", required = false) String tagIdsStr,
            @RequestParam(value = "description", required = false) String description,
            Authentication authentication) throws IOException {

        Long userId = (Long) authentication.getPrincipal();
        User user = userMapper.selectById(userId);
        if (user == null || user.getImagePermission() == null || user.getImagePermission() < 2) {
            return Result.error(403, "你没有使用图库的权限，请先向管理员申请");
        }

        if (file.isEmpty()) {
            return Result.error(400, "请选择文件");
        }

        // 校验文件类型
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        }
        List<String> allowed = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp");
        if (!allowed.contains(ext)) {
            return Result.error(400, "仅支持 JPG/PNG/GIF/WebP 格式");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            return Result.error(400, "文件类型校验失败");
        }
        if (!hasValidImageSignature(file, ext)) {
            return Result.error(400, "文件内容不是有效图片");
        }

        // 校验大小（10MB）
        if (file.getSize() > 10 * 1024 * 1024) {
            return Result.error(400, "文件不能超过 10MB");
        }

        // 按年月创建目录
        LocalDate now = LocalDate.now();
        String datePath = "images/" + now.getYear() + "/" + String.format("%02d", now.getMonthValue());
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String storedName = uuid + ext;
        String relativePath = datePath + "/" + storedName;

        Path uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path dirPath = uploadRoot.resolve(datePath).normalize();
        if (!dirPath.startsWith(uploadRoot)) {
            return Result.error(400, "非法文件路径");
        }
        Files.createDirectories(dirPath);
        Path filePath = dirPath.resolve(storedName).normalize();
        if (!filePath.startsWith(uploadRoot)) {
            return Result.error(400, "非法文件路径");
        }
        file.transferTo(filePath.toFile());

        String url = "/uploads/" + relativePath;

        // 入库
        Image img = new Image();
        img.setFilename(originalName != null ? originalName : "unknown");
        img.setStoredPath("/uploads/" + relativePath);
        img.setUrl(url);
        img.setSize(file.getSize());
        img.setMimeType(contentType);
        img.setUserId(userId);
        img.setDescription(description);
        img.setLikeCount(0);
        try {
            imageMapper.insert(img);
        } catch (RuntimeException e) {
            Files.deleteIfExists(filePath);
            throw e;
        }

        // 保存标签关联
        if (tagIdsStr != null && !tagIdsStr.trim().isEmpty()) {
            String[] ids = tagIdsStr.split(",");
            for (String tid : ids) {
                try {
                    Long tagId = Long.parseLong(tid.trim());
                    ImageTagRel rel = new ImageTagRel();
                    rel.setImageId(img.getId());
                    rel.setTagId(tagId);
                    imageTagRelMapper.insert(rel);
                } catch (NumberFormatException ignored) { }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", img.getId());
        result.put("url", img.getUrl());
        return Result.success("上传成功", result);
    }

    private boolean hasValidImageSignature(MultipartFile file, String ext) throws IOException {
        try (InputStream input = file.getInputStream()) {
            byte[] header = input.readNBytes(12);
            if (header.length < 4) return false;
            boolean jpeg = (header[0] & 0xff) == 0xff && (header[1] & 0xff) == 0xd8;
            boolean png = (header[0] & 0xff) == 0x89 && header[1] == 0x50 && header[2] == 0x4e && header[3] == 0x47;
            boolean gif = header[0] == 'G' && header[1] == 'I' && header[2] == 'F';
            boolean webp = header.length >= 12 && header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F'
                    && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P';
            return (jpeg && (ext.equals(".jpg") || ext.equals(".jpeg")))
                    || (png && ext.equals(".png")) || (gif && ext.equals(".gif")) || (webp && ext.equals(".webp"));
        }
    }

    // ===== 删除图片 =====
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, Authentication authentication) {
        checkImagePermission(authentication);
        Image img = imageMapper.selectById(id);
        if (img == null) {
            return Result.error(404, "图片不存在");
        }
        Long userId = (Long) authentication.getPrincipal();
        if (!img.getUserId().equals(userId)) {
            return Result.error(403, "只能删除自己的图片");
        }

        // 删除文件
        try {
            Path filePath = Paths.get(uploadDir).resolve(img.getStoredPath().replace("/uploads/", ""));
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) { }

        // 删除关联
        imageTagRelMapper.delete(new LambdaQueryWrapper<ImageTagRel>().eq(ImageTagRel::getImageId, id));
        imageMapper.deleteById(id);

        return Result.success("删除成功", null);
    }

    // ===== 更新描述/标签 =====
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body,
                                Authentication authentication) {
        Image img = imageMapper.selectById(id);
        if (img == null) {
            return Result.error(404, "图片不存在");
        }
        Long userId = (Long) authentication.getPrincipal();
        if (!img.getUserId().equals(userId)) {
            return Result.error(403, "只能修改自己的图片");
        }

        if (body.containsKey("description")) {
            img.setDescription((String) body.get("description"));
        }
        imageMapper.updateById(img);

        // 更新标签
        if (body.containsKey("tagIds")) {
            @SuppressWarnings("unchecked")
            List<Integer> tagIds = (List<Integer>) body.get("tagIds");
            imageTagRelMapper.delete(new LambdaQueryWrapper<ImageTagRel>().eq(ImageTagRel::getImageId, id));
            if (tagIds != null) {
                for (Integer tid : tagIds) {
                    ImageTagRel rel = new ImageTagRel();
                    rel.setImageId(id);
                    rel.setTagId(tid.longValue());
                    imageTagRelMapper.insert(rel);
                }
            }
        }

        return Result.success("更新成功", null);
    }

    /** 检查当前用户是否有图库权限 */
    private void checkImagePermission(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        User user = userMapper.selectById(userId);
        if (user == null || user.getImagePermission() == null || user.getImagePermission() < 2) {
            throw new org.example.mentalblogh.common.exception.BusinessException(ErrorCode.IMAGE_PERMISSION_DENIED);
        }
    }
}
