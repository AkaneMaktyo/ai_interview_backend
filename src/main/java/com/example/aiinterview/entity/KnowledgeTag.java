package com.example.aiinterview.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 知识点标签实体类
 */
@TableName(value = "knowledge_tags", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = false)
public class KnowledgeTag {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "name")
    private String name;

    @TableField(value = "description")
    private String description;

    @TableField(value = "category")
    private String category = "technical";

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "is_active")
    private Boolean isActive = true;
}