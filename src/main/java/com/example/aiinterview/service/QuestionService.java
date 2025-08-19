package com.example.aiinterview.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aiinterview.dto.QuestionRequestDTO;
import com.example.aiinterview.entity.Question;
import com.example.aiinterview.repository.AnswerRecordRepository;
import com.example.aiinterview.repository.QuestionRepository;
import com.example.aiinterview.util.DoubaoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 题目管理服务
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRecordRepository answerRecordRepository;
    private final Optional<DoubaoUtil> doubaoUtil;

    /**
     * 生成并保存AI题目到数据库
     */
    public Question generateAndSaveQuestion(QuestionRequestDTO request, String aiPrompt, String aiResponse) {
        try {
            // 解析AI响应创建题目
            Question question = new Question();
            
            // 提取标题（取问题的前50个字符作为标题）
            String title = aiResponse.length() > 50 ? 
                aiResponse.substring(0, 47) + "..." : aiResponse;
            question.setTitle(title);
            
            question.setContent(aiResponse);
            question.setQuestionType(request.getInterviewType());
            question.setDifficulty(request.getDifficulty());
            question.setPosition(request.getPosition());
            
            // 根据请求参数生成标签
            List<String> tags = generateTagsFromRequest(request);
            question.setTags(tags);
            
            question.setAiGenerated(true);
            question.setAiPrompt(aiPrompt);
            question.setIsActive(true);
            
            questionRepository.insert(question);
            log.info("保存AI生成题目成功: {}", question.getId());
            return question;
            
        } catch (Exception e) {
            log.error("保存AI生成题目失败", e);
            throw new RuntimeException("保存题目失败: " + e.getMessage());
        }
    }

    /**
     * 获取推荐题目（避免用户已答过的题目）
     */
    public List<Question> getRecommendedQuestions(Long userId, String interviewType, 
                                                String difficulty, String position, int count) {
        // 获取用户已答题目ID
        List<Long> answeredQuestionIds = userId != null ? 
            answerRecordRepository.findAnsweredQuestionIdsByUserId(userId) : 
            Collections.emptyList();

        // 随机获取题目，排除已答题目
        List<Question> questions = questionRepository.findRandomQuestions(
            interviewType, difficulty, position, 
            answeredQuestionIds.isEmpty() ? null : answeredQuestionIds, 
            Math.max(count, 10) // 多获取一些以防不够
        );

        // 限制返回数量
        return questions.stream()
            .limit(count)
            .collect(Collectors.toList());
    }

    /**
     * 根据ID获取题目详情
     */
    public Question getQuestionById(Long id) {
        Question question = questionRepository.selectById(id);
        return (question != null && question.getIsActive()) ? question : null;
    }

    /**
     * 获取题目列表（分页）
     */
    public Page<Question> getQuestions(String interviewType, String difficulty, 
                                     String position, int page, int size) {
        Page<Question> pageParam = new Page<>(page + 1, size); // MyBatis Plus页码从1开始
        
        if (interviewType == null && difficulty == null && position == null) {
            return questionRepository.findActiveQuestionsPage(pageParam);
        }
        
        // 有筛选条件时使用条件查询
        List<Question> questions = questionRepository.findByConditions(interviewType, difficulty, position);
        
        // 手动分页处理
        int start = page * size;
        int end = Math.min(start + size, questions.size());
        List<Question> pageQuestions = start < questions.size() ? 
            questions.subList(start, end) : Collections.emptyList();
        
        Page<Question> result = new Page<>(page + 1, size);
        result.setRecords(pageQuestions);
        result.setTotal(questions.size());
        return result;
    }

    /**
     * 获取热门题目
     */
    public List<Question> getPopularQuestions(int count) {
        Page<Question> pageParam = new Page<>(1, count);
        Page<Question> popularQuestions = questionRepository.findPopularQuestions(pageParam);
        return popularQuestions.getRecords();
    }

    /**
     * 获取题目统计信息
     */
    public Map<String, Object> getQuestionStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 总题目数
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_active", true);
        long totalQuestions = questionRepository.selectCount(queryWrapper);
        stats.put("totalQuestions", totalQuestions);
        
        // 按难度统计
        List<Object[]> difficultyStats = questionRepository.countByDifficulty();
        Map<String, Long> difficultyMap = new HashMap<>();
        for (Object[] stat : difficultyStats) {
            difficultyMap.put((String) stat[0], (Long) stat[1]);
        }
        stats.put("difficultyStats", difficultyMap);
        
        // 按类型统计
        List<Object[]> typeStats = questionRepository.countByQuestionType();
        Map<String, Long> typeMap = new HashMap<>();
        for (Object[] stat : typeStats) {
            typeMap.put((String) stat[0], (Long) stat[1]);
        }
        stats.put("typeStats", typeMap);
        
        // AI生成题目比例
        List<Question> aiGeneratedQuestions = questionRepository.findByAiGeneratedAndIsActiveTrue(true);
        long aiGeneratedCount = aiGeneratedQuestions.size();
        double aiGeneratedRatio = totalQuestions > 0 ? (double) aiGeneratedCount / totalQuestions : 0;
        stats.put("aiGeneratedRatio", aiGeneratedRatio);
        
        return stats;
    }

    /**
     * 检查题目是否存在且活跃
     */
    public boolean isQuestionValid(Long questionId) {
        Question question = questionRepository.selectById(questionId);
        return question != null && question.getIsActive();
    }

    /**
     * 根据标签搜索题目
     */
    public List<Question> searchQuestionsByTag(String tag) {
        return questionRepository.findByTag(tag);
    }

    /**
     * 从请求参数生成标签
     */
    private List<String> generateTagsFromRequest(QuestionRequestDTO request) {
        List<String> tags = new ArrayList<>();
        
        // 根据面试类型添加标签
        switch (request.getInterviewType()) {
            case "technical":
                tags.add("技术面试");
                break;
            case "behavioral":
                tags.add("行为面试");
                break;
            case "system_design":
                tags.add("系统设计");
                break;
            case "coding":
                tags.add("编程面试");
                break;
        }
        
        // 根据职位方向添加标签
        switch (request.getPosition()) {
            case "frontend":
                tags.add("前端开发");
                break;
            case "backend":
                tags.add("后端开发");
                break;
            case "fullstack":
                tags.add("全栈开发");
                break;
            case "mobile":
                tags.add("移动开发");
                break;
            case "devops":
                tags.add("DevOps");
                break;
        }
        
        // 根据难度添加标签
        switch (request.getDifficulty()) {
            case "easy":
                tags.add("简单");
                break;
            case "medium":
                tags.add("中等");
                break;
            case "hard":
                tags.add("困难");
                break;
        }
        
        return tags;
    }
}