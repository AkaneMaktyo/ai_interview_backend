package com.example.aiinterview.service;

import com.example.aiinterview.entity.AnswerRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 答题记录服务
 */
@Service
@RequiredArgsConstructor
public class AnswerRecordService {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<AnswerRecord> recordRowMapper = new RowMapper<AnswerRecord>() {
        @Override
        public AnswerRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
            AnswerRecord record = new AnswerRecord();
            record.setId(rs.getLong("id"));
            record.setUserId(rs.getLong("user_id"));
            record.setQuestionId(rs.getLong("question_id"));
            record.setUserAnswer(rs.getString("user_answer"));
            record.setScore(rs.getObject("score", Integer.class));
            record.setInterviewType(rs.getString("interview_type"));
            record.setDifficulty(rs.getString("difficulty"));
            record.setPosition(rs.getString("position"));
            record.setTimeSpent(rs.getObject("time_spent", Integer.class));
            record.setAttemptCount(rs.getInt("attempt_count"));
            record.setStatus(rs.getString("status"));
            record.setAiEvaluation(rs.getString("ai_evaluation"));
            record.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
            record.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
            return record;
        }
    };

    /**
     * 根据用户ID查询答题记录
     */
    public List<AnswerRecord> findByUserId(Long userId) {
        String sql = "SELECT id, user_id, question_id, user_answer, score, interview_type, difficulty, " +
                    "position, time_spent, attempt_count, status, ai_evaluation, created_at, updated_at " +
                    "FROM answer_records WHERE user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, recordRowMapper, userId);
    }

    /**
     * 根据ID查询答题记录
     */
    public Optional<AnswerRecord> findById(Long id) {
        String sql = "SELECT id, user_id, question_id, user_answer, score, interview_type, difficulty, " +
                    "position, time_spent, attempt_count, status, ai_evaluation, created_at, updated_at " +
                    "FROM answer_records WHERE id = ?";
        try {
            AnswerRecord record = jdbcTemplate.queryForObject(sql, recordRowMapper, id);
            return Optional.ofNullable(record);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * 创建新答题记录
     */
    public AnswerRecord save(AnswerRecord record) {
        LocalDateTime now = LocalDateTime.now();
        record.setCreatedAt(now);
        record.setUpdatedAt(now);
        if (record.getAttemptCount() == null) {
            record.setAttemptCount(1);
        }
        if (record.getStatus() == null) {
            record.setStatus("completed");
        }
        
        String sql = "INSERT INTO answer_records (user_id, question_id, user_answer, score, interview_type, " +
                    "difficulty, position, time_spent, attempt_count, status, ai_evaluation, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(sql,
            record.getUserId(), record.getQuestionId(), record.getUserAnswer(),
            record.getScore(), record.getInterviewType(), record.getDifficulty(),
            record.getPosition(), record.getTimeSpent(), record.getAttemptCount(),
            record.getStatus(), record.getAiEvaluation(), record.getCreatedAt(), record.getUpdatedAt()
        );
        
        // 获取生成的ID
        String findSql = "SELECT id, user_id, question_id, user_answer, score, interview_type, difficulty, " +
                        "position, time_spent, attempt_count, status, ai_evaluation, created_at, updated_at " +
                        "FROM answer_records WHERE user_id = ? AND question_id = ? ORDER BY created_at DESC LIMIT 1";
        return jdbcTemplate.queryForObject(findSql, recordRowMapper, record.getUserId(), record.getQuestionId());
    }

    /**
     * 根据题目ID和用户ID查询答题记录
     */
    public List<AnswerRecord> findByUserIdAndQuestionId(Long userId, Long questionId) {
        String sql = "SELECT id, user_id, question_id, user_answer, score, interview_type, difficulty, " +
                    "position, time_spent, attempt_count, status, ai_evaluation, created_at, updated_at " +
                    "FROM answer_records WHERE user_id = ? AND question_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, recordRowMapper, userId, questionId);
    }

    /**
     * 获取用户统计信息
     */
    public Map<String, Object> getUserStats(Long userId) {
        String sql = "SELECT COUNT(*) as total_answered, " +
                    "AVG(score) as avg_score, " +
                    "MAX(score) as max_score, " +
                    "MIN(score) as min_score " +
                    "FROM answer_records WHERE user_id = ? AND score IS NOT NULL";
        
        return jdbcTemplate.queryForMap(sql, userId);
    }
}