package com.zsj.aiinterview.controller;

import com.zsj.aiinterview.entity.Question;
import com.zsj.aiinterview.service.QuestionService;
import com.zsj.aiinterview.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 题目控制器
 */
@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /**
     * 获取所有题目
     */
    @GetMapping("/list")
    public Map<String, Object> getAllQuestions() {
        try {
            List<Question> questions = questionService.findAll();
            return ResponseUtil.successWithCount(questions, questions.size());
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    /**
     * 根据ID获取题目
     */
    @GetMapping("/get")
    public Map<String, Object> getQuestionById(@RequestParam Long id) {
        try {
            Optional<Question> question = questionService.findById(id);
            return question.isPresent() 
                ? ResponseUtil.success(question.get())
                : ResponseUtil.error("题目不存在");
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    /**
     * 根据条件查询题目
     */
    @GetMapping("/search")
    public Map<String, Object> searchQuestions(@RequestParam(required = false) String questionType,
                                             @RequestParam(required = false) String difficulty,
                                             @RequestParam(required = false) String position) {
        try {
            List<Question> questions = questionService.findByCondition(questionType, difficulty, position);
            return ResponseUtil.successWithCount(questions, questions.size());
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    /**
     * 创建新题目
     */
    @PostMapping("/create")
    public Map<String, Object> createQuestion(@RequestBody Question question) {
        try {
            Question savedQuestion = questionService.createQuestion(question);
            return ResponseUtil.success(savedQuestion, "题目创建成功");
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    /**
     * 更新题目信息
     */
    @PostMapping("/update")
    public Map<String, Object> updateQuestion(@RequestBody Question question) {
        try {
            if (question.getId() == null) {
                return ResponseUtil.error("题目ID不能为空");
            }
            
            Question updatedQuestion = questionService.updateQuestion(question.getId(), question);
            return ResponseUtil.success(updatedQuestion, "题目更新成功");
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    /**
     * 删除题目
     */
    @PostMapping("/delete")
    public Map<String, Object> deleteQuestion(@RequestParam Long id) {
        try {
            boolean deleted = questionService.deactivateQuestion(id);
            return deleted 
                ? ResponseUtil.success(null, "题目删除成功")
                : ResponseUtil.error("题目不存在或删除失败");
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    /**
     * 获取题目总数
     */
    @GetMapping("/count")
    public Map<String, Object> getQuestionCount() {
        try {
            long count = questionService.getActiveQuestionCount();
            return ResponseUtil.count(count);
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }
}