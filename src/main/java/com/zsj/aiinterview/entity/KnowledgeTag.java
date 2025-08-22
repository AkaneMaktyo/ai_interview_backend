package com.zsj.aiinterview.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 知识点标签实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("knowledge_tags")
public class KnowledgeTag {
    /**
     * 知识点标签主键ID，唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 标签名称，如"Java基础"、"算法"、"数据结构"等
     */
    private String name;
    
    /**
     * 标签描述，详细说明该知识点的内容
     */
    private String description;
    
    /**
     * 标签分类：technical(技术类)、soft_skill(软技能)、industry(行业知识)等
     */
    private String category;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 是否激活状态：true为可用，false为禁用
     */
    @TableField("is_active")
    private Boolean isActive;
    
    // 构造函数用于创建新标签
    public KnowledgeTag(String name, String description, String category) {
        this.name = name;
        this.description = description;
        this.category = category != null ? category : "technical";
        this.isActive = true;
        // 移除手动设置时间，让MyBatis Plus自动填充
    }
}