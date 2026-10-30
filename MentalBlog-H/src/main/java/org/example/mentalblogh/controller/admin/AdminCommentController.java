package org.example.mentalblogh.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.mentalblogh.common.result.PageResult;
import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.entity.Comment;
import org.example.mentalblogh.mapper.CommentMapper;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/comments")
public class AdminCommentController {

    private final CommentMapper commentMapper;

    public AdminCommentController(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    @GetMapping
    public PageResult<Comment> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) Integer status) {
        Page<Comment> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Comment> query = new LambdaQueryWrapper<>();
        if (status != null) {
            query.eq(Comment::getStatus, status);
        }
        query.orderByDesc(Comment::getCreatedAt);
        return PageResult.success(commentMapper.selectPage(pageParam, query));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        commentMapper.deleteById(id);
        return Result.success("删除成功", null);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Comment comment = commentMapper.selectById(id);
        if (comment != null) {
            comment.setStatus(body.get("status"));
            commentMapper.updateById(comment);
        }
        return Result.success();
    }
}
