package com.example.aiinterview.controller;

import com.example.aiinterview.entity.AnswerRecord;
import com.example.aiinterview.service.AnswerRecordService;
import com.example.aiinterview.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 答题记录控制器
 */
@RestController
@RequestMapping("/api/records")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
@RequiredArgsConstructor
public class AnswerRecordController {

    private final AnswerRecordService answerRecordService;

    /**
     * 保存答题记录
     */
    @PostMapping("/save")
    public Map<String, Object> saveRecord(@RequestParam Long userId,
                                        @RequestParam Long questionId,
                                        @RequestParam String userAnswer,
                                        @RequestParam Integer score,
                                        @RequestParam String interviewType,
                                        @RequestParam String difficulty,
                                        @RequestParam String position,
                                        @RequestParam String aiEvaluation) {
        try {
            AnswerRecord record = new AnswerRecord(userId, questionId, userAnswer, score,
                                                 interviewType, difficulty, position, aiEvaluation);
            AnswerRecord savedRecord = answerRecordService.save(record);
            return ResponseUtil.success(savedRecord, "答题记录保存成功");
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    /**
     * 获取用户答题记录
     */
    @GetMapping("/user")
    public Map<String, Object> getUserRecords(@RequestParam Long userId) {
        try {
            List<AnswerRecord> records = answerRecordService.findByUserId(userId);
            return ResponseUtil.successWithCount(records, records.size());
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/stats")
    public Map<String, Object> getUserStats(@RequestParam Long userId) {
        try {
            Map<String, Object> stats = answerRecordService.getUserStats(userId);
            return ResponseUtil.success(stats);
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    /**
     * 根据题目查询答题记录
     */
    @GetMapping("/question")
    public Map<String, Object> getRecordsByQuestion(@RequestParam Long userId,
                                                   @RequestParam Long questionId) {
        try {
            List<AnswerRecord> records = answerRecordService.findByUserIdAndQuestionId(userId, questionId);
            return ResponseUtil.successWithCount(records, records.size());
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }
}