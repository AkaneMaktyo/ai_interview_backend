package com.zsj.aiinterview.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 统一API响应类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    /**
     * 响应状态，true表示成功，false表示失败
     */
    private boolean success;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 数据总数（用于列表查询）
     */
    private Long count;
    
    /**
     * 成功响应（无数据）
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, "操作成功", null, null);
    }
    
    /**
     * 成功响应（带数据）
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "操作成功", data, null);
    }
    
    /**
     * 成功响应（带数据和消息）
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, null);
    }
    
    /**
     * 成功响应（带数据和计数）
     */
    public static <T> ApiResponse<T> success(T data, Long count) {
        return new ApiResponse<>(true, "查询成功", data, count);
    }
    
    /**
     * 成功响应（带数据、消息和计数）
     */
    public static <T> ApiResponse<T> success(T data, String message, Long count) {
        return new ApiResponse<>(true, message, data, count);
    }
    
    /**
     * 失败响应
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, null);
    }
    
    /**
     * 计数响应
     */
    public static <T> ApiResponse<T> count(Long count) {
        return new ApiResponse<>(true, "统计成功", null, count);
    }
}