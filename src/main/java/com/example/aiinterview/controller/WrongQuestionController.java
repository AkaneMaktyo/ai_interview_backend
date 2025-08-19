package com.example.aiinterview.controller;

import com.example.aiinterview.dto.ApiResponseDTO;
import com.example.aiinterview.entity.WrongQuestion;
import com.example.aiinterview.service.WrongQuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 错题集控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/wrong-questions")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
@RequiredArgsConstructor
public class WrongQuestionController {

    private final WrongQuestionService wrongQuestionService;

    /**
     * 获取用户错题集
     */
    @GetMapping("/list")
    public ApiResponseDTO<List<WrongQuestion>> getUserWrongQuestions(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam(defaultValue = "false") boolean includeMastered) {
        
        try {
            List<WrongQuestion> wrongQuestions = wrongQuestionService.getUserWrongQuestions(userId, includeMastered);
            
            log.info("获取用户{}的错题集成功，共{}道题目", userId, wrongQuestions.size());
            return ApiResponseDTO.success(wrongQuestions);
            
        } catch (Exception e) {
            log.error("获取错题集失败", e);
            return ApiResponseDTO.error("获取错题集失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户未掌握的错题
     */
    @GetMapping("/unmastered")
    public ApiResponseDTO<List<WrongQuestion>> getUnmasteredWrongQuestions(
            @RequestParam(defaultValue = "1") Long userId) {
        
        try {
            List<WrongQuestion> unmasteredQuestions = wrongQuestionService.getUserUnmasteredWrongQuestions(userId);
            
            log.info("获取用户{}的未掌握错题成功，共{}道题目", userId, unmasteredQuestions.size());
            return ApiResponseDTO.success(unmasteredQuestions);
            
        } catch (Exception e) {
            log.error("获取未掌握错题失败", e);
            return ApiResponseDTO.error("获取未掌握错题失败: " + e.getMessage());
        }
    }

    /**
     * 获取错题集统计信息
     */
    @GetMapping("/stats")
    public ApiResponseDTO<Map<String, Object>> getWrongQuestionStats(
            @RequestParam(defaultValue = "1") Long userId) {
        
        try {
            Map<String, Object> stats = wrongQuestionService.getUserWrongQuestionStats(userId);
            
            log.info("获取用户{}的错题集统计成功", userId);
            return ApiResponseDTO.success(stats);
            
        } catch (Exception e) {
            log.error("获取错题集统计失败", e);
            return ApiResponseDTO.error("获取错题集统计失败: " + e.getMessage());
        }
    }

    /**
     * 标记错题为已掌握
     */
    @PostMapping("/master")
    public ApiResponseDTO<String> markAsMastered(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam Long questionId) {
        
        try {
            wrongQuestionService.markAsMastered(userId, questionId);
            
            log.info("用户{}标记题目{}为已掌握成功", userId, questionId);
            return ApiResponseDTO.success("标记为已掌握成功");
            
        } catch (Exception e) {
            log.error("标记为已掌握失败", e);
            return ApiResponseDTO.error("标记为已掌握失败: " + e.getMessage());
        }
    }

    /**
     * 错题重做（记录重做次数）
     */
    @PostMapping("/retry")
    public ApiResponseDTO<String> retryWrongQuestion(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam Long questionId) {
        
        try {
            wrongQuestionService.retryWrongQuestion(userId, questionId);
            
            log.info("用户{}重做错题{}成功", userId, questionId);
            return ApiResponseDTO.success("错题重做记录成功");
            
        } catch (Exception e) {
            log.error("错题重做记录失败", e);
            return ApiResponseDTO.error("错题重做记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取最需要复习的错题
     */
    @GetMapping("/need-review")
    public ApiResponseDTO<List<WrongQuestion>> getMostNeedReviewQuestions(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            List<WrongQuestion> needReview = wrongQuestionService.getMostNeedReviewQuestions(userId, limit);
            
            log.info("获取用户{}最需要复习的错题成功，共{}道", userId, needReview.size());
            return ApiResponseDTO.success(needReview);
            
        } catch (Exception e) {
            log.error("获取最需要复习的错题失败", e);
            return ApiResponseDTO.error("获取最需要复习的错题失败: " + e.getMessage());
        }
    }

    /**
     * 删除错题记录
     */
    @DeleteMapping("/remove")
    public ApiResponseDTO<String> removeFromWrongQuestions(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam Long questionId) {
        
        try {
            wrongQuestionService.removeFromWrongQuestions(userId, questionId);
            
            log.info("用户{}删除错题{}成功", userId, questionId);
            return ApiResponseDTO.success("删除错题记录成功");
            
        } catch (Exception e) {
            log.error("删除错题记录失败", e);
            return ApiResponseDTO.error("删除错题记录失败: " + e.getMessage());
        }
    }

    /**
     * 检查题目是否在错题集中
     */
    @GetMapping("/check")
    public ApiResponseDTO<Boolean> isInWrongQuestions(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam Long questionId) {
        
        try {
            boolean isInWrongQuestions = wrongQuestionService.isInWrongQuestions(userId, questionId);
            
            return ApiResponseDTO.success(isInWrongQuestions);
            
        } catch (Exception e) {
            log.error("检查错题集失败", e);
            return ApiResponseDTO.error("检查错题集失败: " + e.getMessage());
        }
    }

    /**
     * 手动添加题目到错题集
     */
    @PostMapping("/add")
    public ApiResponseDTO<String> addToWrongQuestions(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam Long questionId) {
        
        try {
            // 这里可以传null作为firstAttemptRecordId，表示手动添加
            wrongQuestionService.addToWrongQuestions(userId, questionId, null);
            
            log.info("用户{}手动添加题目{}到错题集成功", userId, questionId);
            return ApiResponseDTO.success("添加到错题集成功");
            
        } catch (Exception e) {
            log.error("添加到错题集失败", e);
            return ApiResponseDTO.error("添加到错题集失败: " + e.getMessage());
        }
    }
}