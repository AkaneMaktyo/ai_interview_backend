package com.zsj.aiinterview.controller;

import com.zsj.aiinterview.entity.InterviewSession;
import com.zsj.aiinterview.entity.Question;
import com.zsj.aiinterview.service.InterviewSessionService;
import com.zsj.aiinterview.service.QuestionService;
import com.zsj.aiinterview.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * AI面试控制器
 */
@RestController
@RequestMapping("/api/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewSessionService sessionService;
    private final QuestionService questionService;

    /**
     * 创建新的面试会话
     */
    @PostMapping("/session/create")
    public Map<String, Object> createSession(@RequestParam Long userId,
                                           @RequestParam String sessionName,
                                           @RequestParam String interviewType,
                                           @RequestParam String difficulty,
                                           @RequestParam String position) {
        try {
            InterviewSession session = new InterviewSession(userId, sessionName, interviewType, difficulty, position);
            InterviewSession savedSession = sessionService.save(session);
            return ResponseUtil.success(savedSession, "面试会话创建成功");
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    /**
     * 获取用户的所有面试会话
     */
    @GetMapping("/session/list")
    public Map<String, Object> getUserSessions(@RequestParam Long userId) {
        try {
            List<InterviewSession> sessions = sessionService.findByUserId(userId);
            return ResponseUtil.successWithCount(sessions, sessions.size());
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    /**
     * 获取用户的活跃会话
     */
    @GetMapping("/session/active")
    public Map<String, Object> getActiveSessions(@RequestParam Long userId) {
        try {
            List<InterviewSession> sessions = sessionService.findActiveSessionsByUserId(userId);
            return ResponseUtil.successWithCount(sessions, sessions.size());
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    /**
     * 根据ID获取面试会话
     */
    @GetMapping("/session/get")
    public Map<String, Object> getSessionById(@RequestParam Long id) {
        try {
            Optional<InterviewSession> session = sessionService.findById(id);
            return session.isPresent() ? ResponseUtil.success(session.get()) : ResponseUtil.error("会话不存在");
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    /**
     * 结束面试会话
     */
    @PostMapping("/session/end")
    public Map<String, Object> endSession(@RequestParam Long id) {
        try {
            InterviewSession session = sessionService.endSession(id);
            return session != null ? 
                ResponseUtil.success(session, "面试会话已结束") : 
                ResponseUtil.error("会话不存在或操作失败");
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    /**
     * 获取面试题目（根据难度和职位）
     */
    @GetMapping("/questions")
    public Map<String, Object> getInterviewQuestions(@RequestParam String difficulty,
                                                   @RequestParam String position,
                                                   @RequestParam(required = false, defaultValue = "5") Integer limit) {
        try {
            List<Question> questions = questionService.findByCondition("technical", difficulty, position);
            
            // 限制题目数量
            if (questions.size() > limit) {
                questions = questions.subList(0, limit);
            }
            
            Map<String, Object> response = ResponseUtil.successWithCount(questions, questions.size());
            response.put("difficulty", difficulty);
            response.put("position", position);
            response.put("limit", limit);
            return response;
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    /**
     * 简单的AI回答生成（模拟功能）
     */
    @PostMapping("/ai/answer")
    public Map<String, Object> generateAIAnswer(@RequestParam Long questionId,
                                              @RequestParam String userAnswer) {
        try {
            Optional<Question> question = questionService.findById(questionId);
            if (!question.isPresent()) {
                return ResponseUtil.error("题目不存在");
            }

            // 简单的AI评估逻辑（实际应用中会连接真实AI服务）
            String aiEvaluation = generateSimpleEvaluation(question.get(), userAnswer);
            int score = calculateSimpleScore(userAnswer);
            
            Map<String, Object> result = new HashMap<>();
            result.put("questionId", questionId);
            result.put("questionTitle", question.get().getTitle());
            result.put("userAnswer", userAnswer);
            result.put("aiEvaluation", aiEvaluation);
            result.put("score", score);
            result.put("expectedAnswer", question.get().getExpectedAnswer());
            
            return ResponseUtil.success(result, "AI评估完成");
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    /**
     * 简单的AI评估生成（模拟）
     */
    private String generateSimpleEvaluation(Question question, String userAnswer) {
        if (userAnswer == null || userAnswer.trim().isEmpty()) {
            return "回答为空，建议详细说明相关概念和实际应用。";
        }
        
        int length = userAnswer.length();
        if (length < 50) {
            return "回答较简短，建议补充更多细节和具体示例。可以从以下角度展开：" + 
                   (question.getHints() != null ? question.getHints() : "概念解释、实际应用、优缺点分析等。");
        } else if (length < 200) {
            return "回答基本完整，涵盖了主要知识点。建议进一步补充实际项目经验和深入思考。";
        } else {
            return "回答详细完整，体现了良好的技术理解。建议在实际面试中保持这种详细程度，同时注意逻辑清晰。";
        }
    }

    /**
     * 简单的评分计算（模拟）
     */
    private int calculateSimpleScore(String userAnswer) {
        if (userAnswer == null || userAnswer.trim().isEmpty()) {
            return 0;
        }
        
        int length = userAnswer.length();
        if (length < 50) {
            return 3; // 30分
        } else if (length < 100) {
            return 5; // 50分
        } else if (length < 200) {
            return 7; // 70分
        } else {
            return 8; // 80分
        }
    }
}