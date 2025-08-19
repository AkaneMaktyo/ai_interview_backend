package com.example.aiinterview.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 答题记录实体类
 */
@TableName(value = "answer_records", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = false)
public class AnswerRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("question_id")
    private Long questionId;

    @TableField("user_answer")
    private String userAnswer;

    @TableField(value = "ai_evaluation", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> aiEvaluation;

    @TableField("score")
    private Integer score;

    @TableField("time_spent")
    private Integer timeSpent;

    @TableField("attempt_count")
    private Integer attemptCount = 1;

    @TableField("interview_type")
    private String interviewType;

    @TableField("difficulty")
    private String difficulty;

    @TableField("position")
    private String position;

    @TableField("status")
    private String status = "completed";

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // 关联的题目信息（需要单独查询或通过联表查询获取）
    @TableField(exist = false)
    private Question question;

    @TableField(exist = false)
    private User user;
}