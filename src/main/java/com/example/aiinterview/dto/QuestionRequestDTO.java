package com.example.aiinterview.dto;

import lombok.Data;
import java.util.List;

/**
 * 面试题目请求DTO
 */
@Data
public class QuestionRequestDTO {
    /** 题目索引 */
    private Integer questionIndex;
    /** 面试类型 */
    private String interviewType;
    /** 难度等级 */
    private String difficulty;
    /** 职位方向 */
    private String position;
    /** 工作经验 */
    private String experience;
    /** 历史记录 */
    private List<InterviewHistoryDTO> history;
}