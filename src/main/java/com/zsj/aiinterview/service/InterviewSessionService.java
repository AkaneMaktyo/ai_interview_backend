package com.zsj.aiinterview.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsj.aiinterview.entity.InterviewSession;
import com.zsj.aiinterview.mapper.InterviewSessionMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 面试会话服务 - 使用MyBatis Plus
 */
@Service
public class InterviewSessionService extends ServiceImpl<InterviewSessionMapper, InterviewSession> {

    /**
     * 查询所有面试会话
     */
    public List<InterviewSession> findAll() {
        return this.list();
    }

    /**
     * 根据ID查询面试会话
     */
    public Optional<InterviewSession> findById(Long id) {
        InterviewSession session = this.getById(id);
        return Optional.ofNullable(session);
    }

    /**
     * 根据用户ID查询面试会话
     */
    public List<InterviewSession> findByUserId(Long userId) {
        QueryWrapper<InterviewSession> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("created_at");
        return this.list(queryWrapper);
    }

    /**
     * 创建新面试会话
     */
    public InterviewSession createSession(InterviewSession session) {
        LocalDateTime now = LocalDateTime.now();
        session.setCreatedAt(now);
        session.setUpdatedAt(now);
        if (session.getStatus() == null) {
            session.setStatus("active");
        }
        if (session.getTotalQuestions() == null) {
            session.setTotalQuestions(0);
        }
        if (session.getAnsweredQuestions() == null) {
            session.setAnsweredQuestions(0);
        }
        if (session.getTotalScore() == null) {
            session.setTotalScore(0);
        }
        session.setStartedAt(now);
        
        this.save(session);
        return session;
    }

    /**
     * 更新面试会话
     */
    public InterviewSession updateSession(Long id, InterviewSession session) {
        session.setId(id);
        session.setUpdatedAt(LocalDateTime.now());
        this.updateById(session);
        return this.getById(id);
    }

    /**
     * 结束面试会话
     */
    public InterviewSession endSession(Long id) {
        InterviewSession session = new InterviewSession();
        session.setId(id);
        session.setStatus("completed");
        session.setEndedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        this.updateById(session);
        return this.getById(id);
    }

    /**
     * 根据用户ID查询活跃的面试会话
     */
    public List<InterviewSession> findActiveSessionsByUserId(Long userId) {
        QueryWrapper<InterviewSession> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("status", "active");
        queryWrapper.orderByDesc("created_at");
        return this.list(queryWrapper);
    }
}