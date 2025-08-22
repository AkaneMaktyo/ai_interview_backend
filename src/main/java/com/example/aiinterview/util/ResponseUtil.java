package com.example.aiinterview.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 响应工具类
 */
public class ResponseUtil {
    
    /**
     * 成功响应
     */
    public static Map<String, Object> success(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        return response;
    }
    
    /**
     * 成功响应（带消息）
     */
    public static Map<String, Object> success(Object data, String message) {
        Map<String, Object> response = success(data);
        response.put("message", message);
        return response;
    }
    
    /**
     * 成功响应（带计数）
     */
    public static Map<String, Object> successWithCount(Object data, int count) {
        Map<String, Object> response = success(data);
        response.put("count", count);
        return response;
    }
    
    /**
     * 错误响应
     */
    public static Map<String, Object> error(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        return response;
    }
    
    /**
     * 统计响应
     */
    public static Map<String, Object> count(long count) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", count);
        return response;
    }
}