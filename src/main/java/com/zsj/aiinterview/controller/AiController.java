package com.zsj.aiinterview.controller;

import com.zsj.aiinterview.util.DoubaoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI对话控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final DoubaoUtil doubaoUtil;

    // 存储SSE连接
    private final ConcurrentHashMap<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    /**
     * AI问答 - 标准模式
     *
     * @param question 问题内容
     * @return SSE流
     */
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@RequestParam String question) {
        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L); // 5分钟超时
        
        String sessionId = "chat_" + System.currentTimeMillis();
        sseEmitters.put(sessionId, emitter);
        
        // 设置连接关闭时的清理
        emitter.onCompletion(() -> sseEmitters.remove(sessionId));
        emitter.onTimeout(() -> {
            sseEmitters.remove(sessionId);
            emitter.complete();
        });
        emitter.onError((throwable) -> {
            log.error("SSE连接异常: ", throwable);
            sseEmitters.remove(sessionId);
        });

        // 调用豆包AI
        doubaoUtil.chat(question, emitter);
        
        return emitter;
    }

    /**
     * AI问答 - 深度思考模式
     *
     * @param question 问题内容
     * @return SSE流
     */
    @PostMapping(value = "/chat/thinking", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatWithThinking(@RequestParam String question) {
        SseEmitter emitter = new SseEmitter(10 * 60 * 1000L); // 10分钟超时，思考模式需要更多时间
        
        String sessionId = "thinking_" + System.currentTimeMillis();
        sseEmitters.put(sessionId, emitter);
        
        emitter.onCompletion(() -> sseEmitters.remove(sessionId));
        emitter.onTimeout(() -> {
            sseEmitters.remove(sessionId);
            emitter.complete();
        });
        emitter.onError((throwable) -> {
            log.error("SSE连接异常: ", throwable);
            sseEmitters.remove(sessionId);
        });

        // 调用豆包AI深度思考模式
        doubaoUtil.chatWithThinking(question, emitter);
        
        return emitter;
    }

    /**
     * AI问答 - 联网模式
     *
     * @param question 问题内容
     * @return SSE流
     */
    @PostMapping(value = "/chat/network", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatWithNetwork(@RequestParam String question) {
        SseEmitter emitter = new SseEmitter(10 * 60 * 1000L); // 10分钟超时
        
        String sessionId = "network_" + System.currentTimeMillis();
        sseEmitters.put(sessionId, emitter);
        
        emitter.onCompletion(() -> sseEmitters.remove(sessionId));
        emitter.onTimeout(() -> {
            sseEmitters.remove(sessionId);
            emitter.complete();
        });
        emitter.onError((throwable) -> {
            log.error("SSE连接异常: ", throwable);
            sseEmitters.remove(sessionId);
        });

        // 调用豆包AI联网模式
        doubaoUtil.chatWithNetwork(question, emitter);
        
        return emitter;
    }

    /**
     * 获取AI服务状态
     *
     * @return 状态信息
     */
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("status", doubaoUtil.getStatus());
        response.put("activeConnections", sseEmitters.size());
        response.put("message", "豆包AI服务运行中");
        return response;
    }

    /**
     * 测试AI连接
     *
     * @return 测试结果
     */
    @PostMapping("/test")
    public Map<String, Object> testConnection() {
        try {
            String result = doubaoUtil.testConnection();
            Map<String, Object> response = new HashMap<>();
            
            if (result.startsWith("连接成功")) {
                response.put("success", true);
                response.put("message", result);
            } else {
                response.put("success", false);
                response.put("error", result);
            }
            
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "测试连接异常: " + e.getMessage());
            return response;
        }
    }

    /**
     * 获取当前活跃连接数
     *
     * @return 连接统计
     */
    @GetMapping("/connections")
    public Map<String, Object> getConnections() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("activeConnections", sseEmitters.size());
        response.put("connectionIds", sseEmitters.keySet());
        return response;
    }
}