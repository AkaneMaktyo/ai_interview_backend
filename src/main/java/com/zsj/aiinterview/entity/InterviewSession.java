package com.zsj.aiinterview.entity;

import com.baomidou.mybatisplus.annotation.*;
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
@TableName("interview_sessions")
public class InterviewSession {
    /**
     * 面试会话主键ID，唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID，关联用户表
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 面试会话名称
     */
    @TableField("session_name")
    private String sessionName;
    
    /**
     * 面试类型：technical(技术面试)、behavioral(行为面试)、comprehensive(综合面试)
     */
    @TableField("interview_type")
    private String interviewType;
    
    /**
     * 难度等级：easy(简单)、medium(中等)、hard(困难)
     */
    private String difficulty;
    
    /**
     * 面试岗位：如java_developer、python_developer、frontend_developer等
     */
    private String position;
    
    /**
     * 面试状态：active(进行中)、completed(已完成)、paused(暂停)、cancelled(已取消)
     */
    private String status;
    
    /**
     * 总题目数量
     */
    @TableField("total_questions")
    private Integer totalQuestions;
    
    /**
     * 已回答题目数量
     */
    @TableField("answered_questions")
    private Integer answeredQuestions;
    
    /**
     * 总得分
     */
    @TableField("total_score")
    private Integer totalScore;
    
    /**
     * 面试开始时间
     */
    @TableField("started_at")
    private LocalDateTime startedAt;
    
    /**
     * 面试结束时间
     */
    @TableField("ended_at")
    private LocalDateTime endedAt;
    
    /**
     * 面试会话数据，存储JSON格式的扩展信息
     */
    @TableField("session_data")
    private String sessionData;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 最后更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
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
        // 移除手动设置时间，让MyBatis Plus自动填充
    }
}