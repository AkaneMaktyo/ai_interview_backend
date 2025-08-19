package com.example.aiinterview.repository;

import com.example.aiinterview.entity.LearningStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 学习统计数据访问层
 */
@Repository
public interface LearningStatRepository extends JpaRepository<LearningStat, Long> {

    /**
     * 根据用户和日期查找统计记录
     */
    Optional<LearningStat> findByUserIdAndStatDate(Long userId, LocalDate statDate);

    /**
     * 用户指定日期范围的学习统计
     */
    List<LearningStat> findByUserIdAndStatDateBetweenOrderByStatDateDesc(
            Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 用户最近N天的学习统计
     */
    @Query("SELECT ls FROM LearningStat ls WHERE ls.userId = :userId " +
           "AND ls.statDate >= :startDate ORDER BY ls.statDate DESC")
    List<LearningStat> findRecentStats(@Param("userId") Long userId,
                                      @Param("startDate") LocalDate startDate);

    /**
     * 用户学习总统计
     */
    @Query("SELECT SUM(ls.questionsAttempted), SUM(ls.questionsCorrect), " +
           "SUM(ls.totalScore), SUM(ls.timeSpent) " +
           "FROM LearningStat ls WHERE ls.userId = :userId")
    Object[] getUserTotalStats(@Param("userId") Long userId);

    /**
     * 用户学习统计（指定日期范围）
     */
    @Query("SELECT SUM(ls.questionsAttempted), SUM(ls.questionsCorrect), " +
           "AVG(ls.totalScore), SUM(ls.timeSpent) " +
           "FROM LearningStat ls WHERE ls.userId = :userId " +
           "AND ls.statDate BETWEEN :startDate AND :endDate")
    Object[] getUserStatsInDateRange(@Param("userId") Long userId,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);

    /**
     * 删除指定日期之前的统计数据
     */
    void deleteByStatDateBefore(LocalDate date);

    /**
     * 用户连续学习天数
     */
    @Query(value = "SELECT COUNT(*) FROM learning_stats ls " +
                   "WHERE ls.user_id = :userId " +
                   "AND ls.stat_date >= :startDate " +
                   "AND ls.questions_attempted > 0",
           nativeQuery = true)
    int getUserStudyDaysCount(@Param("userId") Long userId,
                             @Param("startDate") LocalDate startDate);
}