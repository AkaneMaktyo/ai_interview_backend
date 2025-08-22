package com.zsj.aiinterview.controller;

import com.zsj.aiinterview.dto.ApiResponse;
import com.zsj.aiinterview.entity.AnswerRecord;
import com.zsj.aiinterview.service.AnswerRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 答题记录控制器
 */
@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class AnswerRecordController {

    private final AnswerRecordService answerRecordService;

    /**
     * 获取所有答题记录
     */
    @GetMapping("/list")
    public ApiResponse<List<AnswerRecord>> getAllRecords() {
        try {
            List<AnswerRecord> records = answerRecordService.list();
            return ApiResponse.success(records, (long) records.size());
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 根据ID获取答题记录
     */
    @GetMapping("/get")
    public ApiResponse<AnswerRecord> getRecordById(@RequestParam Long id) {
        try {
            AnswerRecord record = answerRecordService.getById(id);
            if (record != null) {
                return ApiResponse.success(record);
            } else {
                return ApiResponse.error("答题记录不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 创建答题记录
     */
    @PostMapping("/create")
    public ApiResponse<AnswerRecord> createRecord(@RequestParam Long userId,
                                                 @RequestParam Long questionId,
                                                 @RequestParam String interviewType,
                                                 @RequestParam String difficulty,
                                                 @RequestParam String position) {
        try {
            AnswerRecord record = answerRecordService.createRecord(userId, questionId, 
                                                                 interviewType, difficulty, position);
            return ApiResponse.success(record, "答题记录创建成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 提交答案并更新记录
     */
    @PostMapping("/submit")
    public ApiResponse<AnswerRecord> submitAnswer(@RequestParam Long id,
                                                 @RequestParam String userAnswer,
                                                 @RequestParam Integer score,
                                                 @RequestParam(required = false) Integer timeSpent,
                                                 @RequestParam(required = false) String aiEvaluation) {
        try {
            boolean updated = answerRecordService.updateAnswerRecord(id, userAnswer, score, 
                                                                   timeSpent, aiEvaluation);
            if (updated) {
                AnswerRecord record = answerRecordService.getById(id);
                return ApiResponse.success(record, "答案提交成功");
            } else {
                return ApiResponse.error("答题记录不存在或更新失败");
            }
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除答题记录
     */
    @PostMapping("/delete")
    public ApiResponse<Void> deleteRecord(@RequestParam Long id) {
        try {
            boolean deleted = answerRecordService.removeById(id);
            if (deleted) {
                return ApiResponse.success(null, "答题记录删除成功");
            } else {
                return ApiResponse.error("答题记录不存在或删除失败");
            }
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取用户的答题记录
     */
    @GetMapping("/user")
    public ApiResponse<List<AnswerRecord>> getUserRecords(@RequestParam Long userId) {
        try {
            List<AnswerRecord> records = answerRecordService.findByUserId(userId);
            return ApiResponse.success(records, (long) records.size());
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取用户在某个题目的答题记录
     */
    @GetMapping("/question")
    public ApiResponse<List<AnswerRecord>> getRecordsByQuestion(@RequestParam Long userId,
                                                               @RequestParam Long questionId) {
        try {
            List<AnswerRecord> records = answerRecordService.findByUserIdAndQuestionId(userId, questionId);
            return ApiResponse.success(records, (long) records.size());
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getUserStats(@RequestParam Long userId) {
        try {
            Map<String, Object> stats = answerRecordService.getUserStats(userId);
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取答题记录总数
     */
    @GetMapping("/count")
    public ApiResponse<Void> getRecordsCount() {
        try {
            long count = answerRecordService.count();
            return ApiResponse.count(count);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 根据面试类型获取答题记录
     */
    @GetMapping("/type")
    public ApiResponse<List<AnswerRecord>> getRecordsByType(@RequestParam String interviewType) {
        try {
            List<AnswerRecord> records = answerRecordService.lambdaQuery()
                    .eq(AnswerRecord::getInterviewType, interviewType)
                    .orderByDesc(AnswerRecord::getCreateTime)
                    .list();
            return ApiResponse.success(records, (long) records.size());
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 根据难度获取答题记录
     */
    @GetMapping("/difficulty")
    public ApiResponse<List<AnswerRecord>> getRecordsByDifficulty(@RequestParam String difficulty) {
        try {
            List<AnswerRecord> records = answerRecordService.lambdaQuery()
                    .eq(AnswerRecord::getDifficulty, difficulty)
                    .orderByDesc(AnswerRecord::getCreateTime)
                    .list();
            return ApiResponse.success(records, (long) records.size());
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}