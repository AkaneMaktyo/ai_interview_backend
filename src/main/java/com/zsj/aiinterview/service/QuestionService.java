package com.zsj.aiinterview.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsj.aiinterview.entity.Question;
import com.zsj.aiinterview.mapper.QuestionMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 题目服务 - 使用MyBatis Plus
 */
@Service
public class QuestionService extends ServiceImpl<QuestionMapper, Question> {

    /**
     * 查询所有题目
     */
    public List<Question> findAll() {
        return this.list();
    }

    /**
     * 根据ID查询题目
     */
    public Optional<Question> findById(Long id) {
        Question question = this.getById(id);
        return Optional.ofNullable(question);
    }

    /**
     * 根据题目类型查询题目
     */
    public List<Question> findByType(String questionType) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("question_type", questionType);
        queryWrapper.eq("is_active", true);
        return this.list(queryWrapper);
    }

    /**
     * 根据难度查询题目
     */
    public List<Question> findByDifficulty(String difficulty) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("difficulty", difficulty);
        queryWrapper.eq("is_active", true);
        return this.list(queryWrapper);
    }

    /**
     * 根据多个条件查询题目
     *
     * @param questionType 题目类型
     * @param difficulty   难度等级
     * @param position     适用岗位
     * @return 符合条件的题目列表
     */
    public List<Question> findByCondition(String questionType, String difficulty, String position) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        
        // 只查询激活的题目
        queryWrapper.eq("is_active", true);
        
        // 根据传入的参数添加条件
        if (questionType != null && !questionType.trim().isEmpty()) {
            queryWrapper.eq("question_type", questionType);
        }
        
        if (difficulty != null && !difficulty.trim().isEmpty()) {
            queryWrapper.eq("difficulty", difficulty);
        }
        
        if (position != null && !position.trim().isEmpty()) {
            queryWrapper.eq("position", position);
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc("created_at");
        
        return this.list(queryWrapper);
    }

    /**
     * 创建新题目
     */
    public Question createQuestion(Question question) {
        LocalDateTime now = LocalDateTime.now();
        if (question.getIsActive() == null) {
            question.setIsActive(true);
        }
        if (question.getAiGenerated() == null) {
            question.setAiGenerated(false);
        }
        
        this.save(question);
        return question;
    }

    /**
     * 更新题目信息
     */
    public Question updateQuestion(Long id, Question question) {
        question.setId(id);
        this.updateById(question);
        return this.getById(id);
    }

    /**
     * 逻辑删除题目（设置为非激活状态）
     */
    public boolean deactivateQuestion(Long id) {
        Question question = new Question();
        question.setId(id);
        question.setIsActive(false);
        return this.updateById(question);
    }

    /**
     * 获取激活题目总数
     */
    public long getActiveQuestionCount() {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_active", true);
        return this.count(queryWrapper);
    }

    /**
     * 构建题目对象 - 将Controller的逻辑移到Service层
     */
    public Question buildQuestion(String title, String content, String questionType, 
                                String difficulty, String position, String tags, 
                                String expectedAnswer, String hints) {
        Question question = new Question();
        question.setTitle(title);
        question.setContent(content);
        question.setQuestionType(questionType);
        question.setDifficulty(difficulty);
        question.setPosition(position);
        question.setTags(tags);
        question.setExpectedAnswer(expectedAnswer);
        question.setHints(hints);
        return question;
    }

    /**
     * 更新题目字段 - 将Controller的更新逻辑移到Service层
     */
    public Optional<Question> updateQuestionFields(Long id, String title, String content,
                                                 String questionType, String difficulty, String position,
                                                 String tags, String expectedAnswer, String hints) {
        Optional<Question> existingQuestion = findById(id);
        if (!existingQuestion.isPresent()) {
            return Optional.empty();
        }
        
        Question question = existingQuestion.get();
        // 只更新非空字段
        if (title != null && !title.trim().isEmpty()) question.setTitle(title);
        if (content != null && !content.trim().isEmpty()) question.setContent(content);
        if (questionType != null && !questionType.trim().isEmpty()) question.setQuestionType(questionType);
        if (difficulty != null && !difficulty.trim().isEmpty()) question.setDifficulty(difficulty);
        if (position != null && !position.trim().isEmpty()) question.setPosition(position);
        if (tags != null) question.setTags(tags);
        if (expectedAnswer != null) question.setExpectedAnswer(expectedAnswer);
        if (hints != null) question.setHints(hints);
        
        Question updatedQuestion = updateQuestion(id, question);
        return Optional.ofNullable(updatedQuestion);
    }

}