package com.zsj.aiinterview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zsj.aiinterview.entity.AnswerRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 答题记录Mapper接口
 */
@Mapper
public interface AnswerRecordMapper extends BaseMapper<AnswerRecord> {
    
}