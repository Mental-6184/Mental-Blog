package org.example.mentalblogh.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.common.utils.CacheUtil;
import org.example.mentalblogh.entity.FriendLink;
import org.example.mentalblogh.entity.SiteConfig;
import org.example.mentalblogh.mapper.FriendLinkMapper;
import org.example.mentalblogh.mapper.SiteConfigMapper;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminSiteController {

    private final SiteConfigMapper siteConfigMapper;
    private final FriendLinkMapper friendLinkMapper;
    private final CacheUtil cacheUtil;

    public AdminSiteController(SiteConfigMapper siteConfigMapper, FriendLinkMapper friendLinkMapper,
                               CacheUtil cacheUtil) {
        this.siteConfigMapper = siteConfigMapper;
        this.friendLinkMapper = friendLinkMapper;
        this.cacheUtil = cacheUtil;
    }

    // ===== 站点配置 =====
    @GetMapping("/site-configs")
    public Result<Map<String, String>> getConfigs() {
        List<SiteConfig> configs = siteConfigMapper.selectList(null);
        Map<String, String> map = configs.stream()
                .collect(Collectors.toMap(SiteConfig::getConfigKey, SiteConfig::getConfigValue));
        return Result.success(map);
    }

    @PutMapping("/site-configs")
    public Result<Void> updateConfigs(@RequestBody Map<String, String> configs) {
        configs.forEach((key, value) -> {
            LambdaQueryWrapper<SiteConfig> query = new LambdaQueryWrapper<>();
            query.eq(SiteConfig::getConfigKey, key);
            SiteConfig config = siteConfigMapper.selectOne(query);
            if (config != null) {
                config.setConfigValue(value);
                siteConfigMapper.updateById(config);
            } else {
                config = new SiteConfig();
                config.setConfigKey(key);
                config.setConfigValue(value);
                siteConfigMapper.insert(config);
            }
        });
        // 更新配置后清除缓存
        cacheUtil.delete("cache:site-config");
        return Result.success();
    }

    // ===== 友链 =====
    @GetMapping("/friend-links")
    public Result<List<FriendLink>> getFriendLinks() {
        return Result.success(friendLinkMapper.selectList(
                new LambdaQueryWrapper<FriendLink>().orderByAsc(FriendLink::getSort)));
    }

    @PostMapping("/friend-links")
    public Result<Void> createFriendLink(@RequestBody FriendLink friendLink) {
        friendLink.setUrl(normalizeUrl(friendLink.getUrl()));
        friendLinkMapper.insert(friendLink);
        cacheUtil.delete("cache:friend-links");
        return Result.success("创建成功", null);
    }

    @PutMapping("/friend-links/{id}")
    public Result<Void> updateFriendLink(@PathVariable Long id, @RequestBody FriendLink friendLink) {
        friendLink.setId(id);
        friendLink.setUrl(normalizeUrl(friendLink.getUrl()));
        friendLinkMapper.updateById(friendLink);
        cacheUtil.delete("cache:friend-links");
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/friend-links/{id}")
    public Result<Void> deleteFriendLink(@PathVariable Long id) {
        friendLinkMapper.deleteById(id);
        cacheUtil.delete("cache:friend-links");
        return Result.success("删除成功", null);
    }

    /**
     * 规范化 URL：自动补全 http:// 协议头
     */
    private String normalizeUrl(String url) {
        if (!StringUtils.hasText(url)) return url;
        url = url.trim();
        if (!url.matches("^(https?://|ftp://).*")) {
            return "http://" + url;
        }
        return url;
    }
}
