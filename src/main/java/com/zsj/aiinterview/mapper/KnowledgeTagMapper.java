package com.zsj.aiinterview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zsj.aiinterview.entity.KnowledgeTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知识点标签Mapper接口
 */
@Mapper
public interface KnowledgeTagMapper extends BaseMapper<KnowledgeTag> {
    
}