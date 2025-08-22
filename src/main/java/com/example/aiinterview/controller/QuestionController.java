package com.example.aiinterview.controller;

import com.example.aiinterview.entity.Question;
import com.example.aiinterview.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 题目控制器
 */
@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
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
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", questions);
            response.put("count", questions.size());
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return response;
        }
    }

    /**
     * 根据ID获取题目
     */
    @GetMapping("/get")
    public Map<String, Object> getQuestionById(@RequestParam Long id) {
        try {
            Optional<Question> question = questionService.findById(id);
            Map<String, Object> response = new HashMap<>();
            if (question.isPresent()) {
                response.put("success", true);
                response.put("data", question.get());
            } else {
                response.put("success", false);
                response.put("error", "题目不存在");
            }
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return response;
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
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", questions);
            response.put("count", questions.size());
            response.put("searchCriteria", Map.of(
                "questionType", questionType != null ? questionType : "all",
                "difficulty", difficulty != null ? difficulty : "all",
                "position", position != null ? position : "all"
            ));
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return response;
        }
    }

    /**
     * 创建新题目
     */
    @PostMapping("/create")
    public Map<String, Object> createQuestion(@RequestParam String title,
                                            @RequestParam String content,
                                            @RequestParam String questionType,
                                            @RequestParam String difficulty,
                                            @RequestParam String position,
                                            @RequestParam(required = false) String tags,
                                            @RequestParam(required = false) String expectedAnswer,
                                            @RequestParam(required = false) String hints) {
        try {
            Question question = new Question();
            question.setTitle(title);
            question.setContent(content);
            question.setQuestionType(questionType);
            question.setDifficulty(difficulty);
            question.setPosition(position);
            question.setTags(tags);
            question.setExpectedAnswer(expectedAnswer);
            question.setHints(hints);
            
            Question savedQuestion = questionService.save(question);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", savedQuestion);
            response.put("message", "题目创建成功");
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return response;
        }
    }

    /**
     * 更新题目信息
     */
    @PostMapping("/update")
    public Map<String, Object> updateQuestion(@RequestParam Long id,
                                            @RequestParam(required = false) String title,
                                            @RequestParam(required = false) String content,
                                            @RequestParam(required = false) String questionType,
                                            @RequestParam(required = false) String difficulty,
                                            @RequestParam(required = false) String position,
                                            @RequestParam(required = false) String tags,
                                            @RequestParam(required = false) String expectedAnswer,
                                            @RequestParam(required = false) String hints) {
        try {
            Optional<Question> existingQuestion = questionService.findById(id);
            if (!existingQuestion.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "题目不存在");
                return response;
            }
            
            Question question = existingQuestion.get();
            if (title != null) question.setTitle(title);
            if (content != null) question.setContent(content);
            if (questionType != null) question.setQuestionType(questionType);
            if (difficulty != null) question.setDifficulty(difficulty);
            if (position != null) question.setPosition(position);
            if (tags != null) question.setTags(tags);
            if (expectedAnswer != null) question.setExpectedAnswer(expectedAnswer);
            if (hints != null) question.setHints(hints);
            
            Question updatedQuestion = questionService.update(id, question);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedQuestion);
            response.put("message", "题目更新成功");
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return response;
        }
    }

    /**
     * 删除题目
     */
    @PostMapping("/delete")
    public Map<String, Object> deleteQuestion(@RequestParam Long id) {
        try {
            boolean deleted = questionService.deleteById(id);
            Map<String, Object> response = new HashMap<>();
            if (deleted) {
                response.put("success", true);
                response.put("message", "题目删除成功");
            } else {
                response.put("success", false);
                response.put("error", "题目不存在或删除失败");
            }
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return response;
        }
    }

    /**
     * 获取题目总数
     */
    @GetMapping("/count")
    public Map<String, Object> getQuestionCount() {
        try {
            long count = questionService.count();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", count);
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return response;
        }
    }
}