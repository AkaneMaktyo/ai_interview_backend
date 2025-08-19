package com.example.aiinterview.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aiinterview.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 题目数据访问层
 */
@Mapper
public interface QuestionRepository extends BaseMapper<Question> {

    /**
     * 根据条件查询题目
     */
    @Select("<script>" +
            "SELECT * FROM questions WHERE is_active = true " +
            "<if test='questionType != null'> AND question_type = #{questionType} </if>" +
            "<if test='difficulty != null'> AND difficulty = #{difficulty} </if>" +
            "<if test='position != null'> AND position = #{position} </if>" +
            "ORDER BY created_at DESC" +
            "</script>")
    List<Question> findByConditions(@Param("questionType") String questionType,
                                   @Param("difficulty") String difficulty,
                                   @Param("position") String position);

    /**
     * 根据标签查询题目（JSONB数组包含查询）
     */
    @Select("SELECT * FROM questions WHERE is_active = true AND tags @> to_jsonb(#{tag})")
    List<Question> findByTag(@Param("tag") String tag);

    /**
     * 分页查询活跃题目
     */
    @Select("SELECT * FROM questions WHERE is_active = true ORDER BY created_at DESC")
    Page<Question> findActiveQuestionsPage(Page<Question> page);

    /**
     * 根据类型和难度查询
     */
    @Select("SELECT * FROM questions WHERE question_type = #{questionType} AND difficulty = #{difficulty} AND is_active = true")
    List<Question> findByQuestionTypeAndDifficultyAndIsActiveTrue(@Param("questionType") String questionType, 
                                                                  @Param("difficulty") String difficulty);

    /**
     * 根据职位查询
     */
    @Select("SELECT * FROM questions WHERE position = #{position} AND is_active = true")
    List<Question> findByPositionAndIsActiveTrue(@Param("position") String position);

    /**
     * 统计各难度题目数量
     */
    @Select("SELECT difficulty, COUNT(*) FROM questions WHERE is_active = true GROUP BY difficulty")
    List<Object[]> countByDifficulty();

    /**
     * 统计各类型题目数量
     */
    @Select("SELECT question_type, COUNT(*) FROM questions WHERE is_active = true GROUP BY question_type")
    List<Object[]> countByQuestionType();

    /**
     * 随机获取题目（排除指定ID列表）
     */
    @Select("<script>" +
            "SELECT * FROM questions WHERE is_active = true " +
            "<if test='questionType != null'> AND question_type = #{questionType} </if>" +
            "<if test='difficulty != null'> AND difficulty = #{difficulty} </if>" +
            "<if test='position != null'> AND position = #{position} </if>" +
            "<if test='excludeIds != null and excludeIds.size() > 0'>" +
            " AND id NOT IN " +
            "<foreach collection='excludeIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</if>" +
            " ORDER BY RANDOM() LIMIT #{limit}" +
            "</script>")
    List<Question> findRandomQuestions(@Param("questionType") String questionType,
                                     @Param("difficulty") String difficulty,
                                     @Param("position") String position,
                                     @Param("excludeIds") List<Long> excludeIds,
                                     @Param("limit") Integer limit);

    /**
     * 根据AI生成标识查询
     */
    @Select("SELECT * FROM questions WHERE ai_generated = #{aiGenerated} AND is_active = true")
    List<Question> findByAiGeneratedAndIsActiveTrue(@Param("aiGenerated") Boolean aiGenerated);

    /**
     * 查找热门题目（基于答题记录统计）
     */
    @Select("SELECT q.* FROM questions q " +
            "JOIN answer_records ar ON q.id = ar.question_id " +
            "WHERE q.is_active = true " +
            "GROUP BY q.id " +
            "ORDER BY COUNT(ar.id) DESC")
    Page<Question> findPopularQuestions(Page<Question> page);
}