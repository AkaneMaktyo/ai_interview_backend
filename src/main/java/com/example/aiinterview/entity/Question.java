package com.example.aiinterview.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 题目实体类
 */
@TableName(value = "questions", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = false)
public class Question {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("question_type")
    private String questionType;

    @TableField("difficulty")
    private String difficulty;

    @TableField("position")
    private String position;

    @TableField(value = "tags", typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    @TableField("expected_answer")
    private String expectedAnswer;

    @TableField(value = "evaluation_criteria", typeHandler = JacksonTypeHandler.class)
    private List<String> evaluationCriteria;

    @TableField(value = "hints", typeHandler = JacksonTypeHandler.class)
    private List<String> hints;

    @TableField("ai_generated")
    private Boolean aiGenerated = true;

    @TableField("ai_prompt")
    private String aiPrompt;

    @TableField("created_by")
    private Long createdBy;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField("is_active")
    private Boolean isActive = true;
}