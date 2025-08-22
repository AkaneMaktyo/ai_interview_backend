package com.zsj.aiinterview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zsj.aiinterview.entity.InterviewSession;
import org.apache.ibatis.annotations.Mapper;

/**
 * 面试会话Mapper接口
 */
@Mapper
public interface InterviewSessionMapper extends BaseMapper<InterviewSession> {
    
}