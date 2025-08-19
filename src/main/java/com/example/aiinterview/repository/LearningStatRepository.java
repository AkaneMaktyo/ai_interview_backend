package com.example.aiinterview.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aiinterview.entity.LearningStat;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 学习统计数据访问层
 */
@Mapper
public interface LearningStatRepository extends BaseMapper<LearningStat> {

    /**
     * 根据用户和日期查找统计记录
     */
    @Select("SELECT * FROM learning_stats WHERE user_id = #{userId} AND stat_date = #{statDate}")
    LearningStat findByUserIdAndStatDate(@Param("userId") Long userId, @Param("statDate") LocalDate statDate);

    /**
     * 用户指定日期范围的学习统计
     */
    @Select("SELECT * FROM learning_stats WHERE user_id = #{userId} " +
            "AND stat_date BETWEEN #{startDate} AND #{endDate} ORDER BY stat_date DESC")
    List<LearningStat> findByUserIdAndStatDateBetweenOrderByStatDateDesc(
            @Param("userId") Long userId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);

    /**
     * 用户最近N天的学习统计
     */
    @Select("SELECT * FROM learning_stats WHERE user_id = #{userId} " +
            "AND stat_date >= #{startDate} ORDER BY stat_date DESC")
    List<LearningStat> findRecentStats(@Param("userId") Long userId,
                                      @Param("startDate") LocalDate startDate);

    /**
     * 用户学习总统计
     */
    @Select("SELECT SUM(questions_answered), SUM(correct_answers), " +
            "SUM(total_score), SUM(time_spent) " +
            "FROM learning_stats WHERE user_id = #{userId}")
    Object[] getUserTotalStats(@Param("userId") Long userId);

    /**
     * 用户学习统计（指定日期范围）
     */
    @Select("SELECT SUM(questions_answered), SUM(correct_answers), " +
            "AVG(total_score), SUM(time_spent) " +
            "FROM learning_stats WHERE user_id = #{userId} " +
            "AND stat_date BETWEEN #{startDate} AND #{endDate}")
    Object[] getUserStatsInDateRange(@Param("userId") Long userId,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);

    /**
     * 删除指定日期之前的统计数据
     */
    @Delete("DELETE FROM learning_stats WHERE stat_date < #{date}")
    void deleteByStatDateBefore(@Param("date") LocalDate date);

    /**
     * 用户连续学习天数
     */
    @Select("SELECT COUNT(*) FROM learning_stats " +
            "WHERE user_id = #{userId} " +
            "AND stat_date >= #{startDate} " +
            "AND questions_answered > 0")
    int getUserStudyDaysCount(@Param("userId") Long userId,
                             @Param("startDate") LocalDate startDate);
}