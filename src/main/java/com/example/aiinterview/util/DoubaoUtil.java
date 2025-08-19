package com.example.aiinterview.util;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.example.aiinterview.config.RedisCache;
import com.volcengine.ark.runtime.model.bot.completion.chat.BotChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 豆包AI工具类
 * 支持深度思考和联网模式
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnClass(ArkService.class) // 只有当ArkService类存在时才创建Bean
public class DoubaoUtil {

    private static final String API_KEY = "f829290f-bb20-4692-904a-b812e0da770b";
    // 普通问答模型
    private static final String MODEL = "doubao-1-5-pro-32k-250115";
    // 联网模型
    private static final String NETWORK_BOT = "bot-20250514171832-w4g9f";
    // 深度问答模型
    private static final String DEEP_THINK = "doubao-1-5-thinking-vision-pro-250428";
    
    private final RedisCache redisCache;
    
    private static final ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);
    private static final Dispatcher dispatcher = new Dispatcher();
    private static final ArkService service = ArkService.builder()
            .dispatcher(dispatcher)
            .connectionPool(connectionPool)
            .apiKey(API_KEY)
            .build();

    /**
     * 历史对话内容构建
     *
     * @param question 当前问题
     * @param list     历史对话列表
     * @return 构建后的消息列表
     */
    private List<ChatMessage> buildMessages(String question, List<String> list) {
        List<ChatMessage> messages = new ArrayList<>();
        if (list != null) {
            list.forEach(x -> {
                String role = x.substring(0, x.indexOf(":"));
                ChatMessage message = ChatMessage.builder()
                        .role(ChatMessageRole.valueOf(role))
                        .content(x.substring(x.indexOf(":") + 1))
                        .build();
                messages.add(message);
            });
        }
        // 加上本次的提问
        messages.add(ChatMessage.builder()
                .role(ChatMessageRole.USER)
                .content(question)
                .build());
        return messages;
    }

    /**
     * 单个问题询问（默认使用深度思考模式）
     *
     * @param question 问题内容
     * @param emitter  SSE发射器
     */
    public void singleQuestion(String question, SseEmitter emitter) {
        singleQuestion(question, emitter, true);
    }

    /**
     * 单个问题询问
     *
     * @param question        问题内容
     * @param emitter         SSE发射器
     * @param useDeepThinking 是否使用深度思考模式
     */
    public void singleQuestion(String question, SseEmitter emitter, boolean useDeepThinking) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(ChatMessage.builder()
                .role(ChatMessageRole.USER)
                .content(question)
                .build());

        // 用于收集流式响应的内容
        StringBuilder sb = new StringBuilder();
        
        try {
            if (useDeepThinking) {
                // 深度思考模式
                handleDeepThinking(messages, emitter, sb);
            } else {
                // 联网模式
                handleNetworkMode(messages, emitter, sb);
            }
            emitter.complete();
        } catch (Exception e) {
            log.error("AI问答异常: ", e);
            emitter.completeWithError(e);
        }
    }

    /**
     * 处理深度思考模式
     */
    private void handleDeepThinking(List<ChatMessage> messages, SseEmitter emitter, StringBuilder sb) {
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(DEEP_THINK)
                .messages(messages)
                .stream(true)
                .build();
        
        try {
            service.streamChatCompletion(request)
                    .doOnError(e -> {
                        log.error("Stream chat error: ", e);
                        emitter.completeWithError(e);
                    })
                    .blockingIterable()
                    .forEach(j -> {
                        j.getChoices().forEach(choice -> {
                            String content = choice.getMessage().getContent().toString();
                            if (content != null && !content.isEmpty()) {
                                sendContent(emitter, content);
                                sb.append(content);
                            }
                        });
                    });
        } catch (Exception e) {
            log.error("Deep thinking chat error: ", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 处理联网模式
     */
    private void handleNetworkMode(List<ChatMessage> messages, SseEmitter emitter, StringBuilder sb) {
        BotChatCompletionRequest networkRequest = BotChatCompletionRequest.builder()
                .botId(NETWORK_BOT)
                .messages(messages)
                .stream(true)
                .build();

        try {
            service.streamBotChatCompletion(networkRequest)
                    .doOnError(e -> {
                        log.error("Stream bot chat error: ", e);
                        emitter.completeWithError(e);
                    })
                    .blockingForEach(choice -> {
                        // 处理 references（如果有）
                        if (choice.getReferences() != null && !choice.getReferences().isEmpty()) {
                            List<String> urls = new ArrayList<>();
                            choice.getReferences().forEach(ref -> urls.add(ref.getUrl()));
                            sendReferences(emitter, urls);
                        }

                        // 处理消息内容
                        if (!choice.getChoices().isEmpty()) {
                            String content = choice.getChoices().get(0).getMessage().getContent().toString();
                            sendContent(emitter, content);
                            sb.append(content);
                        }
                    });
        } catch (Exception e) {
            log.error("Network chat error: ", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送内容到SSE
     */
    private void sendContent(SseEmitter emitter, String content) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("content", content);
            SseEmitter.SseEventBuilder eventBuilder = SseEmitter.event()
                    .data(JSON.toJSONString(map));
            emitter.send(eventBuilder);
        } catch (IOException e) {
            log.error("SseEmitter send content error: ", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送引用链接到SSE
     */
    private void sendReferences(SseEmitter emitter, List<String> urls) {
        try {
            Map<String, Object> refMap = new HashMap<>();
            refMap.put("references", urls);
            SseEmitter.SseEventBuilder eventBuilder = SseEmitter.event()
                    .data(JSON.toJSONString(refMap));
            emitter.send(eventBuilder);
        } catch (IOException e) {
            log.error("SseEmitter send references error: ", e);
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 同步获取AI回答（用于面试功能）
     *
     * @param question 问题内容
     * @param useDeepThinking 是否使用深度思考模式
     * @return AI回答内容
     */
    public String getSyncResponse(String question, boolean useDeepThinking) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(ChatMessage.builder()
                .role(ChatMessageRole.USER)
                .content(question)
                .build());

        StringBuilder response = new StringBuilder();
        
        try {
            if (useDeepThinking) {
                response.append(getSyncDeepThinkingResponse(messages));
            } else {
                response.append(getSyncNetworkResponse(messages));
            }
        } catch (Exception e) {
            log.error("同步AI问答异常: ", e);
            throw new RuntimeException("AI服务调用失败: " + e.getMessage());
        }
        
        return response.toString();
    }

    /**
     * 同步深度思考模式
     */
    private String getSyncDeepThinkingResponse(List<ChatMessage> messages) {
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(DEEP_THINK)
                .messages(messages)
                .stream(false)  // 非流式
                .build();
        
        StringBuilder response = new StringBuilder();
        try {
            service.createChatCompletion(request)
                    .getChoices()
                    .forEach(choice -> {
                        String content = choice.getMessage().getContent().toString();
                        if (content != null && !content.isEmpty()) {
                            response.append(content);
                        }
                    });
        } catch (Exception e) {
            log.error("同步深度思考模式调用失败: ", e);
            throw new RuntimeException(e);
        }
        
        return response.toString();
    }

    /**
     * 同步联网模式
     */
    private String getSyncNetworkResponse(List<ChatMessage> messages) {
        BotChatCompletionRequest networkRequest = BotChatCompletionRequest.builder()
                .botId(NETWORK_BOT)
                .messages(messages)
                .stream(false)  // 非流式
                .build();

        StringBuilder response = new StringBuilder();
        try {
            service.createBotChatCompletion(networkRequest)
                    .getChoices()
                    .forEach(choice -> {
                        String content = choice.getMessage().getContent().toString();
                        if (content != null && !content.isEmpty()) {
                            response.append(content);
                        }
                    });
        } catch (Exception e) {
            log.error("同步联网模式调用失败: ", e);
            throw new RuntimeException(e);
        }
        
        return response.toString();
    }
}