package com.example.aiinterview.dto;

import lombok.Data;

/**
 * 通用API响应DTO
 */
@Data
public class ApiResponseDTO<T> {
    /** 是否成功 */
    private Boolean success;
    /** 响应数据 */
    private T data;
    /** 错误消息 */
    private String message;
    /** 响应码 */
    private Integer code;
    
    public static <T> ApiResponseDTO<T> success(T data) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.setSuccess(true);
        response.setData(data);
        response.setCode(200);
        return response;
    }
    
    public static <T> ApiResponseDTO<T> error(String message) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setCode(500);
        return response;
    }
    
    public static <T> ApiResponseDTO<T> error(Integer code, String message) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setCode(code);
        return response;
    }
}