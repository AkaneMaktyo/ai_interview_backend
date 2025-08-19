package com.example.aiinterview.repository;

import com.example.aiinterview.entity.InterviewSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 面试会话数据访问层
 */
@Repository
public interface InterviewSessionRepository extends JpaRepository<InterviewSession, Long> {

    /**
     * 用户面试会话历史
     */
    Page<InterviewSession> findByUserIdOrderByStartedAtDesc(Long userId, Pageable pageable);

    /**
     * 用户进行中的面试会话
     */
    List<InterviewSession> findByUserIdAndStatus(Long userId, String status);

    /**
     * 用户已完成的面试会话
     */
    List<InterviewSession> findByUserIdAndStatusOrderByCompletedAtDesc(Long userId, String status);

    /**
     * 用户面试会话统计
     */
    @Query("SELECT COUNT(is), AVG(is.overallScore), AVG(is.durationMinutes) " +
           "FROM InterviewSession is WHERE is.userId = :userId AND is.status = 'completed'")
    Object[] getUserInterviewStats(@Param("userId") Long userId);

    /**
     * 按类型统计面试会话
     */
    @Query("SELECT is.interviewType, COUNT(is), AVG(is.overallScore) " +
           "FROM InterviewSession is WHERE is.userId = :userId AND is.status = 'completed' " +
           "GROUP BY is.interviewType")
    List<Object[]> getUserInterviewStatsByType(@Param("userId") Long userId);

    /**
     * 按难度统计面试会话
     */
    @Query("SELECT is.difficulty, COUNT(is), AVG(is.overallScore) " +
           "FROM InterviewSession is WHERE is.userId = :userId AND is.status = 'completed' " +
           "GROUP BY is.difficulty")
    List<Object[]> getUserInterviewStatsByDifficulty(@Param("userId") Long userId);

    /**
     * 最近面试会话
     */
    @Query("SELECT is FROM InterviewSession is WHERE is.userId = :userId " +
           "AND is.startedAt >= :startTime ORDER BY is.startedAt DESC")
    List<InterviewSession> findRecentSessions(@Param("userId") Long userId,
                                            @Param("startTime") LocalDateTime startTime);

    /**
     * 清理超时的进行中会话
     */
    @Query("SELECT is FROM InterviewSession is WHERE is.status = 'in_progress' " +
           "AND is.startedAt < :timeoutTime")
    List<InterviewSession> findTimeoutSessions(@Param("timeoutTime") LocalDateTime timeoutTime);
}