package org.example.mentalblogh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.mentalblogh.dto.request.LoginRequest;
import org.example.mentalblogh.dto.request.RegisterRequest;
import org.example.mentalblogh.dto.response.LoginVO;
import org.example.mentalblogh.dto.response.UserInfoVO;
import org.example.mentalblogh.entity.User;

public interface UserService extends IService<User> {
    LoginVO login(LoginRequest request);
    void register(RegisterRequest request);
    UserInfoVO getCurrentUserInfo(Long userId);
    void updateProfile(Long userId, String nickname, String bio, String avatar);
    void updatePassword(Long userId, String oldPassword, String newPassword);
    User getByUsername(String username);
}
