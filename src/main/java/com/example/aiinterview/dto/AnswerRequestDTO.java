package com.example.aiinterview.dto;

import lombok.Data;

/**
 * 回答提交请求DTO
 */
@Data
public class AnswerRequestDTO {
    /** 问题ID */
    private String questionId;
    /** 问题内容 */
    private String question;
    /** 回答内容 */
    private String answer;
    /** 问题索引 */
    private Integer questionIndex;
    /** 面试类型 */
    private String interviewType;
    /** 难度等级 */
    private String difficulty;
    /** 职位方向 */
    private String position;
}