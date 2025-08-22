package com.example.aiinterview.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 面试会话实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewSession {
    private Long id;
    private Long userId;
    private String sessionName;
    private String interviewType;
    private String difficulty;
    private String position;
    private String status;
    private Integer totalQuestions;
    private Integer answeredQuestions;
    private Integer totalScore;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String sessionData;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造函数用于创建新面试会话
    public InterviewSession(Long userId, String sessionName, String interviewType, 
                           String difficulty, String position) {
        this.userId = userId;
        this.sessionName = sessionName;
        this.interviewType = interviewType;
        this.difficulty = difficulty;
        this.position = position;
        this.status = "active";
        this.totalQuestions = 0;
        this.answeredQuestions = 0;
        this.totalScore = 0;
        this.startedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}