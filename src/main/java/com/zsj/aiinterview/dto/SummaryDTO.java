package com.zsj.aiinterview.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 面试总结DTO
 */
@Data
public class SummaryDTO {
    /** 总体评分 (1-10) */
    private Integer overallScore;
    /** 总结评价 */
    private String overallComment;
    /** 各项能力评分 */
    private Map<String, Integer> skillScores;
    /** 优势分析 */
    private List<String> strengths;
    /** 不足之处 */
    private List<String> weaknesses;
    /** 改进建议 */
    private List<String> recommendations;
    /** 面试总时长（分钟） */
    private Integer totalDuration;
    /** 回答题目数量 */
    private Integer answeredQuestions;
}