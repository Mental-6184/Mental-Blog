package org.example.mentalblogh.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.mentalblogh.entity.Comment;
import org.example.mentalblogh.mapper.CommentMapper;
import org.example.mentalblogh.service.CommentService;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
}
