package org.example.mentalblogh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.mentalblogh.common.constant.ArticleStatus;
import org.example.mentalblogh.common.exception.BusinessException;
import org.example.mentalblogh.common.result.ErrorCode;
import org.example.mentalblogh.dto.request.ArticleRequest;
import org.example.mentalblogh.dto.response.ArchiveVO;
import org.example.mentalblogh.dto.response.ArticleVO;
import org.example.mentalblogh.entity.*;
import org.example.mentalblogh.mapper.*;
import org.example.mentalblogh.service.ArticleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;
    private final UserMapper userMapper;

    public ArticleServiceImpl(CategoryMapper categoryMapper, TagMapper tagMapper,
                              ArticleTagMapper articleTagMapper, UserMapper userMapper) {
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
        this.articleTagMapper = articleTagMapper;
        this.userMapper = userMapper;
    }

    @Override
    public IPage<ArticleVO> getPublishedArticles(long page, long pageSize, Long categoryId, Long tagId, String keyword) {
        Page<Article> pageParam = new Page<>(page, pageSize);

        // 构建查询条件
        LambdaQueryWrapper<Article> query = new LambdaQueryWrapper<>();
        query.eq(Article::getStatus, ArticleStatus.PUBLISHED);
        if (categoryId != null) {
            query.eq(Article::getCategoryId, categoryId);
        }
        if (tagId != null) {
            // 联表查询有 tag 的文章
            List<Long> articleIds = articleTagMapper.selectList(
                    new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getTagId, tagId)
            ).stream().map(ArticleTag::getArticleId).collect(Collectors.toList());
            if (articleIds.isEmpty()) {
                Page<ArticleVO> emptyPage = new Page<>(page, pageSize);
                return emptyPage;
            }
            query.in(Article::getId, articleIds);
        }
        if (StringUtils.hasText(keyword)) {
            query.and(w -> w.like(Article::getTitle, keyword)
                    .or().like(Article::getSummary, keyword)
                    .or().like(Article::getContent, keyword));
        }

        // 置顶优先，再按时间倒序
        query.orderByDesc(Article::getTop).orderByDesc(Article::getPublishedAt);

        Page<Article> articlePage = baseMapper.selectPage(pageParam, query);
        List<ArticleVO> voList = batchConvertToVO(articlePage.getRecords());
        Page<ArticleVO> resultPage = new Page<>(articlePage.getCurrent(), articlePage.getSize(), articlePage.getTotal());
        resultPage.setRecords(voList);
        return resultPage;
    }

    @Override
    public ArticleVO getArticleById(Long id) {
        Article article = getById(id);
        if (article == null) {
            throw new BusinessException(ErrorCode.ARTICLE_NOT_FOUND);
        }
        // 阅读量：前端控制防刷，后端不自动增加
        // 由前端调用 PUT /api/articles/{id}/view 接口增加
        return convertToVO(article);
    }

    @Override
    public ArticleVO getArticleBySlug(String slug) {
        LambdaQueryWrapper<Article> query = new LambdaQueryWrapper<>();
        query.eq(Article::getSlug, slug);
        Article article = baseMapper.selectOne(query);
        if (article == null) {
            throw new BusinessException(ErrorCode.ARTICLE_NOT_FOUND);
        }
        // 阅读量：前端控制防刷，后端不自动增加
        return convertToVO(article);
    }

    /** 增加阅读量（由前端调用） */
    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        Article article = getById(id);
        if (article != null) {
            article.setViewCount(article.getViewCount() + 1);
            updateById(article);
        }
    }

    @Override
    public IPage<ArticleVO> getAdminArticles(long page, long pageSize, Integer status, Long categoryId) {
        Page<Article> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Article> query = new LambdaQueryWrapper<>();
        if (status != null) {
            query.eq(Article::getStatus, status);
        }
        if (categoryId != null) {
            query.eq(Article::getCategoryId, categoryId);
        }
        query.orderByDesc(Article::getCreatedAt);

        Page<Article> articlePage = baseMapper.selectPage(pageParam, query);
        List<ArticleVO> voList = batchConvertToVO(articlePage.getRecords());
        Page<ArticleVO> resultPage = new Page<>(articlePage.getCurrent(), articlePage.getSize(), articlePage.getTotal());
        resultPage.setRecords(voList);
        return resultPage;
    }

    @Override
    @Transactional
    public Long createArticle(ArticleRequest request, Long authorId) {
        // 检查 slug 唯一性
        LambdaQueryWrapper<Article> slugQuery = new LambdaQueryWrapper<>();
        slugQuery.eq(Article::getSlug, request.getSlug());
        if (baseMapper.selectCount(slugQuery) > 0) {
            throw new BusinessException(ErrorCode.SLUG_EXISTS);
        }

        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setCoverImage(request.getCoverImage());
        article.setSlug(request.getSlug());
        article.setStatus(request.getStatus());
        article.setTop(request.getTop() != null ? request.getTop() : 0);
        article.setCategoryId(request.getCategoryId());
        article.setAuthorId(authorId);
        article.setViewCount(0);
        article.setLikeCount(0);
        article.setFavoriteCount(0);
        article.setCommentCount(0);
        if (request.getStatus() == ArticleStatus.PUBLISHED) {
            article.setPublishedAt(LocalDateTime.now());
        }

        baseMapper.insert(article);

        // 保存标签关联
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            for (Long tagId : request.getTagIds()) {
                ArticleTag at = new ArticleTag();
                at.setArticleId(article.getId());
                at.setTagId(tagId);
                articleTagMapper.insert(at);
            }
        }

        return article.getId();
    }

    @Override
    @Transactional
    public void updateArticle(Long id, ArticleRequest request) {
        Article article = getById(id);
        if (article == null) {
            throw new BusinessException(ErrorCode.ARTICLE_NOT_FOUND);
        }

        // 检查 slug 唯一性
        LambdaQueryWrapper<Article> slugQuery = new LambdaQueryWrapper<>();
        slugQuery.eq(Article::getSlug, request.getSlug()).ne(Article::getId, id);
        if (baseMapper.selectCount(slugQuery) > 0) {
            throw new BusinessException(ErrorCode.SLUG_EXISTS);
        }

        article.setTitle(request.getTitle());
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setCoverImage(request.getCoverImage());
        article.setSlug(request.getSlug());
        article.setStatus(request.getStatus());
        article.setTop(request.getTop() != null ? request.getTop() : 0);
        article.setCategoryId(request.getCategoryId());
        if (request.getStatus() == ArticleStatus.PUBLISHED && article.getPublishedAt() == null) {
            article.setPublishedAt(LocalDateTime.now());
        }

        updateById(article);

        // 更新标签关联
        articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, id));
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            for (Long tagId : request.getTagIds()) {
                ArticleTag at = new ArticleTag();
                at.setArticleId(id);
                at.setTagId(tagId);
                articleTagMapper.insert(at);
            }
        }
    }

    @Override
    @Transactional
    public void deleteArticle(Long id) {
        Article article = getById(id);
        if (article == null) {
            throw new BusinessException(ErrorCode.ARTICLE_NOT_FOUND);
        }
        // 删除标签关联
        articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, id));
        removeById(id);
    }

    @Override
    public List<ArchiveVO> getArchives() {
        LambdaQueryWrapper<Article> query = new LambdaQueryWrapper<>();
        query.eq(Article::getStatus, ArticleStatus.PUBLISHED)
                .isNotNull(Article::getPublishedAt)
                .orderByDesc(Article::getPublishedAt);

        List<Article> articles = baseMapper.selectList(query);

        // 按年月分组
        Map<String, List<ArchiveVO.ArchiveItem>> grouped = articles.stream()
                .map(a -> ArchiveVO.ArchiveItem.builder()
                        .id(a.getId())
                        .title(a.getTitle())
                        .slug(a.getSlug())
                        .publishedAt(a.getPublishedAt().toString())
                        .build())
                .collect(Collectors.groupingBy(item -> {
                    String date = item.getPublishedAt();
                    return date.substring(0, 7);
                }, LinkedHashMap::new, Collectors.toList()));

        return grouped.entrySet().stream()
                .map(entry -> {
                    String[] parts = entry.getKey().split("-");
                    return ArchiveVO.builder()
                            .year(parts[0])
                            .month(parts[1])
                            .articles(entry.getValue())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ArticleVO> getHotArticles(int limit) {
        LambdaQueryWrapper<Article> query = new LambdaQueryWrapper<>();
        query.eq(Article::getStatus, ArticleStatus.PUBLISHED)
                .orderByDesc(Article::getViewCount)
                .last("LIMIT " + limit);
        List<Article> articles = baseMapper.selectList(query);
        return articles.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    /**
     * 批量转换 List<Article> 为 List<ArticleVO>（消除 N+1）
     */
    private List<ArticleVO> batchConvertToVO(List<Article> articles) {

        // 1. 批量查用户
        Set<Long> authorIds = articles.stream().map(Article::getAuthorId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Map<Long, User> userMap = new HashMap<>();
        if (!authorIds.isEmpty()) {
            userMapper.selectList(new LambdaQueryWrapper<User>().in(User::getId, authorIds)).forEach(u -> userMap.put(u.getId(), u));
        }

        // 2. 批量查分类
        Set<Long> catIds = articles.stream().map(Article::getCategoryId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> catMap = new HashMap<>();
        if (!catIds.isEmpty()) {
            categoryMapper.selectList(new LambdaQueryWrapper<Category>().in(Category::getId, catIds)).forEach(c -> catMap.put(c.getId(), c.getName()));
        }

        // 3. 批量查标签
        String idsStr = articles.stream().map(a -> String.valueOf(a.getId()))
                .collect(Collectors.joining(","));
        Map<Long, List<String>> tagMap = new HashMap<>();
        if (!idsStr.isEmpty()) {
            List<java.util.Map<String, Object>> tagRows = baseMapper.selectTagsByArticleIds(idsStr);
            tagRows.forEach(row -> {
                Long aid = Long.valueOf(String.valueOf(row.get("article_id")));
                String tName = (String) row.get("tag_name");
                tagMap.computeIfAbsent(aid, k -> new ArrayList<>()).add(tName);
            });
        }

        // 4. 组装 VO
        List<ArticleVO> result = new ArrayList<>();
        for (Article a : articles) {
            User u = userMap.get(a.getAuthorId());
            result.add(ArticleVO.builder()
                .id(a.getId()).title(a.getTitle()).summary(a.getSummary())
                .content(a.getContent()).coverImage(a.getCoverImage()).slug(a.getSlug())
                .status(a.getStatus()).top(a.getTop())
                .viewCount(a.getViewCount()).likeCount(a.getLikeCount())
                .favoriteCount(a.getFavoriteCount()).commentCount(a.getCommentCount())
                .categoryId(a.getCategoryId()).categoryName(catMap.get(a.getCategoryId()))
                .authorId(a.getAuthorId())
                .authorName(u != null ? u.getNickname() : null)
                .authorAvatar(u != null ? u.getAvatar() : null)
                .tags(tagMap.getOrDefault(a.getId(), new ArrayList<>()))
                .publishedAt(a.getPublishedAt() != null ? a.getPublishedAt().toString() : null)
                .createdAt(a.getCreatedAt() != null ? a.getCreatedAt().toString() : null)
                .updatedAt(a.getUpdatedAt() != null ? a.getUpdatedAt().toString() : null)
                .build());
        }
        return result;
    }


    private ArticleVO convertToVO(Article article) {
        // 分类名
        String categoryName = null;
        if (article.getCategoryId() != null) {
            Category category = categoryMapper.selectById(article.getCategoryId());
            if (category != null) {
                categoryName = category.getName();
            }
        }

        // 作者
        User author = userMapper.selectById(article.getAuthorId());

        // 标签
        List<String> tags = articleTagMapper.selectList(
                new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, article.getId())
        ).stream().map(at -> {
            Tag tag = tagMapper.selectById(at.getTagId());
            return tag != null ? tag.getName() : null;
        }).filter(java.util.Objects::nonNull).collect(Collectors.toList());

        return ArticleVO.builder()
                .id(article.getId())
                .title(article.getTitle())
                .summary(article.getSummary())
                .content(article.getContent())
                .coverImage(article.getCoverImage())
                .slug(article.getSlug())
                .status(article.getStatus())
                .top(article.getTop())
                .viewCount(article.getViewCount())
                .likeCount(article.getLikeCount())
                .favoriteCount(article.getFavoriteCount())
                .commentCount(article.getCommentCount())
                .categoryId(article.getCategoryId())
                .categoryName(categoryName)
                .authorId(article.getAuthorId())
                .authorName(author != null ? author.getNickname() : null)
                .authorAvatar(author != null ? author.getAvatar() : null)
                .tags(tags)
                .publishedAt(article.getPublishedAt() != null ? article.getPublishedAt().toString() : null)
                .createdAt(article.getCreatedAt() != null ? article.getCreatedAt().toString() : null)
                .updatedAt(article.getUpdatedAt() != null ? article.getUpdatedAt().toString() : null)
                .build();


}
}
