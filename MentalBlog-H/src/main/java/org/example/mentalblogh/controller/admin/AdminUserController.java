package org.example.mentalblogh.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.mentalblogh.common.result.PageResult;
import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.entity.User;
import org.example.mentalblogh.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserMapper userMapper;

    public AdminUserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping
    public PageResult<User> list(@RequestParam(defaultValue = "1") long page,
                                  @RequestParam(defaultValue = "10") long pageSize) {
        Page<User> pageParam = new Page<>(page, pageSize);
        return PageResult.success(userMapper.selectPage(pageParam, null));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setStatus(body.get("status"));
            userMapper.updateById(user);
        }
        return Result.success();
    }
}
