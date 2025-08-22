package com.zsj.aiinterview.entity;

import com.baomidou.mybatisplus.annotation.*;
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
@TableName("answer_records")
public class AnswerRecord {
    /**
     * 答题记录主键ID，唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID，关联用户表
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 题目ID，关联题目表
     */
    @TableField("question_id")
    private Long questionId;
    
    /**
     * 用户提交的答案
     */
    @TableField("user_answer")
    private String userAnswer;
    
    /**
     * 得分，0-100分
     */
    private Integer score;
    
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
     * 答题耗时，单位：秒
     */
    @TableField("time_spent")
    private Integer timeSpent;
    
    /**
     * 尝试次数
     */
    @TableField("attempt_count")
    private Integer attemptCount;
    
    /**
     * 答题状态：in_progress(答题中)、completed(已完成)、skipped(已跳过)
     */
    private String status;
    
    /**
     * AI评价结果，JSON格式存储详细评价信息
     */
    @TableField("ai_evaluation")
    private String aiEvaluation;
    
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
        // 移除手动设置时间，让MyBatis Plus自动填充
    }
}