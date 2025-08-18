package com.example.aiinterview.util;

import com.alibaba.fastjson.JSON;
import com.example.aiinterview.config.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 简化版AI工具类
 * 如果豆包SDK依赖有问题，可以使用此类作为备用方案
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleAiUtil {

    private final RedisCache redisCache;
    private RestTemplate restTemplate;

    @PostConstruct
    private void init() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 简单的AI问答（模拟响应）
     *
     * @param question 问题内容
     * @param emitter  SSE发射器
     */
    public void simpleQuestion(String question, SseEmitter emitter) {
        // 异步处理，避免阻塞
        CompletableFuture.runAsync(() -> {
            try {
                // 模拟AI思考过程
                Thread.sleep(1000);
                
                String response = "这是一个模拟的AI回答：" + question + 
                    "\n\n由于豆包AI SDK依赖问题，当前使用模拟响应。请检查网络连接和依赖配置。";
                
                // 分段发送响应，模拟流式输出
                String[] parts = response.split("\\n");
                for (String part : parts) {
                    if (!part.trim().isEmpty()) {
                        sendContent(emitter, part + "\n");
                        Thread.sleep(500); // 模拟打字效果
                    }
                }
                
                emitter.complete();
            } catch (Exception e) {
                log.error("模拟AI问答异常: ", e);
                emitter.completeWithError(e);
            }
        });
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
        return "简化版AI服务运行中（模拟模式）";
    }
}