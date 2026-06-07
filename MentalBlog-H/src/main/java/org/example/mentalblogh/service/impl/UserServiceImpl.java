package org.example.mentalblogh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.mentalblogh.common.constant.UserRole;
import org.example.mentalblogh.common.exception.BusinessException;
import org.example.mentalblogh.common.result.ErrorCode;
import org.example.mentalblogh.common.utils.JwtUtil;
import org.example.mentalblogh.common.utils.PasswordUtil;
import org.example.mentalblogh.dto.request.LoginRequest;
import org.example.mentalblogh.dto.request.RegisterRequest;
import org.example.mentalblogh.dto.response.LoginVO;
import org.example.mentalblogh.dto.response.UserInfoVO;
import org.example.mentalblogh.entity.User;
import org.example.mentalblogh.mapper.UserMapper;
import org.example.mentalblogh.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtil jwtUtil;

    public UserServiceImpl(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginVO login(LoginRequest request) {
        User user = getByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(ErrorCode.USERNAME_OR_PASSWORD_ERROR);
        }
        if (user.getStatus() == 1) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }
        if (!PasswordUtil.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.USERNAME_OR_PASSWORD_ERROR);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return LoginVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .token(token)
                .build();
    }

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        // 检查用户名是否存在
        if (getByUsername(request.getUsername()) != null) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }

        // 检查邮箱是否存在
        LambdaQueryWrapper<User> emailQuery = new LambdaQueryWrapper<>();
        emailQuery.eq(User::getEmail, request.getEmail());
        if (baseMapper.selectCount(emailQuery) > 0) {
            throw new BusinessException(ErrorCode.EMAIL_EXISTS);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtil.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setRole(UserRole.USER);
        user.setStatus(0);

        baseMapper.insert(user);
    }

    @Override
    public UserInfoVO getCurrentUserInfo(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return UserInfoVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .bio(user.getBio())
                .role(user.getRole())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                .build();
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, String nickname, String bio, String avatar) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        user.setNickname(nickname);
        user.setBio(bio);
        user.setAvatar(avatar);
        updateById(user);
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        if (!PasswordUtil.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(1007, "原密码错误");
        }
        user.setPassword(PasswordUtil.encode(newPassword));
        updateById(user);
    }

    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getUsername, username);
        return baseMapper.selectOne(query);
    }
}
