package com.zsj.aiinterview.dto;

import lombok.Data;

/**
 * 面试历史记录DTO
 */
@Data
public class InterviewHistoryDTO {
    /** 问题ID */
    private String questionId;
    /** 问题内容 */
    private String question;
    /** 回答内容 */
    private String answer;
    /** 时间戳 */
    private String timestamp;
    /** AI反馈 */
    private FeedbackDTO feedback;
}