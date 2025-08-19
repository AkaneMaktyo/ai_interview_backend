package com.example.aiinterview.controller;

import com.example.aiinterview.dto.*;
import com.example.aiinterview.entity.Question;
import com.example.aiinterview.service.QuestionService;
import com.example.aiinterview.service.AnswerRecordService;
import com.example.aiinterview.util.DoubaoUtil;
import com.example.aiinterview.util.HttpAiUtil;
import com.example.aiinterview.util.SimpleAiUtil;
import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 面试功能控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
@RequiredArgsConstructor
public class InterviewController {

    private final HttpAiUtil httpAiUtil;
    private final SimpleAiUtil simpleAiUtil;
    private final Optional<DoubaoUtil> doubaoUtil;
    private final QuestionService questionService;
    private final AnswerRecordService answerRecordService;

    /**
     * 获取面试题目
     * @param request 题目请求参数
     * @return 面试题目
     */
    @PostMapping("/question")
    public ApiResponseDTO<Map<String, Object>> getQuestion(@RequestBody QuestionRequestDTO request) {
        log.info("获取面试题目请求: {}", JSON.toJSONString(request));
        
        try {
            // 构建面试题目提示词
            String questionPrompt = buildQuestionPrompt(request);
            
            // 使用AI生成题目并保存到数据库
            Map<String, Object> questionData = generateQuestionWithAI(questionPrompt, request);
            
            log.info("生成面试题目成功: {}", JSON.toJSONString(questionData));
            return ApiResponseDTO.success(questionData);
            
        } catch (Exception e) {
            log.error("获取面试题目失败", e);
            return ApiResponseDTO.error("获取面试题目失败: " + e.getMessage());
        }
    }

