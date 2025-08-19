package com.example.aiinterview.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aiinterview.entity.WrongQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 错题集数据访问层
 */
@Mapper
public interface WrongQuestionRepository extends BaseMapper<WrongQuestion> {

    /**
     * 用户错题集
     */
    @Select("SELECT * FROM wrong_questions WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<WrongQuestion> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    /**
     * 用户未掌握的错题
     */
    @Select("SELECT * FROM wrong_questions WHERE user_id = #{userId} AND mastered = false ORDER BY created_at DESC")
    List<WrongQuestion> findByUserIdAndMasteredFalseOrderByCreatedAtDesc(@Param("userId") Long userId);

    /**
     * 用户已掌握的错题
     */
    @Select("SELECT * FROM wrong_questions WHERE user_id = #{userId} AND mastered = true ORDER BY mastered_at DESC")
    List<WrongQuestion> findByUserIdAndMasteredTrueOrderByMasteredAtDesc(@Param("userId") Long userId);

    /**
     * 检查是否已存在错题记录
     */
    @Select("SELECT * FROM wrong_questions WHERE user_id = #{userId} AND question_id = #{questionId}")
    WrongQuestion findByUserIdAndQuestionId(@Param("userId") Long userId, @Param("questionId") Long questionId);

    /**
     * 用户错题总数
     */
    @Select("SELECT COUNT(*) FROM wrong_questions WHERE user_id = #{userId}")
    long countByUserId(@Param("userId") Long userId);

    /**
     * 用户未掌握错题数
     */
    @Select("SELECT COUNT(*) FROM wrong_questions WHERE user_id = #{userId} AND mastered = false")
    long countByUserIdAndMasteredFalse(@Param("userId") Long userId);

    /**
     * 错题按知识点分组统计
     */
    @Select("SELECT tag_item, COUNT(*) as count " +
            "FROM wrong_questions wq " +
            "JOIN questions q ON wq.question_id = q.id, " +
            "jsonb_array_elements_text(q.tags) AS tag_item " +
            "WHERE wq.user_id = #{userId} AND wq.mastered = false " +
            "GROUP BY tag_item " +
            "ORDER BY count DESC")
    List<Object[]> getWrongQuestionTagStats(@Param("userId") Long userId);

    /**
     * 最需要复习的错题（重做次数少且时间久远）
     */
    @Select("SELECT * FROM wrong_questions " +
            "WHERE user_id = #{userId} AND mastered = false " +
            "ORDER BY retry_count ASC, created_at ASC")
    List<WrongQuestion> findMostNeedReviewQuestions(@Param("userId") Long userId);

    /**
     * 根据难度统计错题
     */
    @Select("SELECT q.difficulty, COUNT(wq.*) " +
            "FROM wrong_questions wq " +
            "JOIN questions q ON wq.question_id = q.id " +
            "WHERE wq.user_id = #{userId} AND wq.mastered = false " +
            "GROUP BY q.difficulty")
    List<Object[]> getWrongQuestionsByDifficulty(@Param("userId") Long userId);

    /**
     * 根据题目类型统计错题
     */
    @Select("SELECT q.question_type, COUNT(wq.*) " +
            "FROM wrong_questions wq " +
            "JOIN questions q ON wq.question_id = q.id " +
            "WHERE wq.user_id = #{userId} AND wq.mastered = false " +
            "GROUP BY q.question_type")
    List<Object[]> getWrongQuestionsByType(@Param("userId") Long userId);
}