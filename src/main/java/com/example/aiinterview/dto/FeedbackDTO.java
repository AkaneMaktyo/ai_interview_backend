package com.example.aiinterview.dto;

import lombok.Data;
import java.util.List;

/**
 * AI反馈DTO
 */
@Data
public class FeedbackDTO {
    /** 评分 (1-10) */
    private Integer score;
    /** 评价内容 */
    private String comment;
    /** 改进建议 */
    private List<String> suggestions;
}