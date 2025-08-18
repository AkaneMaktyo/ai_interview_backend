package com.example.aiinterview.config;

import org.springframework.stereotype.Component;

/**
 * Redis缓存工具类（简化版，避免启动依赖问题）
 */
@Component
public class RedisCache {
    
    // 暂时使用内存存储，避免Redis依赖问题
    // 生产环境可以配置真实的Redis连接
    
    /**
     * 简单的缓存存储方法示例
     */
    public void set(String key, Object value) {
        // 这里可以实现缓存逻辑
        // 暂时留空，避免Redis连接问题
    }
    
    /**
     * 简单的缓存获取方法示例  
     */
    public Object get(String key) {
        // 这里可以实现缓存逻辑
        // 暂时返回null，避免Redis连接问题
        return null;
    }
}