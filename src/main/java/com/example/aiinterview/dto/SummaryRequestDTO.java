package com.example.aiinterview.dto;

import lombok.Data;
import java.util.List;

/**
 * 面试总结请求DTO
 */
@Data
public class SummaryRequestDTO {
    /** 面试历史记录 */
    private List<InterviewHistoryDTO> history;
    /** 总题目数量 */
    private Integer totalQuestions;
    /** 面试持续时间（分钟） */
    private Integer interviewDuration;
    /** 面试类型 */
    private String interviewType;
    /** 难度等级 */
    private String difficulty;
    /** 职位方向 */
    private String position;
    /** 工作经验 */
    private String experience;
}