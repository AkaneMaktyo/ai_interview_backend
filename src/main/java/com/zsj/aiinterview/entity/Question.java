package com.zsj.aiinterview.entity;

import com.baomidou.mybatisplus.annotation.*;
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
@TableName("questions")
public class Question {
    /**
     * 题目主键ID，唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 题目标题
     */
    private String title;
    
    /**
     * 题目详细内容/描述
     */
    private String content;
    
    /**
     * 题目类型：technical(技术题)、behavioral(行为题)、algorithm(算法题)、system_design(系统设计)
     */
    @TableField("question_type")
    private String questionType;
    
    /**
     * 难度等级：easy(简单)、medium(中等)、hard(困难)
     */
    private String difficulty;
    
    /**
     * 适用岗位：如java_developer、python_developer、frontend_developer等
     */
    private String position;
    
    /**
     * 知识点标签，多个标签用逗号分隔
     */
    private String tags;
    
    /**
     * 期望答案/参考答案
     */
    @TableField("expected_answer")
    private String expectedAnswer;
    
    /**
     * 评价标准，用于AI评分
     */
    @TableField("evaluation_criteria")
    private String evaluationCriteria;
    
    /**
     * 提示信息，帮助用户思考
     */
    private String hints;
    
    /**
     * 是否由AI生成：true为AI生成，false为人工录入
     */
    @TableField("ai_generated")
    private Boolean aiGenerated;
    
    /**
     * AI生成时使用的提示词
     */
    @TableField("ai_prompt")
    private String aiPrompt;
    
    /**
     * 创建者用户ID
     */
    @TableField("created_by")
    private Long createdBy;
    
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
    
    /**
     * 是否激活状态：true为可用，false为禁用
     */
    @TableField("is_active")
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
        // 移除手动设置时间，让MyBatis Plus自动填充
    }
}