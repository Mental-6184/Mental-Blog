package org.example.mentalblogh.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.mentalblogh.dto.request.ArticleRequest;
import org.example.mentalblogh.dto.response.ArchiveVO;
import org.example.mentalblogh.dto.response.ArticleVO;
import org.example.mentalblogh.entity.Article;

import java.util.List;

public interface ArticleService extends IService<Article> {
    IPage<ArticleVO> getPublishedArticles(long page, long pageSize, Long categoryId, Long tagId, String keyword);
    ArticleVO getPublishedArticleById(Long id);
    ArticleVO getPublishedArticleBySlug(String slug);
    ArticleVO getArticleById(Long id);
    IPage<ArticleVO> getAdminArticles(long page, long pageSize, Integer status, Long categoryId);
    Long createArticle(ArticleRequest request, Long authorId);
    void updateArticle(Long id, ArticleRequest request);
    void deleteArticle(Long id);
    List<ArchiveVO> getArchives();
    boolean incrementViewCount(Long id);
    List<ArticleVO> getHotArticles(int limit);
}
