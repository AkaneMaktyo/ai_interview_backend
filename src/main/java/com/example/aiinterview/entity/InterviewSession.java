package com.example.aiinterview.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 面试会话实体类
 */
@TableName(value = "interview_sessions", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = false)
public class InterviewSession {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "session_name")
    private String sessionName;

    @TableField(value = "interview_type")
    private String interviewType;

    @TableField(value = "difficulty")
    private String difficulty;

    @TableField(value = "position")
    private String position;

    @TableField(value = "status")
    private String status = "active";

    @TableField(value = "total_questions")
    private Integer totalQuestions = 0;

    @TableField(value = "answered_questions")
    private Integer answeredQuestions = 0;

    @TableField(value = "total_score")
    private Integer totalScore = 0;

    @TableField(value = "started_at", fill = FieldFill.INSERT)
    private LocalDateTime startedAt;

    @TableField(value = "ended_at")
    private LocalDateTime endedAt;

    @TableField(value = "session_data", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> sessionData;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // 临时字段，不映射到数据库
    @TableField(exist = false)
    private User user;
}