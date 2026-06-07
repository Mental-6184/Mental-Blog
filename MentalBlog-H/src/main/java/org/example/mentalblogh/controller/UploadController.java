package org.example.mentalblogh.controller;

import org.example.mentalblogh.common.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/upload")
public class UploadController {

    @Value("${app.upload.dir}")
    private String uploadPath;

    @PostMapping
    public Result<Map<String, String>> upload(@RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "file[]", required = false) MultipartFile fileArray) {
        if (file == null) file = fileArray;
        if (file == null) { return Result.error(400, "请选择文件"); }
        if (file.isEmpty()) {
            return Result.error(400, "文件为空");
        }

        // 检查文件大小（10MB）
        if (file.getSize() > 10 * 1024 * 1024) {
            return Result.error(4001, "文件大小超出限制");
        }

        // 检查文件类型
        String originalName = file.getOriginalFilename();
        if (originalName != null && !originalName.isEmpty()) {
            String ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
            if (!ext.matches("\\.(jpg|jpeg|png|gif|bmp|webp|svg|ico)$")) {
                return Result.error(4002, "文件类型不允许，仅支持图片");
            }
        }

        try {
            // 按日期分目录
            String dateDir = LocalDate.now().toString().replace("-", "/");
            File dir = new File(uploadPath + dateDir);
            if (!dir.exists()) dir.mkdirs();

            // 生成唯一文件名
            String ext = originalName != null ? originalName.substring(originalName.lastIndexOf(".")) : ".jpg";
            String newName = UUID.randomUUID().toString().replace("-", "") + ext;
            String relativePath = dateDir + "/" + newName;

            File dest = new File(uploadPath + relativePath);
            File parentDir = dest.getParentFile();
            if (!parentDir.exists()) parentDir.mkdirs();
            file.transferTo(dest);

            String url = "/uploads/" + relativePath;
            return Result.success(Map.of("url", url, "name", originalName));
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(4003, "文件上传失败");
        }
    }
}
