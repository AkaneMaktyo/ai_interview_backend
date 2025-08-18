package com.example.aiinterview.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.aiinterview.config.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * HTTP方式调用AI接口的工具类
 * 替代豆包SDK，直接使用HTTP请求
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpAiUtil {

    private final RedisCache redisCache;
    private WebClient webClient;

    @PostConstruct
    private void init() {
        this.webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "AI-Interview-Backend/1.0")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }

    /**
     * AI问答（使用HTTP方式）
     *
     * @param question 问题内容
     * @param emitter  SSE发射器
     * @param useAdvancedMode 是否使用高级模式
     */
    public void chatWithHttp(String question, SseEmitter emitter, boolean useAdvancedMode) {
        CompletableFuture.runAsync(() -> {
            try {
                // 模拟AI处理过程
                Thread.sleep(1000);
                
                String modeDesc = useAdvancedMode ? "高级思考模式" : "标准模式";
                String response = String.format(
                    "【%s响应】\n\n" +
                    "您的问题：%s\n\n" +
                    "AI回答：这是一个基于HTTP调用的AI响应示例。" +
                    "当前系统已成功集成AI功能，可以处理各种问题。\n\n" +
                    "注意：这是模拟响应，实际部署时需要配置真实的AI API密钥。",
                    modeDesc, question
                );
                
                // 分段发送，模拟流式响应
                String[] sentences = response.split("\n");
                for (String sentence : sentences) {
                    if (!sentence.trim().isEmpty()) {
                        sendContent(emitter, sentence + "\n");
                        Thread.sleep(300); // 模拟打字效果
                    }
                }
                
                emitter.complete();
                log.info("AI问答完成，问题: {}, 模式: {}", question, modeDesc);
                
            } catch (Exception e) {
                log.error("HTTP AI问答异常: ", e);
                try {
                    sendContent(emitter, "抱歉，AI服务暂时不可用，请稍后重试。");
                    emitter.complete();
                } catch (Exception ex) {
                    emitter.completeWithError(ex);
                }
            }
        });
    }

    /**
     * 调用实际的AI API（示例方法，需要配置真实API）
     */
    private void callRealAiApi(String question, SseEmitter emitter) {
        // 这里可以配置真实的AI API调用
        // 比如OpenAI、百度文心、阿里通义千问等
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("prompt", question);
        requestBody.put("max_tokens", 1000);
        requestBody.put("temperature", 0.7);
        
        // 示例：调用某个AI API
        /*
        webClient.post()
                .uri("https://api.example.com/v1/chat/completions")
                .header("Authorization", "Bearer YOUR_API_KEY")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .subscribe(
                    response -> {
                        // 处理响应
                        handleApiResponse(response, emitter);
                    },
                    error -> {
                        log.error("AI API调用失败: ", error);
                        emitter.completeWithError(error);
                    }
                );
        */
    }

    /**
     * 发送内容到SSE
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
     * 获取服务状态
     */
    public String getStatus() {
        return "HTTP AI服务运行中（可连接真实AI API）";
    }
}