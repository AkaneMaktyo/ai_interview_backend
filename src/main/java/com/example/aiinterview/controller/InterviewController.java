package com.example.aiinterview.controller;

import com.example.aiinterview.dto.*;
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
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RequiredArgsConstructor
public class InterviewController {

    private final HttpAiUtil httpAiUtil;
    private final SimpleAiUtil simpleAiUtil;
    private final Optional<DoubaoUtil> doubaoUtil;

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
            
            // 使用AI生成题目，这里先用模拟数据
            Map<String, Object> questionData = generateMockQuestion(request);
            
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
            
            // 使用AI评估回答，这里先用模拟数据
            FeedbackDTO feedback = generateMockFeedback(request);
            
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
        prompt.append("- 类型：").append(getInterviewTypeText(request.getInterviewType())).append("\n");
        prompt.append("- 难度：").append(getDifficultyText(request.getDifficulty())).append("\n");
        prompt.append("- 职位：").append(getPositionText(request.getPosition())).append("\n\n");
        
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
     * 生成模拟面试题目
     */
    private Map<String, Object> generateMockQuestion(QuestionRequestDTO request) {
        Map<String, Object> questionData = new HashMap<>();
        
        // 根据面试类型生成不同的题目
        String question = generateQuestionByType(request);
        
        questionData.put("question", question);
        questionData.put("type", request.getInterviewType());
        questionData.put("questionId", "mock_" + System.currentTimeMillis());
        
        return questionData;
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
     * 生成模拟反馈
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
}