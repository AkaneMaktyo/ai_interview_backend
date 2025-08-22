package com.example.aiinterview.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 答题记录实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRecord {
    private Long id;
    private Long userId;
    private Long questionId;
    private String userAnswer;
    private Integer score;
    private String interviewType;
    private String difficulty;
    private String position;
    private Integer timeSpent;
    private Integer attemptCount;
    private String status;
    private String aiEvaluation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造函数用于创建新答题记录
    public AnswerRecord(Long userId, Long questionId, String userAnswer, Integer score,
                       String interviewType, String difficulty, String position, String aiEvaluation) {
        this.userId = userId;
        this.questionId = questionId;
        this.userAnswer = userAnswer;
        this.score = score;
        this.interviewType = interviewType;
        this.difficulty = difficulty;
        this.position = position;
        this.aiEvaluation = aiEvaluation;
        this.attemptCount = 1;
        this.status = "completed";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}