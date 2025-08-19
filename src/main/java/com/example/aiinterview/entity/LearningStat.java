package com.example.aiinterview.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.util.List;

/**
 * 学习统计实体类
 */
@Entity
@Table(name = "learning_stats")
@Data
@EqualsAndHashCode(callSuper = false)
public class LearningStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;

    @Column(name = "questions_attempted")
    private Integer questionsAttempted = 0;

    @Column(name = "questions_correct")
    private Integer questionsCorrect = 0;

    @Column(name = "total_score")
    private Integer totalScore = 0;

    @Column(name = "time_spent")
    private Integer timeSpent = 0;

    @Type(JsonType.class)
    @Column(name = "tags_practiced", columnDefinition = "jsonb")
    private List<String> tagsPracticed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}