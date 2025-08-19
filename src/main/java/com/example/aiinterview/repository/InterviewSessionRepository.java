package com.example.aiinterview.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aiinterview.entity.InterviewSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 面试会话数据访问层
 */
@Mapper
public interface InterviewSessionRepository extends BaseMapper<InterviewSession> {

    /**
     * 用户面试会话历史（分页）
     */
    @Select("SELECT * FROM interview_sessions WHERE user_id = #{userId} ORDER BY started_at DESC")
    Page<InterviewSession> findByUserIdOrderByStartedAtDesc(Page<InterviewSession> page, @Param("userId") Long userId);

    /**
     * 用户进行中的面试会话
     */
    @Select("SELECT * FROM interview_sessions WHERE user_id = #{userId} AND status = #{status}")
    List<InterviewSession> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    /**
     * 用户已完成的面试会话
     */
    @Select("SELECT * FROM interview_sessions WHERE user_id = #{userId} AND status = #{status} ORDER BY ended_at DESC")
    List<InterviewSession> findByUserIdAndStatusOrderByEndedAtDesc(@Param("userId") Long userId, @Param("status") String status);

    /**
     * 用户面试会话统计
     */
    @Select("SELECT COUNT(*), AVG(total_score), AVG(CAST(EXTRACT(EPOCH FROM (ended_at - started_at))/60 AS BIGINT)) " +
            "FROM interview_sessions WHERE user_id = #{userId} AND status = 'completed'")
    Object[] getUserInterviewStats(@Param("userId") Long userId);

    /**
     * 按类型统计面试会话
     */
    @Select("SELECT interview_type, COUNT(*), AVG(total_score) " +
            "FROM interview_sessions WHERE user_id = #{userId} AND status = 'completed' " +
            "GROUP BY interview_type")
    List<Object[]> getUserInterviewStatsByType(@Param("userId") Long userId);

    /**
     * 按难度统计面试会话
     */
    @Select("SELECT difficulty, COUNT(*), AVG(total_score) " +
            "FROM interview_sessions WHERE user_id = #{userId} AND status = 'completed' " +
            "GROUP BY difficulty")
    List<Object[]> getUserInterviewStatsByDifficulty(@Param("userId") Long userId);

    /**
     * 最近面试会话
     */
    @Select("SELECT * FROM interview_sessions WHERE user_id = #{userId} " +
            "AND started_at >= #{startTime} ORDER BY started_at DESC")
    List<InterviewSession> findRecentSessions(@Param("userId") Long userId,
                                            @Param("startTime") LocalDateTime startTime);

    /**
     * 清理超时的进行中会话
     */
    @Select("SELECT * FROM interview_sessions WHERE status = 'active' " +
            "AND started_at < #{timeoutTime}")
    List<InterviewSession> findTimeoutSessions(@Param("timeoutTime") LocalDateTime timeoutTime);
}