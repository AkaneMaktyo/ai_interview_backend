package com.example.aiinterview.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 题目实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    private Long id;
    private String title;
    private String content;
    private String questionType;
    private String difficulty;
    private String position;
    private String tags;
    private String expectedAnswer;
    private String evaluationCriteria;
    private String hints;
    private Boolean aiGenerated;
    private String aiPrompt;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
    
    // 构造函数用于创建新题目
    public Question(String title, String content, String questionType, 
                   String difficulty, String position) {
        this.title = title;
        this.content = content;
        this.questionType = questionType;
        this.difficulty = difficulty;
        this.position = position;
        this.aiGenerated = true;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}