    /**
     * 提交回答并获取反馈
     * @param request 回答请求参数
     * @return AI反馈
     */
    @PostMapping("/answer")
    public ApiResponseDTO<Map<String, Object>> submitAnswer(@RequestBody AnswerRequestDTO request) {
        log.info("提交回答请求: {}", JSON.toJSONString(request));
        
        try {
            // 构建评估提示词
            String evaluationPrompt = buildEvaluationPrompt(request);
            
            // 使用AI评估回答
            FeedbackDTO feedback = evaluateAnswerWithAI(evaluationPrompt, request);
            
            // 保存答题记录到数据库（使用默认用户ID: 1）
            Long userId = 1L; // 临时使用固定用户ID，后续可通过登录信息获取
            answerRecordService.saveAnswerRecord(request, feedback, userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("feedback", feedback);
            
            log.info("生成回答反馈成功: {}", JSON.toJSONString(result));
            return ApiResponseDTO.success(result);
            
        } catch (Exception e) {
            log.error("提交回答失败", e);
            return ApiResponseDTO.error("提交回答失败: " + e.getMessage());
        }
    }

    /**
     * 获取面试总结
     * @param request 总结请求参数
     * @return 面试总结
     */
    @PostMapping("/summary")
    public ApiResponseDTO<SummaryDTO> getInterviewSummary(@RequestBody SummaryRequestDTO request) {
        log.info("获取面试总结请求: {}", JSON.toJSONString(request));
        
        try {
            // 生成面试总结
            SummaryDTO summary = generateInterviewSummary(request);
            
            log.info("生成面试总结成功: {}", JSON.toJSONString(summary));
            return ApiResponseDTO.success(summary);
            
        } catch (Exception e) {
            log.error("获取面试总结失败", e);
            return ApiResponseDTO.error("获取面试总结失败: " + e.getMessage());
        }
    }

    /**
     * 构建面试题目提示词
     */
    private String buildQuestionPrompt(QuestionRequestDTO request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个专业的面试官，需要根据以下条件生成一道面试题目：\n");
        prompt.append("面试类型：").append(getInterviewTypeText(request.getInterviewType())).append("\n");
        prompt.append("难度等级：").append(getDifficultyText(request.getDifficulty())).append("\n");
        prompt.append("职位方向：").append(getPositionText(request.getPosition())).append("\n");
        prompt.append("工作经验：").append(getExperienceText(request.getExperience())).append("\n");
        prompt.append("当前是第 ").append(request.getQuestionIndex() + 1).append(" 题\n");
        
        if (request.getHistory() != null && !request.getHistory().isEmpty()) {
            prompt.append("\n已经问过的题目：\n");
            for (InterviewHistoryDTO history : request.getHistory()) {
                prompt.append("- ").append(history.getQuestion()).append("\n");
            }
            prompt.append("\n请避免重复，生成新的题目。");
        }
        
        prompt.append("\n请生成一道合适的面试题目，要求：\n");
        prompt.append("1. 题目要符合指定的类型和难度\n");
        prompt.append("2. 适合指定经验级别的候选人\n");
        prompt.append("3. 与职位方向相关\n");
        prompt.append("4. 题目表述清晰明确\n");
        prompt.append("\n只需要返回题目内容，不需要其他说明。");
        
        return prompt.toString();
    }

    /**
     * 构建评估提示词
     */
    private String buildEvaluationPrompt(AnswerRequestDTO request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个专业的面试官，需要评估候选人的回答：\n\n");
        prompt.append("面试题目：").append(request.getQuestion()).append("\n");
        prompt.append("候选人回答：").append(request.getAnswer()).append("\n\n");
        prompt.append("面试信息：\n");
        prompt.append("- 类型：").append(getInterviewTypeText(request.getInterviewType() != null ? request.getInterviewType() : "technical")).append("\n");
        prompt.append("- 难度：").append(getDifficultyText(request.getDifficulty() != null ? request.getDifficulty() : "medium")).append("\n");
        prompt.append("- 职位：").append(getPositionText(request.getPosition() != null ? request.getPosition() : "frontend")).append("\n\n");
        
        prompt.append("请对这个回答进行评估，需要返回JSON格式：\n");
        prompt.append("{\n");
        prompt.append("  \"score\": 评分(1-10的整数),\n");
        prompt.append("  \"comment\": \"详细评价\",\n");
        prompt.append("  \"suggestions\": [\"改进建议1\", \"改进建议2\"]\n");
        prompt.append("}\n\n");
        prompt.append("评分标准：\n");
        prompt.append("- 9-10分：回答优秀，全面准确，逻辑清晰\n");
        prompt.append("- 7-8分：回答良好，基本正确，有一定深度\n");
        prompt.append("- 5-6分：回答一般，部分正确，缺少细节\n");
        prompt.append("- 3-4分：回答较差，理解有误，缺乏逻辑\n");
        prompt.append("- 1-2分：回答很差，基本错误或无关");
        
        return prompt.toString();
    }

    /**
     * 使用AI生成面试题目
     */
    private Map<String, Object> generateQuestionWithAI(String prompt, QuestionRequestDTO request) {
        Map<String, Object> questionData = new HashMap<>();
        
        try {
            String aiResponse;
            
            // 优先使用豆包AI
            if (doubaoUtil.isPresent()) {
                log.info("使用豆包AI生成面试题目");
                aiResponse = doubaoUtil.get().getSyncResponse(prompt, false); // 不使用深度思考模式，用联网模式
            } else {
                log.info("豆包AI不可用，降级使用HTTP模式");
                aiResponse = getHttpAiResponse(prompt);
            }
            
            // 从AI响应中提取题目（AI应该直接返回题目内容）
            String question = extractQuestionFromResponse(aiResponse);
            
            // 保存题目到数据库
            Question savedQuestion = questionService.generateAndSaveQuestion(request, prompt, question);
            
            questionData.put("question", question);
            questionData.put("type", request.getInterviewType());
            questionData.put("questionId", savedQuestion.getId().toString()); // 使用数据库生成的真实ID
            
        } catch (Exception e) {
            log.error("AI生成题目失败，使用模拟数据: ", e);
            // 如果AI调用失败，回退到模拟数据
            return generateMockQuestion(request);
        }
        
        return questionData;
    }
    
    /**
     * 使用AI评估回答
     */
    private FeedbackDTO evaluateAnswerWithAI(String evaluationPrompt, AnswerRequestDTO request) {
        try {
            String aiResponse;
            
            // 优先使用豆包AI
            if (doubaoUtil.isPresent()) {
                log.info("使用豆包AI评估回答");
                aiResponse = doubaoUtil.get().getSyncResponse(evaluationPrompt, true); // 使用深度思考模式
            } else {
                log.info("豆包AI不可用，降级使用HTTP模式");
                aiResponse = getHttpAiResponse(evaluationPrompt);
            }
            
            // 解析AI响应为反馈对象
            return parseFeedbackFromResponse(aiResponse);
            
        } catch (Exception e) {
            log.error("AI评估回答失败，使用模拟数据: ", e);
            // 如果AI调用失败，回退到模拟数据
            return generateMockFeedback(request);
        }
    }
    
    /**
     * 使用HTTP方式调用AI（作为豆包AI的备选方案）
     */
    private String getHttpAiResponse(String prompt) {
        // 这里可以实现HTTP方式的AI调用
        // 暂时抛出异常，让它回退到模拟数据
        throw new RuntimeException("HTTP AI调用未实现，回退到模拟数据");
    }
    
    /**
     * 从AI响应中提取题目内容
     */
    private String extractQuestionFromResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            throw new RuntimeException("AI返回空响应");
        }
        
