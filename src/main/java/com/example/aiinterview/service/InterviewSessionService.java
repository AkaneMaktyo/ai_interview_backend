package com.example.aiinterview.service;

import com.example.aiinterview.entity.InterviewSession;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 面试会话服务
 */
@Service
@RequiredArgsConstructor
public class InterviewSessionService {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<InterviewSession> sessionRowMapper = new RowMapper<InterviewSession>() {
        @Override
        public InterviewSession mapRow(ResultSet rs, int rowNum) throws SQLException {
            InterviewSession session = new InterviewSession();
            session.setId(rs.getLong("id"));
            session.setUserId(rs.getLong("user_id"));
            session.setSessionName(rs.getString("session_name"));
            session.setInterviewType(rs.getString("interview_type"));
            session.setDifficulty(rs.getString("difficulty"));
            session.setPosition(rs.getString("position"));
            session.setStatus(rs.getString("status"));
            session.setTotalQuestions(rs.getInt("total_questions"));
            session.setAnsweredQuestions(rs.getInt("answered_questions"));
            session.setTotalScore(rs.getInt("total_score"));
            session.setStartedAt(rs.getObject("started_at", LocalDateTime.class));
            session.setEndedAt(rs.getObject("ended_at", LocalDateTime.class));
            session.setSessionData(rs.getString("session_data"));
            session.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
            session.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
            return session;
        }
    };

    /**
     * 根据用户ID查询会话
     */
    public List<InterviewSession> findByUserId(Long userId) {
        String sql = "SELECT id, user_id, session_name, interview_type, difficulty, position, status, " +
                    "total_questions, answered_questions, total_score, started_at, ended_at, " +
                    "session_data, created_at, updated_at FROM interview_sessions WHERE user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, sessionRowMapper, userId);
    }

    /**
     * 根据ID查询会话
     */
    public Optional<InterviewSession> findById(Long id) {
        String sql = "SELECT id, user_id, session_name, interview_type, difficulty, position, status, " +
                    "total_questions, answered_questions, total_score, started_at, ended_at, " +
                    "session_data, created_at, updated_at FROM interview_sessions WHERE id = ?";
        try {
            InterviewSession session = jdbcTemplate.queryForObject(sql, sessionRowMapper, id);
            return Optional.ofNullable(session);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * 创建新会话
     */
    public InterviewSession save(InterviewSession session) {
        LocalDateTime now = LocalDateTime.now();
        session.setCreatedAt(now);
        session.setUpdatedAt(now);
        
        String sql = "INSERT INTO interview_sessions (user_id, session_name, interview_type, difficulty, " +
                    "position, status, total_questions, answered_questions, total_score, started_at, " +
                    "session_data, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(sql,
            session.getUserId(), session.getSessionName(), session.getInterviewType(),
            session.getDifficulty(), session.getPosition(), session.getStatus(),
            session.getTotalQuestions(), session.getAnsweredQuestions(), session.getTotalScore(),
            session.getStartedAt(), session.getSessionData(), session.getCreatedAt(), session.getUpdatedAt()
        );
        
        // 获取生成的ID
        String findSql = "SELECT id, user_id, session_name, interview_type, difficulty, position, status, " +
                        "total_questions, answered_questions, total_score, started_at, ended_at, " +
                        "session_data, created_at, updated_at FROM interview_sessions WHERE user_id = ? AND session_name = ? ORDER BY created_at DESC LIMIT 1";
        return jdbcTemplate.queryForObject(findSql, sessionRowMapper, session.getUserId(), session.getSessionName());
    }

    /**
     * 更新会话信息
     */
    public InterviewSession update(Long id, InterviewSession session) {
        session.setUpdatedAt(LocalDateTime.now());
        String sql = "UPDATE interview_sessions SET session_name = ?, status = ?, total_questions = ?, " +
                    "answered_questions = ?, total_score = ?, ended_at = ?, session_data = ?, updated_at = ? WHERE id = ?";
        
        jdbcTemplate.update(sql,
            session.getSessionName(), session.getStatus(), session.getTotalQuestions(),
            session.getAnsweredQuestions(), session.getTotalScore(), session.getEndedAt(),
            session.getSessionData(), session.getUpdatedAt(), id
        );
        
        return findById(id).orElse(null);
    }

    /**
     * 结束会话
     */
    public InterviewSession endSession(Long id) {
        LocalDateTime now = LocalDateTime.now();
        String sql = "UPDATE interview_sessions SET status = 'completed', ended_at = ?, updated_at = ? WHERE id = ?";
        jdbcTemplate.update(sql, now, now, id);
        return findById(id).orElse(null);
    }

    /**
     * 获取用户的活跃会话
     */
    public List<InterviewSession> findActiveSessionsByUserId(Long userId) {
        String sql = "SELECT id, user_id, session_name, interview_type, difficulty, position, status, " +
                    "total_questions, answered_questions, total_score, started_at, ended_at, " +
                    "session_data, created_at, updated_at FROM interview_sessions WHERE user_id = ? AND status = 'active' ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, sessionRowMapper, userId);
    }
}