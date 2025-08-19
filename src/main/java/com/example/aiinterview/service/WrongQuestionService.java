package com.example.aiinterview.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.aiinterview.entity.WrongQuestion;
import com.example.aiinterview.entity.Question;
import com.example.aiinterview.entity.AnswerRecord;
import com.example.aiinterview.repository.WrongQuestionRepository;
import com.example.aiinterview.repository.QuestionRepository;
import com.example.aiinterview.repository.AnswerRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 错题集服务
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class WrongQuestionService {

    private final WrongQuestionRepository wrongQuestionRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRecordRepository answerRecordRepository;

    /**
     * 添加到错题集
     */
    public void addToWrongQuestions(Long userId, Long questionId, Long firstAttemptRecordId) {
        try {
            // 检查是否已存在
            QueryWrapper<WrongQuestion> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                       .eq("question_id", questionId);
            WrongQuestion existing = wrongQuestionRepository.selectOne(queryWrapper);
            
            if (existing == null) {
                // 创建新的错题记录
                WrongQuestion wrongQuestion = new WrongQuestion();
                wrongQuestion.setUserId(userId);
                wrongQuestion.setQuestionId(questionId);
                wrongQuestion.setFirstAttemptRecordId(firstAttemptRecordId);
                wrongQuestion.setRetryCount(0);
                wrongQuestion.setMastered(false);
                
                wrongQuestionRepository.insert(wrongQuestion);
                log.info("添加错题成功: 用户ID={}, 题目ID={}", userId, questionId);
            } else if (existing.getMastered()) {
                // 如果之前已掌握，但又答错了，重新标记为未掌握
                existing.setMastered(false);
                existing.setMasteredAt(null);
                existing.setRetryCount(0);
                wrongQuestionRepository.updateById(existing);
                log.info("重新标记为错题: 用户ID={}, 题目ID={}", userId, questionId);
            }
        } catch (Exception e) {
            log.error("添加错题失败", e);
            throw new RuntimeException("添加错题失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户错题集
     */
    public List<WrongQuestion> getUserWrongQuestions(Long userId, boolean includeMastered) {
        QueryWrapper<WrongQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        
        if (!includeMastered) {
            queryWrapper.eq("mastered", false);
        }
        
        queryWrapper.orderByDesc("created_at");
        
        List<WrongQuestion> wrongQuestions = wrongQuestionRepository.selectList(queryWrapper);
        
        // 填充题目信息
        for (WrongQuestion wrongQuestion : wrongQuestions) {
            Question question = questionRepository.selectById(wrongQuestion.getQuestionId());
            wrongQuestion.setQuestion(question);
            
            // 填充首次答题记录
            if (wrongQuestion.getFirstAttemptRecordId() != null) {
                AnswerRecord firstAttempt = answerRecordRepository.selectById(wrongQuestion.getFirstAttemptRecordId());
                wrongQuestion.setFirstAttemptRecord(firstAttempt);
            }
        }
        
        return wrongQuestions;
    }

    /**
     * 获取用户未掌握的错题
     */
    public List<WrongQuestion> getUserUnmasteredWrongQuestions(Long userId) {
        return getUserWrongQuestions(userId, false);
    }

    /**
     * 标记错题为已掌握
     */
    public void markAsMastered(Long userId, Long questionId) {
        try {
            QueryWrapper<WrongQuestion> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                       .eq("question_id", questionId);
            WrongQuestion wrongQuestion = wrongQuestionRepository.selectOne(queryWrapper);
            
            if (wrongQuestion != null && !wrongQuestion.getMastered()) {
                wrongQuestion.setMastered(true);
                wrongQuestion.setMasteredAt(LocalDateTime.now());
                wrongQuestionRepository.updateById(wrongQuestion);
                log.info("标记错题为已掌握: 用户ID={}, 题目ID={}", userId, questionId);
            }
        } catch (Exception e) {
            log.error("标记错题为已掌握失败", e);
            throw new RuntimeException("标记错题为已掌握失败: " + e.getMessage());
        }
    }

    /**
     * 错题重做
     */
    public void retryWrongQuestion(Long userId, Long questionId) {
        try {
            QueryWrapper<WrongQuestion> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                       .eq("question_id", questionId);
            WrongQuestion wrongQuestion = wrongQuestionRepository.selectOne(queryWrapper);
            
            if (wrongQuestion != null) {
                wrongQuestion.setRetryCount(wrongQuestion.getRetryCount() + 1);
                wrongQuestion.setLastRetryAt(LocalDateTime.now());
                wrongQuestionRepository.updateById(wrongQuestion);
                log.info("错题重做: 用户ID={}, 题目ID={}, 重做次数={}", userId, questionId, wrongQuestion.getRetryCount());
            }
        } catch (Exception e) {
            log.error("错题重做失败", e);
            throw new RuntimeException("错题重做失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户错题统计
     */
    public Map<String, Object> getUserWrongQuestionStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 总错题数
        QueryWrapper<WrongQuestion> totalWrapper = new QueryWrapper<>();
        totalWrapper.eq("user_id", userId);
        long totalWrongQuestions = wrongQuestionRepository.selectCount(totalWrapper);
        stats.put("totalWrongQuestions", totalWrongQuestions);
        
        // 未掌握错题数
        QueryWrapper<WrongQuestion> unmasteredWrapper = new QueryWrapper<>();
        unmasteredWrapper.eq("user_id", userId)
                        .eq("mastered", false);
        long unmasteredCount = wrongQuestionRepository.selectCount(unmasteredWrapper);
        stats.put("unmasteredCount", unmasteredCount);
        
        // 已掌握错题数
        long masteredCount = totalWrongQuestions - unmasteredCount;
        stats.put("masteredCount", masteredCount);
        
        // 掌握率
        double masteryRate = totalWrongQuestions > 0 ? (double) masteredCount / totalWrongQuestions * 100 : 0;
        stats.put("masteryRate", Math.round(masteryRate * 100.0) / 100.0);
        
        // 错题按知识点分组统计
        List<Object[]> tagStats = wrongQuestionRepository.getWrongQuestionTagStats(userId);
        stats.put("tagStats", tagStats);
        
        // 错题按难度分组统计
        List<Object[]> difficultyStats = wrongQuestionRepository.getWrongQuestionsByDifficulty(userId);
        stats.put("difficultyStats", difficultyStats);
        
        // 错题按类型分组统计
        List<Object[]> typeStats = wrongQuestionRepository.getWrongQuestionsByType(userId);
        stats.put("typeStats", typeStats);
        
        return stats;
    }

    /**
     * 获取最需要复习的错题
     */
    public List<WrongQuestion> getMostNeedReviewQuestions(Long userId, int limit) {
        List<WrongQuestion> needReview = wrongQuestionRepository.findMostNeedReviewQuestions(userId);
        
        // 填充题目信息
        for (WrongQuestion wrongQuestion : needReview) {
            Question question = questionRepository.selectById(wrongQuestion.getQuestionId());
            wrongQuestion.setQuestion(question);
        }
        
        return needReview.stream()
            .limit(limit)
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 删除错题记录
     */
    public void removeFromWrongQuestions(Long userId, Long questionId) {
        try {
            QueryWrapper<WrongQuestion> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                       .eq("question_id", questionId);
            wrongQuestionRepository.delete(queryWrapper);
            log.info("删除错题记录: 用户ID={}, 题目ID={}", userId, questionId);
        } catch (Exception e) {
            log.error("删除错题记录失败", e);
            throw new RuntimeException("删除错题记录失败: " + e.getMessage());
        }
    }

    /**
     * 检查是否在错题集中
     */
    public boolean isInWrongQuestions(Long userId, Long questionId) {
        QueryWrapper<WrongQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("question_id", questionId)
                   .eq("mastered", false);
        return wrongQuestionRepository.selectCount(queryWrapper) > 0;
    }
}