package org.example.mentalblogh.controller;

import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Item;
import org.example.mentalblogh.common.constant.ArticleStatus;
import org.example.mentalblogh.entity.Article;
import org.example.mentalblogh.mapper.ArticleMapper;
import org.example.mentalblogh.mapper.SiteConfigMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rss")
public class RssController {

    private final ArticleMapper articleMapper;
    private final SiteConfigMapper siteConfigMapper;

    public RssController(ArticleMapper articleMapper, SiteConfigMapper siteConfigMapper) {
        this.articleMapper = articleMapper;
        this.siteConfigMapper = siteConfigMapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public Channel rss() {
        Channel channel = new Channel("rss_2.0");

        String siteName = "MentalBlog";
        String siteDesc = "一个分享技术与生活的个人博客";
        try {
            var cfg = siteConfigMapper.selectList(null);
            for (var c : cfg) {
                if ("site_name".equals(c.getConfigKey())) siteName = c.getConfigValue();
                if ("site_description".equals(c.getConfigKey())) siteDesc = c.getConfigValue();
            }
        } catch (Exception e) { /* ignore */ }

        channel.setTitle(siteName);
        channel.setDescription(siteDesc);
        channel.setLink("http://localhost:5173");
        channel.setPubDate(new Date());

        List<Article> articles = articleMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, 1)
                        .orderByDesc(Article::getPublishedAt)
                        .last("LIMIT 20"));

        List<Item> items = articles.stream().map(a -> {
            Item item = new Item();
            item.setTitle(a.getTitle());
            item.setLink("http://localhost:5173/article/" + a.getId());
            item.setPubDate(java.sql.Timestamp.valueOf(a.getPublishedAt()));
            Description desc = new Description();
            desc.setValue(a.getSummary() != null ? a.getSummary() : a.getTitle());
            item.setDescription(desc);
            item.setGuid(new com.rometools.rome.feed.rss.Guid());
            item.getGuid().setValue(a.getId().toString());
            return item;
        }).collect(Collectors.toList());

        channel.setItems(items);
        return channel;
    }
}
