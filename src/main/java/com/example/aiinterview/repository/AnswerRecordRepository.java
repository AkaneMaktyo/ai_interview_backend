package com.example.aiinterview.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aiinterview.entity.AnswerRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 答题记录数据访问层
 */
@Mapper
public interface AnswerRecordRepository extends BaseMapper<AnswerRecord> {

    /**
     * 用户答题历史（分页）
     */
    @Select("SELECT * FROM answer_records WHERE user_id = #{userId} ORDER BY created_at DESC")
    Page<AnswerRecord> findByUserIdOrderByCreatedAtDesc(Page<AnswerRecord> page, @Param("userId") Long userId);

    /**
     * 用户答题历史（按时间范围）
     */
    @Select("SELECT * FROM answer_records WHERE user_id = #{userId} " +
            "AND created_at BETWEEN #{startTime} AND #{endTime} " +
            "ORDER BY created_at DESC")
    List<AnswerRecord> findByUserIdAndTimeRange(@Param("userId") Long userId,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);

    /**
     * 用户错题记录（低分题目）
     */
    @Select("SELECT * FROM answer_records WHERE user_id = #{userId} " +
            "AND score <= #{maxScore} ORDER BY created_at DESC")
    List<AnswerRecord> findWrongAnswersByUserId(@Param("userId") Long userId,
                                               @Param("maxScore") Integer maxScore);

    /**
     * 用户某题目的最新记录
     */
    @Select("SELECT * FROM answer_records WHERE user_id = #{userId} AND question_id = #{questionId} " +
            "ORDER BY created_at DESC LIMIT 1")
    AnswerRecord findTopByUserIdAndQuestionIdOrderByCreatedAtDesc(@Param("userId") Long userId, 
                                                                 @Param("questionId") Long questionId);

    /**
     * 用户学习统计
     */
    @Select("SELECT COUNT(*), AVG(score), SUM(time_spent) " +
            "FROM answer_records WHERE user_id = #{userId} " +
            "AND created_at >= #{startDate}")
    Object[] getUserLearningStats(@Param("userId") Long userId,
                                 @Param("startDate") LocalDateTime startDate);

    /**
     * 题目平均分
     */
    @Select("SELECT AVG(score) FROM answer_records WHERE question_id = #{questionId}")
    Double getAverageScoreByQuestionId(@Param("questionId") Long questionId);

    /**
     * 用户已答题目ID列表
     */
    @Select("SELECT DISTINCT question_id FROM answer_records WHERE user_id = #{userId}")
    List<Long> findAnsweredQuestionIdsByUserId(@Param("userId") Long userId);

    /**
     * 用户按类型统计
     */
    @Select("SELECT interview_type, COUNT(*), AVG(score) " +
            "FROM answer_records WHERE user_id = #{userId} " +
            "GROUP BY interview_type")
    List<Object[]> getUserStatsByType(@Param("userId") Long userId);

    /**
     * 用户按难度统计
     */
    @Select("SELECT difficulty, COUNT(*), AVG(score) " +
            "FROM answer_records WHERE user_id = #{userId} " +
            "GROUP BY difficulty")
    List<Object[]> getUserStatsByDifficulty(@Param("userId") Long userId);

    /**
     * 按日期统计用户答题情况
     */
    @Select("SELECT DATE(created_at) as date, " +
            "COUNT(*) as question_count, " +
            "AVG(score) as avg_score, " +
            "SUM(time_spent) as total_time " +
            "FROM answer_records " +
            "WHERE user_id = #{userId} " +
            "AND created_at >= #{startDate} " +
            "GROUP BY DATE(created_at) " +
            "ORDER BY date")
    List<Object[]> getDailyStats(@Param("userId") Long userId,
                                @Param("startDate") LocalDateTime startDate);

    /**
     * 用户答题总数
     */
    @Select("SELECT COUNT(*) FROM answer_records WHERE user_id = #{userId}")
    long countByUserId(@Param("userId") Long userId);

    /**
     * 用户正确题目总数（基于分数阈值）
     */
    @Select("SELECT COUNT(*) FROM answer_records WHERE user_id = #{userId} AND score >= #{minScore}")
    long countCorrectAnswersByUserId(@Param("userId") Long userId, @Param("minScore") Integer minScore);

    /**
     * 按状态查询
     */
    @Select("SELECT * FROM answer_records WHERE user_id = #{userId} AND status = #{status}")
    List<AnswerRecord> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);
}