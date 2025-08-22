package com.zsj.aiinterview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zsj.aiinterview.entity.Question;
import org.apache.ibatis.annotations.Mapper;

/**
 * 题目Mapper接口
 */
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
    
}