        // 简单的文本清理和提取
        String question = response.trim();
        
        // 移除可能的前缀（如"题目："、"Question:"等）
        question = question.replaceFirst("^(题目[:：]|问题[:：]|Question[:：]?|面试题[:：]?)\\s*", "");
        
        // 如果题目太短，可能不是有效题目
        if (question.length() < 10) {
            throw new RuntimeException("AI返回的题目过短: " + question);
        }
        
        return question;
    }
    
    /**
     * 从AI响应中解析反馈信息
     */
    private FeedbackDTO parseFeedbackFromResponse(String response) {
        try {
            // 尝试解析JSON格式的回应
            if (response.contains("{") && response.contains("}")) {
                int startIndex = response.indexOf("{");
                int endIndex = response.lastIndexOf("}") + 1;
                String jsonStr = response.substring(startIndex, endIndex);
                
                Map<String, Object> jsonMap = JSON.parseObject(jsonStr, Map.class);
                
                FeedbackDTO feedback = new FeedbackDTO();
                
                // 解析评分
                Object scoreObj = jsonMap.get("score");
                if (scoreObj instanceof Number) {
                    feedback.setScore(((Number) scoreObj).intValue());
                } else {
                    feedback.setScore(7); // 默认评分
                }
                
                // 解析评价
                Object commentObj = jsonMap.get("comment");
                if (commentObj != null) {
                    feedback.setComment(commentObj.toString());
                } else {
                    feedback.setComment("AI评估完成");
                }
                
                // 解析建议
                Object suggestionsObj = jsonMap.get("suggestions");
                if (suggestionsObj instanceof List) {
                    List<String> suggestions = new ArrayList<>();
                    for (Object item : (List<?>) suggestionsObj) {
                        if (item != null) {
                            suggestions.add(item.toString());
                        }
                    }
                    feedback.setSuggestions(suggestions);
                } else {
                    feedback.setSuggestions(Arrays.asList("建议继续深入学习相关知识点"));
                }
                
                return feedback;
            } else {
                // 如果不是JSON格式，创建一个基础的反馈
                FeedbackDTO feedback = new FeedbackDTO();
                feedback.setScore(7);
                feedback.setComment(response.length() > 200 ? response.substring(0, 200) + "..." : response);
                feedback.setSuggestions(Arrays.asList("建议参考AI的详细分析"));
                return feedback;
            }
        } catch (Exception e) {
            log.error("解析AI反馈失败: ", e);
            // 创建一个默认的反馈
            FeedbackDTO feedback = new FeedbackDTO();
            feedback.setScore(6);
            feedback.setComment("AI反馈解析异常，请重试");
            feedback.setSuggestions(Arrays.asList("请检查回答内容并重新提交"));
            return feedback;
        }
    }

    /**
     * 根据面试类型生成题目
     */
    private String generateQuestionByType(QuestionRequestDTO request) {
        String type = request.getInterviewType();
        String position = request.getPosition();
        String difficulty = request.getDifficulty();
        int questionIndex = request.getQuestionIndex();
        
        List<String> questions = new ArrayList<>();
        
        switch (type) {
            case "technical":
                questions.addAll(getTechnicalQuestions(position, difficulty));
                break;
            case "behavioral":
                questions.addAll(getBehavioralQuestions());
                break;
            case "system_design":
                questions.addAll(getSystemDesignQuestions(difficulty));
                break;
            case "coding":
                questions.addAll(getCodingQuestions(difficulty));
                break;
            default:
                questions.addAll(getTechnicalQuestions(position, difficulty));
        }
        
        // 随机选择一个题目
        int index = ThreadLocalRandom.current().nextInt(questions.size());
        return questions.get(index);
    }

    // ==================== 模拟数据方法（作为AI调用失败时的回退机制） ====================
    
    /**
     * 生成模拟面试题目（回退机制）
     */
    private Map<String, Object> generateMockQuestion(QuestionRequestDTO request) {
        Map<String, Object> questionData = new HashMap<>();
        
        // 根据面试类型生成不同的题目
        String question = generateQuestionByType(request);
        
        questionData.put("question", question);
        questionData.put("type", request.getInterviewType());
        questionData.put("questionId", "fallback_" + System.currentTimeMillis());
        
        return questionData;
    }

    /**
     * 获取技术题目池
     */
    private List<String> getTechnicalQuestions(String position, String difficulty) {
        List<String> questions = new ArrayList<>();
        
        if ("frontend".equals(position)) {
            if ("easy".equals(difficulty)) {
                questions.add("请介绍一下HTML、CSS、JavaScript的作用和区别。");
                questions.add("什么是响应式设计？如何实现？");
                questions.add("请说明浏览器的工作原理。");
            } else if ("medium".equals(difficulty)) {
                questions.add("请详细解释JavaScript的闭包机制，并提供一个实际应用场景。");
                questions.add("Vue.js和React的虚拟DOM有什么区别？各有什么优缺点？");
                questions.add("如何优化前端性能？请从多个角度详细说明。");
            } else { // hard
                questions.add("请设计一个前端微服务架构，并说明如何解决跨应用通信问题。");
                questions.add("如何实现一个高性能的虚拟列表组件？涉及哪些技术细节？");
                questions.add("浏览器的垃圾回收机制是怎样的？如何避免内存泄漏？");
            }
        } else if ("backend".equals(position)) {
            if ("easy".equals(difficulty)) {
                questions.add("请解释什么是RESTful API，有哪些设计原则？");
                questions.add("数据库的ACID特性是什么意思？");
                questions.add("什么是MVC架构模式？");
            } else if ("medium".equals(difficulty)) {
                questions.add("请详细说明Spring Boot的自动配置原理。");
                questions.add("如何设计一个高并发的秒杀系统？需要考虑哪些问题？");
                questions.add("数据库索引的原理是什么？如何优化查询性能？");
            } else { // hard
                questions.add("请设计一个分布式事务解决方案，并比较不同方案的优缺点。");
                questions.add("如何设计一个支持千万级用户的消息推送系统？");
                questions.add("微服务架构中如何处理数据一致性问题？");
            }
        }
        
        return questions;
    }

    /**
     * 获取行为面试题目池
     */
    private List<String> getBehavioralQuestions() {
        return Arrays.asList(
            "请描述一个你在工作中遇到的最大挑战，以及你是如何解决的。",
            "谈谈你在团队协作中的经验，如何处理与同事的分歧？",
            "描述一个你主动学习新技术的经历，动机是什么？",
            "如果项目时间紧迫，你会如何平衡代码质量和交付时间？",
            "请谈谈你对技术领导力的理解，如何影响团队的技术决策？"
        );
    }

    /**
     * 获取系统设计题目池
     */
    private List<String> getSystemDesignQuestions(String difficulty) {
        List<String> questions = new ArrayList<>();
        
        if ("easy".equals(difficulty)) {
            questions.add("设计一个简单的聊天应用，需要支持用户注册、登录和消息发送。");
            questions.add("设计一个博客系统的数据库结构，包括用户、文章、评论等功能。");
        } else if ("medium".equals(difficulty)) {
            questions.add("设计一个类似于短链接服务（如bit.ly）的系统，需要考虑高并发和数据存储。");
            questions.add("设计一个在线协作文档系统，类似于Google Docs，如何处理多用户同时编辑？");
        } else { // hard
            questions.add("设计一个类似于微博的社交媒体平台，需要支持千万级用户和实时动态推送。");
            questions.add("设计一个分布式缓存系统，需要考虑数据分片、一致性哈希和故障恢复。");
        }
        
        return questions;
    }

    /**
     * 获取编程题目池
     */
    private List<String> getCodingQuestions(String difficulty) {
        List<String> questions = new ArrayList<>();
        
        if ("easy".equals(difficulty)) {
            questions.add("实现一个函数来反转字符串。");
            questions.add("判断一个字符串是否为回文。");
            questions.add("找出数组中两个数的和等于目标值的所有组合。");
        } else if ("medium".equals(difficulty)) {
            questions.add("实现一个LRU（最近最少使用）缓存。");
            questions.add("给定一个二叉树，返回其层次遍历的结果。");
            questions.add("实现一个算法来检测链表中是否有环。");
        } else { // hard
            questions.add("实现一个支持通配符的字符串匹配算法。");
            questions.add("设计并实现一个数据结构来支持以下操作：插入、删除、获取随机元素，所有操作的时间复杂度都是O(1)。");
            questions.add("给定一个表达式，实现一个计算器来计算其结果（支持+、-、*、/和括号）。");
        }
        
        return questions;
    }

    /**
     * 生成模拟反馈（回退机制）
     */
    private FeedbackDTO generateMockFeedback(AnswerRequestDTO request) {
        FeedbackDTO feedback = new FeedbackDTO();
        
        // 基于回答长度和关键词生成评分
        int score = calculateMockScore(request.getAnswer());
        feedback.setScore(score);
        
        // 生成评价
        feedback.setComment(generateMockComment(score, request.getInterviewType()));
        
        // 生成建议
        feedback.setSuggestions(generateMockSuggestions(score));
        
        return feedback;
    }

    /**
     * 计算模拟评分
     */
    private int calculateMockScore(String answer) {
        if (answer == null || answer.trim().length() < 20) {
            return ThreadLocalRandom.current().nextInt(3, 5); // 3-4分
        } else if (answer.length() < 100) {
            return ThreadLocalRandom.current().nextInt(5, 7); // 5-6分
        } else if (answer.length() < 200) {
            return ThreadLocalRandom.current().nextInt(6, 8); // 6-7分
        } else {
            return ThreadLocalRandom.current().nextInt(7, 10); // 7-9分
        }
    }

    /**
     * 生成模拟评价
     */
    private String generateMockComment(int score, String type) {
        String[] excellentComments = {
            "回答非常全面，展现了深厚的技术功底和清晰的逻辑思维。",
            "答案准确详细，能够从多个角度分析问题，体现了良好的技术理解。",
            "回答逻辑清晰，技术细节把握准确，展现出优秀的专业素养。"
        };
        
        String[] goodComments = {
            "回答基本正确，有一定的技术深度，但在某些细节上可以更加深入。",
            "答案覆盖了主要要点，逻辑比较清晰，但可以补充更多实际案例。",
            "回答展现了良好的基础知识，建议在实践应用方面进一步加强。"
        };
        
        String[] averageComments = {
            "回答涉及了一些关键点，但缺少深入分析，建议加强理论学习。",
            "答案基本符合要求，但在技术细节和逻辑性上还有提升空间。",
            "回答有一定的理解，但表达不够准确，需要加强专业术语的使用。"
        };
        
        String[] poorComments = {
            "回答过于简单，缺乏技术深度，建议加强相关知识的学习。",
            "答案偏离了题目要求，需要提高对问题的理解能力。",
            "回答显示出基础知识不够扎实，建议系统性地学习相关技术。"
        };
        
        if (score >= 8) {
            return excellentComments[ThreadLocalRandom.current().nextInt(excellentComments.length)];
        } else if (score >= 6) {
            return goodComments[ThreadLocalRandom.current().nextInt(goodComments.length)];
        } else if (score >= 4) {
            return averageComments[ThreadLocalRandom.current().nextInt(averageComments.length)];
        } else {
            return poorComments[ThreadLocalRandom.current().nextInt(poorComments.length)];
        }
    }

    /**
     * 生成模拟建议
     */
    private List<String> generateMockSuggestions(int score) {
        List<String> allSuggestions = Arrays.asList(
            "可以提供更具体的实际案例来支撑观点",
            "建议深入了解相关技术的底层原理",
            "可以从性能、安全、可维护性等多个角度分析",
            "建议补充对比不同方案的优缺点",
            "可以结合实际项目经验来阐述",
            "建议使用更准确的技术术语",
            "可以按照逻辑顺序重新组织答案结构",
            "建议提及相关的最佳实践"
        );
        
        List<String> suggestions = new ArrayList<>();
        int count = Math.max(2, Math.min(4, (10 - score) + 1));
        
        Collections.shuffle(allSuggestions);
        for (int i = 0; i < Math.min(count, allSuggestions.size()); i++) {
            suggestions.add(allSuggestions.get(i));
        }
        
        return suggestions;
    }

    // 辅助方法：获取显示文本
    private String getInterviewTypeText(String type) {
        switch (type) {
            case "technical": return "技术面试";
            case "behavioral": return "行为面试";
            case "system_design": return "系统设计";
            case "coding": return "编程面试";
            default: return "综合面试";
        }
    }

    private String getDifficultyText(String difficulty) {
        switch (difficulty) {
            case "easy": return "简单";
            case "medium": return "中等";
            case "hard": return "困难";
            default: return "中等";
        }
    }

    private String getPositionText(String position) {
        switch (position) {
            case "frontend": return "前端开发";
            case "backend": return "后端开发";
            case "fullstack": return "全栈开发";
            case "mobile": return "移动开发";
            case "devops": return "DevOps";
            default: return "软件开发";
        }
    }

    private String getExperienceText(String experience) {
        switch (experience) {
            case "junior": return "初级 (0-2年)";
            case "intermediate": return "中级 (2-5年)";
            case "senior": return "高级 (5年以上)";
            default: return "中级";
        }
    }

    /**
     * 生成面试总结
     */
    private SummaryDTO generateInterviewSummary(SummaryRequestDTO request) {
        SummaryDTO summary = new SummaryDTO();
        
        // 计算总体评分
        int totalScore = 0;
        int questionCount = 0;
        
        if (request.getHistory() != null) {
            for (InterviewHistoryDTO history : request.getHistory()) {
                if (history.getFeedback() != null && history.getFeedback().getScore() != null) {
                    totalScore += history.getFeedback().getScore();
                    questionCount++;
                }
            }
        }
        
        int overallScore = questionCount > 0 ? Math.round((float) totalScore / questionCount) : 5;
        summary.setOverallScore(overallScore);
        summary.setAnsweredQuestions(questionCount);
        summary.setTotalDuration(request.getInterviewDuration() != null ? request.getInterviewDuration() : 30);
        
        // 生成总结评价
        summary.setOverallComment(generateOverallComment(overallScore, request));
        
        // 生成能力评分
        Map<String, Integer> skillScores = new HashMap<>();
        skillScores.put("技术能力", adjustScore(overallScore, -1, 1));
        skillScores.put("逻辑思维", adjustScore(overallScore, -1, 1));
        skillScores.put("表达能力", adjustScore(overallScore, -1, 1));
        skillScores.put("问题分析", adjustScore(overallScore, -1, 1));
        skillScores.put("解决方案", adjustScore(overallScore, -1, 1));
        summary.setSkillScores(skillScores);
        
        // 生成优势和不足
        summary.setStrengths(generateStrengths(overallScore));
        summary.setWeaknesses(generateWeaknesses(overallScore));
        summary.setRecommendations(generateRecommendations(overallScore, request.getPosition()));
        
        return summary;
    }
    
    private int adjustScore(int baseScore, int minAdjust, int maxAdjust) {
        int adjustment = ThreadLocalRandom.current().nextInt(minAdjust, maxAdjust + 1);
        return Math.max(1, Math.min(10, baseScore + adjustment));
    }
    
    private String generateOverallComment(int score, SummaryRequestDTO request) {
        String position = getPositionText(request.getPosition());
        String experience = getExperienceText(request.getExperience());
        
        if (score >= 8) {
            return String.format("候选人在%s面试中表现优秀，展现了扎实的技术基础和良好的问题分析能力。" +
                    "回答逻辑清晰，技术深度适合%s的要求。建议进入下一轮面试。", position, experience);
        } else if (score >= 6) {
            return String.format("候选人在%s面试中表现良好，具备一定的技术能力。" +
                    "在部分问题上展现了正确的理解，但在深度和广度上还有提升空间。符合%s的基本要求。", 
                    position, experience);
        } else if (score >= 4) {
            return String.format("候选人对%s相关技术有基础了解，但在回答深度和准确性上需要加强。" +
                    "建议继续学习相关技术知识，提高技术水平后再次尝试%s职位。", position, experience);
        } else {
            return String.format("候选人在%s面试中表现不够理想，技术基础较为薄弱。" +
                    "建议系统性地学习相关技术知识，积累实践经验后再次申请%s职位。", position, experience);
        }
    }
    
    private List<String> generateStrengths(int score) {
        List<String> allStrengths = Arrays.asList(
            "回答逻辑清晰，思路条理分明",
            "技术基础扎实，概念理解准确",
            "能够结合实际场景分析问题",
            "表达能力较强，沟通顺畅",
            "对技术细节有较深入的理解",
            "具备良好的问题分析能力",
            "回答覆盖面广，知识面较宽",
            "能够提出创新性的解决方案"
        );
        
        List<String> strengths = new ArrayList<>();
        int count = Math.min(score / 2 + 1, 4);
        
        Collections.shuffle(allStrengths);
        for (int i = 0; i < Math.min(count, allStrengths.size()); i++) {
            strengths.add(allStrengths.get(i));
        }
        
        return strengths;
    }
    
    private List<String> generateWeaknesses(int score) {
        List<String> allWeaknesses = Arrays.asList(
            "部分回答缺乏技术深度",
            "对某些概念的理解还不够准确",
            "实际案例分享较少",
            "回答时间把控需要改善",
            "技术术语使用不够准确",
            "逻辑结构有待优化",
            "对新技术的了解有限",
            "缺乏系统性的思考方式"
        );
        
        List<String> weaknesses = new ArrayList<>();
        int count = Math.max(1, (10 - score) / 2 + 1);
        count = Math.min(count, 3);
        
        Collections.shuffle(allWeaknesses);
        for (int i = 0; i < Math.min(count, allWeaknesses.size()); i++) {
            weaknesses.add(allWeaknesses.get(i));
        }
        
        return weaknesses;
    }
    
    private List<String> generateRecommendations(int score, String position) {
        List<String> recommendations = new ArrayList<>();
        
        if (score < 6) {
            recommendations.add("建议系统性地学习" + getPositionText(position) + "相关的核心技术");
            recommendations.add("多做实际项目练习，积累实战经验");
            recommendations.add("关注行业最新技术动态和最佳实践");
        } else if (score < 8) {
            recommendations.add("继续深入学习技术细节和底层原理");
            recommendations.add("多参与开源项目或技术社区交流");
            recommendations.add("提高技术方案的设计和架构能力");
        } else {
            recommendations.add("可以考虑技术领导或架构师方向发展");
            recommendations.add("分享技术经验，帮助团队成长");
            recommendations.add("关注前沿技术，推动技术创新");
        }
        
        recommendations.add("加强沟通表达能力，提升面试技巧");
        
        return recommendations;
    }
}