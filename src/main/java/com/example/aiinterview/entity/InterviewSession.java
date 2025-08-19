package com.example.aiinterview.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 面试会话实体类
 */
@Entity
@Table(name = "interview_sessions")
@Data
@EqualsAndHashCode(callSuper = false)
public class InterviewSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "session_name", length = 100)
    private String sessionName;

    @Column(name = "interview_type", nullable = false, length = 20)
    private String interviewType;

    @Column(nullable = false, length = 10)
    private String difficulty;

    @Column(nullable = false, length = 20)
    private String position;

    @Column(nullable = false, length = 20)
    private String experience;

    @Column(name = "total_questions")
    private Integer totalQuestions = 0;

    @Column(name = "completed_questions")
    private Integer completedQuestions = 0;

    @Column(name = "overall_score", precision = 4, scale = 2)
    private BigDecimal overallScore;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(length = 20)
    private String status = "in_progress";

    @CreationTimestamp
    @Column(name = "started_at", updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> summary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}