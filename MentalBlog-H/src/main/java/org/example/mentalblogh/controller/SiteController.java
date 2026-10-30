package org.example.mentalblogh.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.entity.FriendLink;
import org.example.mentalblogh.entity.SiteConfig;
import org.example.mentalblogh.mapper.FriendLinkMapper;
import org.example.mentalblogh.mapper.SiteConfigMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/site")
public class SiteController {

    private final SiteConfigMapper siteConfigMapper;
    private final FriendLinkMapper friendLinkMapper;

    public SiteController(SiteConfigMapper siteConfigMapper, FriendLinkMapper friendLinkMapper) {
        this.siteConfigMapper = siteConfigMapper;
        this.friendLinkMapper = friendLinkMapper;
    }

    @GetMapping("/config")
    public Result<Map<String, String>> getPublicConfig() {
        List<SiteConfig> configs = siteConfigMapper.selectList(null);
        Map<String, String> map = configs.stream()
                .collect(Collectors.toMap(SiteConfig::getConfigKey, SiteConfig::getConfigValue));
        return Result.success(map);
    }

    @GetMapping("/friend-links")
    public Result<List<FriendLink>> getFriendLinks() {
        List<FriendLink> list = friendLinkMapper.selectList(
                new LambdaQueryWrapper<FriendLink>()
                        .eq(FriendLink::getStatus, 1)
                        .orderByAsc(FriendLink::getSort));
        return Result.success(list);
    }
}
