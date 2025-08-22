package com.zsj.aiinterview.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("users")
public class User {
    /**
     * 用户主键ID，唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户名，用于登录，不可重复
     */
    private String username;
    
    /**
     * 用户邮箱，用于注册和找回密码
     */
    private String email;
    
    /**
     * 用户昵称，用于显示
     */
    private String nickname;
    
    /**
     * 用户头像URL
     */
    private String avatar;
    
    /**
     * 用户等级：beginner(初级)、intermediate(中级)、advanced(高级)
     */
    private String level;
    
    /**
     * 用户状态：true为激活，false为禁用
     */
    @TableField("is_active")
    private Boolean isActive;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 最后更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // 带参构造函数
    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.level = "beginner";
        this.isActive = true;
        // 移除手动设置时间，让MyBatis Plus自动填充
    }
}