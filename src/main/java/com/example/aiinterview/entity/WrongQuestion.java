package com.example.aiinterview.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 错题集实体类
 */
@TableName("wrong_questions")
@Data
@EqualsAndHashCode(callSuper = false)
public class WrongQuestion {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("question_id")
    private Long questionId;

    @TableField("first_attempt_record_id")
    private Long firstAttemptRecordId;

    @TableField("retry_count")
    private Integer retryCount = 0;

    @TableField("last_retry_at")
    private LocalDateTime lastRetryAt;

    @TableField("mastered")
    private Boolean mastered = false;

    @TableField("mastered_at")
    private LocalDateTime masteredAt;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // 关联的题目信息（需要单独查询或通过联表查询获取）
    @TableField(exist = false)
    private Question question;

    @TableField(exist = false)
    private User user;

    @TableField(exist = false)
    private AnswerRecord firstAttemptRecord;
}