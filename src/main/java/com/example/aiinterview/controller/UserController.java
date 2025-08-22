package com.example.aiinterview.controller;

import com.example.aiinterview.entity.User;
import com.example.aiinterview.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取所有用户
     */
    @GetMapping("/list")
    public Map<String, Object> getAllUsers() {
        try {
            List<User> users = userService.findAll();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", users);
            response.put("count", users.size());
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return response;
        }
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/get")
    public Map<String, Object> getUserById(@RequestParam Long id) {
        try {
            Optional<User> user = userService.findById(id);
            Map<String, Object> response = new HashMap<>();
            if (user.isPresent()) {
                response.put("success", true);
                response.put("data", user.get());
            } else {
                response.put("success", false);
                response.put("error", "用户不存在");
            }
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return response;
        }
    }

    /**
     * 创建新用户
     */
    @PostMapping("/create")
    public Map<String, Object> createUser(@RequestParam String username, 
                                        @RequestParam String email,
                                        @RequestParam(required = false) String nickname,
                                        @RequestParam(required = false) String level) {
        try {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setNickname(nickname != null ? nickname : username);
            user.setLevel(level != null ? level : "beginner");
            
            User savedUser = userService.save(user);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", savedUser);
            response.put("message", "用户创建成功");
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return response;
        }
    }

    /**
     * 更新用户信息
     */
    @PostMapping("/update")
    public Map<String, Object> updateUser(@RequestParam Long id,
                                        @RequestParam(required = false) String username,
                                        @RequestParam(required = false) String email,
                                        @RequestParam(required = false) String nickname,
                                        @RequestParam(required = false) String level) {
        try {
            Optional<User> existingUser = userService.findById(id);
            if (!existingUser.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "用户不存在");
                return response;
            }
            
            User user = existingUser.get();
            if (username != null) user.setUsername(username);
            if (email != null) user.setEmail(email);
            if (nickname != null) user.setNickname(nickname);
            if (level != null) user.setLevel(level);
            
            User updatedUser = userService.update(id, user);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedUser);
            response.put("message", "用户更新成功");
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return response;
        }
    }

    /**
     * 删除用户
     */
    @PostMapping("/delete")
    public Map<String, Object> deleteUser(@RequestParam Long id) {
        try {
            boolean deleted = userService.deleteById(id);
            Map<String, Object> response = new HashMap<>();
            if (deleted) {
                response.put("success", true);
                response.put("message", "用户删除成功");
            } else {
                response.put("success", false);
                response.put("error", "用户不存在或删除失败");
            }
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return response;
        }
    }

    /**
     * 获取用户总数
     */
    @GetMapping("/count")
    public Map<String, Object> getUserCount() {
        try {
            long count = userService.count();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", count);
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return response;
        }
    }
}