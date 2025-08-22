package com.zsj.aiinterview.util;

import com.alibaba.fastjson.JSON;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionResult;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 豆包AI官方SDK工具类
 * 使用豆包官方API进行AI对话
 */
@Slf4j
@Component
public class DoubaoUtil {

    @Value("${doubao.api-key:f829290f-bb20-4692-904a-b812e0da770b}")
    private String apiKey;

    @Value("${doubao.base-url:https://ark.cn-beijing.volces.com/api/v3}")
    private String baseUrl;

    @Value("${doubao.model-id:doubao-pro-4k}")
    private String modelId;

    @Value("${doubao.thinking-model:doubao-1-5-thinking-vision-pro-250428}")
    private String thinkingModelId;

    @Value("${doubao.network-bot:bot-20250514171832-w4g9f}")
    private String networkBotId;

    private ArkService arkService;

    @PostConstruct
    public void init() {
        try {
            // 初始化豆包AI服务
            this.arkService = ArkService.builder()
                    .apiKey(apiKey)
                    .baseUrl(baseUrl)
                    .timeout(Duration.ofMinutes(3))
                    .build();
            
            log.info("豆包AI SDK初始化成功，模型ID: {}", modelId);
        } catch (Exception e) {
            log.error("豆包AI SDK初始化失败: ", e);
            // 不抛出异常，让应用继续启动
        }
    }

    /**
     * AI问答 - 标准模式
     *
     * @param question 问题内容
     * @param emitter  SSE发射器
     */
    public void chat(String question, SseEmitter emitter) {
        chatWithModel(question, emitter, modelId, false);
    }

    /**
     * AI问答 - 深度思考模式
     *
     * @param question 问题内容
     * @param emitter  SSE发射器
     */
    public void chatWithThinking(String question, SseEmitter emitter) {
        chatWithModel(question, emitter, thinkingModelId, true);
    }

    /**
     * AI问答 - 联网模式
     *
     * @param question 问题内容
     * @param emitter  SSE发射器
     */
    public void chatWithNetwork(String question, SseEmitter emitter) {
        // 使用联网机器人
        chatWithBot(question, emitter, networkBotId);
    }

    /**
     * 使用指定模型进行对话
     *
     * @param question    问题内容
     * @param emitter     SSE发射器
     * @param model       模型ID
     * @param isThinking  是否为思考模式
     */
    private void chatWithModel(String question, SseEmitter emitter, String model, boolean isThinking) {
        CompletableFuture.runAsync(() -> {
            try {
                if (arkService == null) {
                    sendError(emitter, "豆包AI服务未正确初始化，请检查配置");
                    return;
                }

                // 构建消息列表
                List<ChatMessage> messages = new ArrayList<>();
                
                // 系统提示词
                String systemPrompt = isThinking ? 
                    "你是一个专业的面试官助手，具有深度思考能力。请仔细分析问题，提供详细、专业的回答。" :
                    "你是一个专业的面试官助手，请提供准确、有用的回答。";
                    
                messages.add(ChatMessage.builder()
                    .role(ChatMessageRole.SYSTEM)
                    .content(systemPrompt)
                    .build());

                messages.add(ChatMessage.builder()
                    .role(ChatMessageRole.USER)
                    .content(question)
                    .build());

                // 构建请求
                ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(messages)
                    .maxTokens(2000)
                    .temperature(0.7)
                    .topP(0.9)
                    .stream(false) // 先使用非流式，确保稳定性
                    .build();

                log.info("调用豆包AI，模型: {}, 问题: {}", model, question);

                // 调用API
                ChatCompletionResult result = arkService.createChatCompletion(request);
                
                if (result != null && result.getChoices() != null && !result.getChoices().isEmpty()) {
                    String response = result.getChoices().get(0).getMessage().getContent().toString();
                    
                    // 分段发送响应，模拟流式输出效果
                    sendResponseInChunks(emitter, response);
                    
                    log.info("豆包AI响应成功，模型: {}", model);
                } else {
                    sendError(emitter, "豆包AI返回空响应");
                }

            } catch (Exception e) {
                log.error("豆包AI调用异常: {}", e.getMessage());
                sendError(emitter, "豆包AI调用失败: " + e.getMessage());
            }
        });
    }

    /**
     * 使用机器人进行对话（联网功能）
     *
     * @param question 问题内容
     * @param emitter  SSE发射器
     * @param botId    机器人ID
     */
    private void chatWithBot(String question, SseEmitter emitter, String botId) {
        // 机器人模式的实现，需要使用不同的API endpoint
        // 这里先用标准模式，后续可以扩展
        chatWithModel(question + "（请联网搜索最新信息）", emitter, modelId, false);
    }

    /**
     * 分段发送响应内容，模拟流式输出
     *
     * @param emitter  SSE发射器
     * @param response 完整响应内容
     */
    private void sendResponseInChunks(SseEmitter emitter, String response) {
        try {
            // 按句子分割，模拟打字效果
            String[] sentences = response.split("(?<=[。！？\\.])\s*");
            
            for (String sentence : sentences) {
                if (!sentence.trim().isEmpty()) {
                    sendContent(emitter, sentence + " ");
                    Thread.sleep(300); // 模拟打字延迟
                }
            }
            
            emitter.complete();
            
        } catch (Exception e) {
            log.error("发送响应异常: ", e);
            emitter.completeWithError(e);
        }
    }

    /**
     * 发送内容到SSE
     *
     * @param emitter SSE发射器
     * @param content 内容
     */
    private void sendContent(SseEmitter emitter, String content) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("content", content);
            emitter.send(SseEmitter.event().data(JSON.toJSONString(map)));
        } catch (IOException e) {
            log.error("SSE发送内容异常: ", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送错误信息
     *
     * @param emitter SSE发射器
     * @param error   错误信息
     */
    private void sendError(SseEmitter emitter, String error) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("error", error);
            emitter.send(SseEmitter.event().data(JSON.toJSONString(map)));
            emitter.complete();
        } catch (Exception e) {
            log.error("发送错误信息异常: ", e);
            emitter.completeWithError(e);
        }
    }

    /**
     * 获取服务状态
     *
     * @return 状态信息
     */
    public String getStatus() {
        if (arkService == null) {
            return "豆包AI服务未初始化";
        }
        return "豆包AI服务运行中（官方SDK）- 模型: " + modelId;
    }

    /**
     * 测试AI连接
     *
     * @return 测试结果
     */
    public String testConnection() {
        try {
            if (arkService == null) {
                return "连接失败: 服务未初始化";
            }

            // 发送一个简单的测试请求
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(ChatMessage.builder()
                .role(ChatMessageRole.USER)
                .content("你好，请回复'连接成功'")
                .build());

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(modelId)
                .messages(messages)
                .maxTokens(50)
                .temperature(0.1)
                .build();

            ChatCompletionResult result = arkService.createChatCompletion(request);
            
            if (result != null && result.getChoices() != null && !result.getChoices().isEmpty()) {
                return "连接成功: " + result.getChoices().get(0).getMessage().getContent();
            } else {
                return "连接失败: 无响应";
            }
            
        } catch (Exception e) {
            return "连接失败: " + e.getMessage();
        }
    }
}