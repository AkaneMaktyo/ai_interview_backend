package com.example.aiinterview.entity;

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
public class KnowledgeTag {
    private Long id;
    private String name;
    private String description;
    private String category;
    private LocalDateTime createdAt;
    private Boolean isActive;
    
    // 构造函数用于创建新标签
    public KnowledgeTag(String name, String description, String category) {
        this.name = name;
        this.description = description;
        this.category = category != null ? category : "technical";
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }
}