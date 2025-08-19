package com.example.aiinterview.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aiinterview.dto.AnswerRequestDTO;
import com.example.aiinterview.dto.FeedbackDTO;
import com.example.aiinterview.entity.AnswerRecord;
import com.example.aiinterview.entity.Question;
import com.example.aiinterview.repository.AnswerRecordRepository;
import com.example.aiinterview.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 答题记录服务
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AnswerRecordService {

    private final AnswerRecordRepository answerRecordRepository;
    private final QuestionRepository questionRepository;
    private final WrongQuestionService wrongQuestionService;

    /**
     * 保存答题记录
     */
    public AnswerRecord saveAnswerRecord(AnswerRequestDTO request, FeedbackDTO feedback, Long userId) {
        try {
            AnswerRecord record = new AnswerRecord();
            record.setUserId(userId);
            
            // 处理questionId，如果是字符串格式需要提取数字部分或创建新记录
            Long questionId = parseQuestionId(request.getQuestionId());
            record.setQuestionId(questionId);
            
            record.setUserAnswer(request.getAnswer());
            record.setScore(feedback.getScore());
            record.setInterviewType(request.getInterviewType());
            record.setDifficulty(request.getDifficulty());
            record.setPosition(request.getPosition());
            record.setStatus("completed");
            
            // 将FeedbackDTO转换为Map存储
            Map<String, Object> aiEvaluationMap = new HashMap<>();
            aiEvaluationMap.put("score", feedback.getScore());
            aiEvaluationMap.put("comment", feedback.getComment());
            aiEvaluationMap.put("suggestions", feedback.getSuggestions());
            record.setAiEvaluation(aiEvaluationMap);
            
            // 计算答题次数
            QueryWrapper<AnswerRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                       .eq("question_id", questionId);
            long attemptCount = answerRecordRepository.selectCount(queryWrapper) + 1;
            record.setAttemptCount((int) attemptCount);
            
            answerRecordRepository.insert(record);
            
            // 如果分数较低，加入错题集
            if (feedback.getScore() != null && feedback.getScore() < 7) {
                wrongQuestionService.addToWrongQuestions(userId, questionId, record.getId());
            }
            
            log.info("保存答题记录成功: 用户ID={}, 题目ID={}, 分数={}", userId, questionId, feedback.getScore());
            return record;
            
        } catch (Exception e) {
            log.error("保存答题记录失败", e);
            throw new RuntimeException("保存答题记录失败: " + e.getMessage());
        }
    }

    /**
     * 解析questionId，支持字符串和数字格式
     */
    private Long parseQuestionId(String questionId) {
        try {
            // 如果是纯数字，直接解析
            return Long.valueOf(questionId);
        } catch (NumberFormatException e) {
            // 如果是 "ai_" 开头的ID，提取时间戳部分
            if (questionId != null && questionId.startsWith("ai_")) {
                try {
                    return Long.valueOf(questionId.substring(3));
                } catch (NumberFormatException ex) {
                    // 如果提取失败，使用当前时间戳
                    return System.currentTimeMillis();
                }
            } else {
                // 其他情况使用当前时间戳
                return System.currentTimeMillis();
            }
        }
    }

    /**
     * 获取用户答题历史（分页）
     */
    public Page<AnswerRecord> getUserAnswerHistory(Long userId, int page, int size) {
        Page<AnswerRecord> pageParam = new Page<>(page + 1, size);
        return answerRecordRepository.findByUserIdOrderByCreatedAtDesc(pageParam, userId);
    }

    /**
     * 获取用户答题历史（带题目信息）
     */
    public Page<AnswerRecord> getUserAnswerHistoryWithQuestions(Long userId, int page, int size) {
        Page<AnswerRecord> recordPage = getUserAnswerHistory(userId, page, size);
        
        // 填充题目信息
        for (AnswerRecord record : recordPage.getRecords()) {
            Question question = questionRepository.selectById(record.getQuestionId());
            record.setQuestion(question);
        }
        
        return recordPage;
    }

    /**
     * 获取用户学习统计
     */
    public Map<String, Object> getUserLearningStats(Long userId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> stats = new HashMap<>();
        
        QueryWrapper<AnswerRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        
        if (startDate != null) {
            queryWrapper.ge("created_at", startDate.atStartOfDay());
        }
        if (endDate != null) {
            queryWrapper.le("created_at", endDate.atTime(23, 59, 59));
        }
        
        List<AnswerRecord> records = answerRecordRepository.selectList(queryWrapper);
        
        // 计算统计数据
        int totalQuestions = records.size();
        int correctQuestions = (int) records.stream()
            .filter(r -> r.getScore() != null && r.getScore() >= 7)
            .count();
        double accuracy = totalQuestions > 0 ? (double) correctQuestions / totalQuestions * 100 : 0;
        double averageScore = records.stream()
            .filter(r -> r.getScore() != null)
            .mapToInt(AnswerRecord::getScore)
            .average()
            .orElse(0);
        int totalTimeSpent = records.stream()
            .filter(r -> r.getTimeSpent() != null)
            .mapToInt(AnswerRecord::getTimeSpent)
            .sum();
        
        stats.put("totalQuestions", totalQuestions);
        stats.put("correctQuestions", correctQuestions);
        stats.put("accuracy", Math.round(accuracy * 100.0) / 100.0);
        stats.put("averageScore", Math.round(averageScore * 100.0) / 100.0);
        stats.put("totalTimeSpent", totalTimeSpent);
        
        return stats;
    }

    /**
     * 获取用户日常学习统计
     */
    public List<Object[]> getUserDailyStats(Long userId, int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        return answerRecordRepository.getDailyStats(userId, startDate);
    }

    /**
     * 获取用户按难度分组的统计
     */
    public List<Object[]> getUserStatsByDifficulty(Long userId) {
        return answerRecordRepository.getUserStatsByDifficulty(userId);
    }

    /**
     * 获取用户按类型分组的统计
     */
    public List<Object[]> getUserStatsByType(Long userId) {
        return answerRecordRepository.getUserStatsByType(userId);
    }

    /**
     * 获取用户错题记录
     */
    public List<AnswerRecord> getUserWrongAnswers(Long userId) {
        return answerRecordRepository.findWrongAnswersByUserId(userId, 6); // 6分以下算错题
    }

    /**
     * 检查用户是否已答过某题目
     */
    public boolean hasUserAnsweredQuestion(Long userId, Long questionId) {
        QueryWrapper<AnswerRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("question_id", questionId);
        return answerRecordRepository.selectCount(queryWrapper) > 0;
    }

    /**
     * 获取用户某题目的最新记录
     */
    public AnswerRecord getUserLatestAnswer(Long userId, Long questionId) {
        QueryWrapper<AnswerRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("question_id", questionId)
                   .orderByDesc("created_at")
                   .last("LIMIT 1");
        return answerRecordRepository.selectOne(queryWrapper);
    }

    /**
     * 获取题目的平均分
     */
    public Double getQuestionAverageScore(Long questionId) {
        return answerRecordRepository.getAverageScoreByQuestionId(questionId);
    }

    /**
     * 根据ID获取答题记录详情
     */
    public AnswerRecord getAnswerRecordById(Long recordId) {
        AnswerRecord record = answerRecordRepository.selectById(recordId);
        if (record != null) {
            // 填充题目信息
            Question question = questionRepository.selectById(record.getQuestionId());
            record.setQuestion(question);
        }
        return record;
    }
}