package org.example.mentalblogh.controller;

import org.example.mentalblogh.common.constant.ArticleStatus;
import org.example.mentalblogh.entity.Article;
import org.example.mentalblogh.entity.Category;
import org.example.mentalblogh.entity.Tag;
import org.example.mentalblogh.mapper.ArticleMapper;
import org.example.mentalblogh.mapper.CategoryMapper;
import org.example.mentalblogh.mapper.TagMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

@RestController
@RequestMapping("/api/seo")
public class SeoController {

    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;

    public SeoController(ArticleMapper articleMapper, CategoryMapper categoryMapper, TagMapper tagMapper) {
        this.articleMapper = articleMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
    }

    @GetMapping(value = "/sitemap", produces = MediaType.APPLICATION_XML_VALUE)
    public String sitemap() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");

        String base = "http://localhost:5173";
        String today = java.time.LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        // 首页
        sb.append("<url><loc>").append(base).append("/</loc><priority>1.0</priority><changefreq>daily</changefreq></url>");

        // 归档
        sb.append("<url><loc>").append(base).append("/archives</loc><priority>0.8</priority><changefreq>weekly</changefreq></url>");
        sb.append("<url><loc>").append(base).append("/about</loc><priority>0.5</priority></url>");
        sb.append("<url><loc>").append(base).append("/links</loc><priority>0.5</priority></url>");

        // 分类
        List<Category> categories = categoryMapper.selectList(null);
        for (Category c : categories) {
            sb.append("<url><loc>").append(base).append("/category/").append(c.getSlug())
                    .append("</loc><priority>0.6</priority></url>");
        }

        // 标签
        List<Tag> tags = tagMapper.selectList(null);
        for (Tag t : tags) {
            sb.append("<url><loc>").append(base).append("/tag/").append(t.getSlug())
                    .append("</loc><priority>0.4</priority></url>");
        }

        // 文章
        List<Article> articles = articleMapper.selectList(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, 1));
        for (Article a : articles) {
            sb.append("<url><loc>").append(base).append("/article/").append(a.getId())
                    .append("</loc><priority>0.9</priority>");
            if (a.getPublishedAt() != null) {
                sb.append("<lastmod>").append(a.getPublishedAt().format(DateTimeFormatter.ISO_DATE)).append("</lastmod>");
            }
            sb.append("</url>");
        }

        sb.append("</urlset>");
        return sb.toString();
    }
}
