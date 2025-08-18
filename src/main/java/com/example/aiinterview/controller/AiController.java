package com.example.aiinterview.controller;

import com.example.aiinterview.util.DoubaoUtil;
import com.example.aiinterview.util.HttpAiUtil;
import com.example.aiinterview.util.SimpleAiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * AI问答接口控制器
 */
@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AiController {

    private final HttpAiUtil httpAiUtil;
    private final SimpleAiUtil simpleAiUtil;
    private final Optional<DoubaoUtil> doubaoUtil;

    /**
     * AI问答接口（深度思考模式）- 优先使用豆包AI
     *
     * @param question 问题内容
     * @return SSE流式响应
     */
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@RequestParam String question) {
        if (doubaoUtil.isPresent()) {
            return createDoubaoSseEmitter(question, true);
        } else {
            return createHttpSseEmitter(question, true);
        }
    }

    /**
     * AI问答接口（联网模式）- 优先使用豆包AI
     *
     * @param question 问题内容
     * @return SSE流式响应
     */
    @PostMapping(value = "/chat-network", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatNetwork(@RequestParam String question) {
        if (doubaoUtil.isPresent()) {
            return createDoubaoSseEmitter(question, false);
        } else {
            return createHttpSseEmitter(question, false);
        }
    }

    /**
     * HTTP模式AI问答接口
     *
     * @param question 问题内容
     * @return SSE流式响应
     */
    @PostMapping(value = "/chat-http", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatHttp(@RequestParam String question) {
        return createHttpSseEmitter(question, true);
    }

    /**
     * 简化版AI问答接口（本地模拟）
     *
     * @param question 问题内容
     * @return SSE流式响应
     */
    @PostMapping(value = "/chat-simple", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatSimple(@RequestParam String question) {
        return createSimpleSseEmitter(question);
    }

    /**
     * 创建豆包AI的SSE发射器
     */
    private SseEmitter createDoubaoSseEmitter(String question, boolean useDeepThinking) {
        SseEmitter emitter = new SseEmitter(TimeUnit.MINUTES.toMillis(10));
        
        emitter.onCompletion(() -> System.out.println("豆包AI对话完成"));
        emitter.onTimeout(() -> System.out.println("豆包AI对话超时"));
        emitter.onError(throwable -> System.out.println("豆包AI对话异常: " + throwable.getMessage()));

        new Thread(() -> {
            try {
                doubaoUtil.get().singleQuestion(question, emitter, useDeepThinking);
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

    /**
     * 创建HTTP AI的SSE发射器
     */
    private SseEmitter createHttpSseEmitter(String question, boolean useAdvancedMode) {
        SseEmitter emitter = new SseEmitter(TimeUnit.MINUTES.toMillis(10));
        
        emitter.onCompletion(() -> System.out.println("HTTP AI对话完成"));
        emitter.onTimeout(() -> System.out.println("HTTP AI对话超时"));
        emitter.onError(throwable -> System.out.println("HTTP AI对话异常: " + throwable.getMessage()));

        httpAiUtil.chatWithHttp(question, emitter, useAdvancedMode);
        return emitter;
    }

    /**
     * 创建简化版AI的SSE发射器
     */
    private SseEmitter createSimpleSseEmitter(String question) {
        SseEmitter emitter = new SseEmitter(TimeUnit.MINUTES.toMillis(5));
        
        emitter.onCompletion(() -> System.out.println("简化AI对话完成"));
        emitter.onTimeout(() -> System.out.println("简化AI对话超时"));
        emitter.onError(throwable -> System.out.println("简化AI对话异常: " + throwable.getMessage()));

        simpleAiUtil.simpleQuestion(question, emitter);
        return emitter;
    }

    /**
     * 获取AI服务状态
     *
     * @return 服务状态信息
     */
    @GetMapping("/status")
    public String getStatus() {
        StringBuilder status = new StringBuilder("AI服务状态:\n");
        
        if (doubaoUtil.isPresent()) {
            status.append("✅ 豆包AI: 可用\n");
        } else {
            status.append("❌ 豆包AI: 不可用\n");
        }
        
        status.append("✅ HTTP模式: ").append(httpAiUtil.getStatus()).append("\n");
        status.append("✅ 简化模式: ").append(simpleAiUtil.getStatus());
        
        return status.toString();
    }
}