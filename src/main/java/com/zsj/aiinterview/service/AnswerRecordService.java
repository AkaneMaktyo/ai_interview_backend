package com.zsj.aiinterview.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsj.aiinterview.entity.AnswerRecord;
import com.zsj.aiinterview.mapper.AnswerRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 答题记录服务
 */
@Service
@RequiredArgsConstructor
public class AnswerRecordService extends ServiceImpl<AnswerRecordMapper, AnswerRecord> {

    /**
     * 根据用户ID查询答题记录
     */
    public List<AnswerRecord> findByUserId(Long userId) {
        return list(new LambdaQueryWrapper<AnswerRecord>()
                .eq(AnswerRecord::getUserId, userId)
                .orderByDesc(AnswerRecord::getCreateTime));
    }

    /**
     * 根据用户ID和题目ID查询答题记录
     */
    public List<AnswerRecord> findByUserIdAndQuestionId(Long userId, Long questionId) {
        return list(new LambdaQueryWrapper<AnswerRecord>()
                .eq(AnswerRecord::getUserId, userId)
                .eq(AnswerRecord::getQuestionId, questionId)
                .orderByDesc(AnswerRecord::getCreateTime));
    }

    /**
     * 获取用户统计信息
     */
    public Map<String, Object> getUserStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 总答题数
        long totalCount = count(new LambdaQueryWrapper<AnswerRecord>()
                .eq(AnswerRecord::getUserId, userId));
        
        // 已完成答题数
        long completedCount = count(new LambdaQueryWrapper<AnswerRecord>()
                .eq(AnswerRecord::getUserId, userId)
                .eq(AnswerRecord::getStatus, "completed"));
        
        // 平均分数
        List<AnswerRecord> records = findByUserId(userId);
        double avgScore = records.stream()
                .filter(r -> r.getScore() != null)
                .mapToInt(AnswerRecord::getScore)
                .average()
                .orElse(0.0);
        
        // 按难度统计
        Map<String, Long> difficultyStats = new HashMap<>();
        difficultyStats.put("easy", count(new LambdaQueryWrapper<AnswerRecord>()
                .eq(AnswerRecord::getUserId, userId)
                .eq(AnswerRecord::getDifficulty, "easy")));
        difficultyStats.put("medium", count(new LambdaQueryWrapper<AnswerRecord>()
                .eq(AnswerRecord::getUserId, userId)
                .eq(AnswerRecord::getDifficulty, "medium")));
        difficultyStats.put("hard", count(new LambdaQueryWrapper<AnswerRecord>()
                .eq(AnswerRecord::getUserId, userId)
                .eq(AnswerRecord::getDifficulty, "hard")));
        
        // 按面试类型统计
        Map<String, Long> typeStats = new HashMap<>();
        typeStats.put("technical", count(new LambdaQueryWrapper<AnswerRecord>()
                .eq(AnswerRecord::getUserId, userId)
                .eq(AnswerRecord::getInterviewType, "technical")));
        typeStats.put("behavioral", count(new LambdaQueryWrapper<AnswerRecord>()
                .eq(AnswerRecord::getUserId, userId)
                .eq(AnswerRecord::getInterviewType, "behavioral")));
        typeStats.put("comprehensive", count(new LambdaQueryWrapper<AnswerRecord>()
                .eq(AnswerRecord::getUserId, userId)
                .eq(AnswerRecord::getInterviewType, "comprehensive")));
        
        stats.put("totalCount", totalCount);
        stats.put("completedCount", completedCount);
        stats.put("avgScore", Math.round(avgScore * 100.0) / 100.0);
        stats.put("difficultyStats", difficultyStats);
        stats.put("typeStats", typeStats);
        
        return stats;
    }

    /**
     * 更新答题状态和分数
     */
    public boolean updateAnswerRecord(Long id, String userAnswer, Integer score, 
                                    Integer timeSpent, String aiEvaluation) {
        AnswerRecord record = getById(id);
        if (record == null) {
            return false;
        }
        
        record.setUserAnswer(userAnswer);
        record.setScore(score);
        record.setTimeSpent(timeSpent);
        record.setAiEvaluation(aiEvaluation);
        record.setStatus("completed");
        
        return updateById(record);
    }

    /**
     * 创建答题记录
     */
    public AnswerRecord createRecord(Long userId, Long questionId, String interviewType, 
                                   String difficulty, String position) {
        AnswerRecord record = new AnswerRecord();
        record.setUserId(userId);
        record.setQuestionId(questionId);
        record.setInterviewType(interviewType);
        record.setDifficulty(difficulty);
        record.setPosition(position);
        record.setStatus("in_progress");
        record.setAttemptCount(1);
        
        save(record);
        return record;
    }
}