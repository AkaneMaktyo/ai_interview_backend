package com.example.aiinterview.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学习统计实体类
 */
@TableName(value = "learning_stats", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = false)
public class LearningStat {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "stat_date")
    private LocalDate statDate;

    @TableField(value = "questions_answered")
    private Integer questionsAnswered = 0;

    @TableField(value = "correct_answers")
    private Integer correctAnswers = 0;

    @TableField(value = "total_score")
    private Integer totalScore = 0;

    @TableField(value = "time_spent")
    private Integer timeSpent = 0;

    @TableField(value = "avg_score")
    private BigDecimal avgScore = BigDecimal.ZERO;

    @TableField(value = "accuracy_rate")
    private BigDecimal accuracyRate = BigDecimal.ZERO;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // 临时字段，不映射到数据库
    @TableField(exist = false)
    private User user;
}