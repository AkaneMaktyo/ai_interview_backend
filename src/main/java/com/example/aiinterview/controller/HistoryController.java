package com.example.aiinterview.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aiinterview.dto.ApiResponseDTO;
import com.example.aiinterview.entity.AnswerRecord;
import com.example.aiinterview.service.AnswerRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 答题历史记录控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/history")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
@RequiredArgsConstructor
public class HistoryController {

    private final AnswerRecordService answerRecordService;

    /**
     * 获取用户答题历史（分页）
     */
    @GetMapping("/records")
    public ApiResponseDTO<Page<AnswerRecord>> getUserHistory(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Page<AnswerRecord> historyPage = answerRecordService.getUserAnswerHistoryWithQuestions(userId, page, size);
            
            log.info("获取用户{}的答题历史成功，第{}页，共{}条记录", userId, page, historyPage.getTotal());
            return ApiResponseDTO.success(historyPage);
            
        } catch (Exception e) {
            log.error("获取答题历史失败", e);
            return ApiResponseDTO.error("获取答题历史失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户学习统计
     */
    @GetMapping("/stats")
    public ApiResponseDTO<Map<String, Object>> getUserStats(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam(required = false) String period) {
        
        try {
            LocalDate startDate = null;
            LocalDate endDate = LocalDate.now();
            
            // 根据period参数确定查询时间范围
            if (period != null) {
                switch (period) {
                    case "week":
                        startDate = endDate.minusDays(7);
                        break;
                    case "month":
                        startDate = endDate.minusDays(30);
                        break;
                    case "year":
                        startDate = endDate.minusDays(365);
                        break;
                    default:
                        startDate = endDate.minusDays(30); // 默认一个月
                }
            }
            
            Map<String, Object> stats = answerRecordService.getUserLearningStats(userId, startDate, endDate);
            
            // 添加日常学习统计
            List<Object[]> dailyStats = answerRecordService.getUserDailyStats(userId, 30);
            stats.put("dailyStats", dailyStats);
            
            // 添加难度分组统计
            List<Object[]> difficultyStats = answerRecordService.getUserStatsByDifficulty(userId);
            stats.put("difficultyStats", difficultyStats);
            
            // 添加类型分组统计
            List<Object[]> typeStats = answerRecordService.getUserStatsByType(userId);
            stats.put("typeStats", typeStats);
            
            log.info("获取用户{}的学习统计成功", userId);
            return ApiResponseDTO.success(stats);
            
        } catch (Exception e) {
            log.error("获取学习统计失败", e);
            return ApiResponseDTO.error("获取学习统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户错题记录
     */
    @GetMapping("/wrong-answers")
    public ApiResponseDTO<List<AnswerRecord>> getUserWrongAnswers(
            @RequestParam(defaultValue = "1") Long userId) {
        
        try {
            List<AnswerRecord> wrongAnswers = answerRecordService.getUserWrongAnswers(userId);
            
            log.info("获取用户{}的错题记录成功，共{}条", userId, wrongAnswers.size());
            return ApiResponseDTO.success(wrongAnswers);
            
        } catch (Exception e) {
            log.error("获取错题记录失败", e);
            return ApiResponseDTO.error("获取错题记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取答题记录详情
     */
    @GetMapping("/record/{recordId}")
    public ApiResponseDTO<AnswerRecord> getAnswerRecordDetail(@PathVariable Long recordId) {
        try {
            AnswerRecord record = answerRecordService.getAnswerRecordById(recordId);
            if (record == null) {
                return ApiResponseDTO.error("答题记录不存在");
            }
            
            log.info("获取答题记录详情成功: recordId={}", recordId);
            return ApiResponseDTO.success(record);
            
        } catch (Exception e) {
            log.error("获取答题记录详情失败", e);
            return ApiResponseDTO.error("获取答题记录详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户日常学习统计图表数据
     */
    @GetMapping("/daily-stats")
    public ApiResponseDTO<List<Object[]>> getUserDailyStats(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam(defaultValue = "30") int days) {
        
        try {
            List<Object[]> dailyStats = answerRecordService.getUserDailyStats(userId, days);
            
            log.info("获取用户{}的日常学习统计成功，最近{}天", userId, days);
            return ApiResponseDTO.success(dailyStats);
            
        } catch (Exception e) {
            log.error("获取日常学习统计失败", e);
            return ApiResponseDTO.error("获取日常学习统计失败: " + e.getMessage());
        }
    }
}