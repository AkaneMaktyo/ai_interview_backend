-- V1__Create_initial_tables.sql
-- AI面试系统初始数据库表结构

-- 创建用户表
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    avatar_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT true
);

-- 创建知识点标签表
CREATE TABLE knowledge_tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    category VARCHAR(50) NOT NULL, -- 如：后端、前端、算法、数据库
    description TEXT,
    parent_id BIGINT REFERENCES knowledge_tags(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建题目表
CREATE TABLE questions (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    question_type VARCHAR(20) NOT NULL, -- technical, behavioral, system_design, coding
    difficulty VARCHAR(10) NOT NULL,    -- easy, medium, hard
    position VARCHAR(20) NOT NULL,      -- frontend, backend, fullstack, mobile, devops
    tags JSONB,                         -- ["Spring Boot", "微服务"]
    expected_answer TEXT,
    evaluation_criteria JSONB,          -- 评分标准数组
    hints JSONB,                        -- 提示数组
    ai_generated BOOLEAN DEFAULT true,   -- 是否AI生成
    ai_prompt TEXT,                     -- 生成时使用的prompt
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT true
);

-- 创建答题记录表
CREATE TABLE answer_records (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    question_id BIGINT NOT NULL REFERENCES questions(id),
    user_answer TEXT NOT NULL,
    ai_evaluation JSONB,                -- AI评价结果 {"score": 8, "comment": "...", "suggestions": [...]}
    score INTEGER,                      -- 得分 1-10
    time_spent INTEGER,                 -- 答题用时（秒）
    attempt_count INTEGER DEFAULT 1,    -- 第几次作答
    interview_type VARCHAR(20),         -- technical, behavioral等
    difficulty VARCHAR(10),             -- easy, medium, hard
    position VARCHAR(20),               -- frontend, backend等
    status VARCHAR(20) DEFAULT 'completed', -- completed, skipped, in_progress
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(user_id, question_id, attempt_count) -- 防止重复记录
);

-- 创建错题集表
CREATE TABLE wrong_questions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    question_id BIGINT NOT NULL REFERENCES questions(id),
    first_attempt_record_id BIGINT REFERENCES answer_records(id),
    retry_count INTEGER DEFAULT 0,
    last_retry_at TIMESTAMP,
    mastered BOOLEAN DEFAULT false,     -- 是否已掌握
    mastered_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(user_id, question_id)
);

-- 创建学习统计表
CREATE TABLE learning_stats (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    stat_date DATE NOT NULL,
    questions_attempted INTEGER DEFAULT 0,
    questions_correct INTEGER DEFAULT 0,
    total_score INTEGER DEFAULT 0,
    time_spent INTEGER DEFAULT 0,      -- 总学习时间（秒）
    tags_practiced JSONB,              -- 练习的知识点
    
    UNIQUE(user_id, stat_date)
);

-- 创建面试会话表（记录完整面试过程）
CREATE TABLE interview_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    session_name VARCHAR(100),
    interview_type VARCHAR(20) NOT NULL,  -- technical, behavioral等
    difficulty VARCHAR(10) NOT NULL,      -- easy, medium, hard
    position VARCHAR(20) NOT NULL,        -- frontend, backend等
    experience VARCHAR(20) NOT NULL,      -- junior, intermediate, senior
    total_questions INTEGER DEFAULT 0,
    completed_questions INTEGER DEFAULT 0,
    overall_score DECIMAL(4,2),           -- 总体评分
    duration_minutes INTEGER,             -- 面试时长（分钟）
    status VARCHAR(20) DEFAULT 'in_progress', -- in_progress, completed, abandoned
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    summary JSONB                         -- 面试总结数据
);

-- 创建索引优化查询性能
-- 用户表索引
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_active ON users(is_active);

-- 知识点标签表索引
CREATE INDEX idx_tags_category ON knowledge_tags(category);
CREATE INDEX idx_tags_parent ON knowledge_tags(parent_id);

-- 题目表索引
CREATE INDEX idx_questions_difficulty ON questions(difficulty);
CREATE INDEX idx_questions_type ON questions(question_type);
CREATE INDEX idx_questions_position ON questions(position);
CREATE INDEX idx_questions_tags ON questions USING GIN(tags);
CREATE INDEX idx_questions_created_at ON questions(created_at);
CREATE INDEX idx_questions_active ON questions(is_active);

-- 答题记录表索引
CREATE INDEX idx_answer_records_user ON answer_records(user_id);
CREATE INDEX idx_answer_records_question ON answer_records(question_id);
CREATE INDEX idx_answer_records_score ON answer_records(score);
CREATE INDEX idx_answer_records_created_at ON answer_records(created_at);
CREATE INDEX idx_answer_records_status ON answer_records(status);
CREATE INDEX idx_answer_records_type_difficulty ON answer_records(interview_type, difficulty);

-- 错题集表索引
CREATE INDEX idx_wrong_questions_user ON wrong_questions(user_id);
CREATE INDEX idx_wrong_questions_mastered ON wrong_questions(user_id, mastered);

-- 学习统计表索引
CREATE INDEX idx_learning_stats_user_date ON learning_stats(user_id, stat_date);

-- 面试会话表索引
CREATE INDEX idx_interview_sessions_user ON interview_sessions(user_id);
CREATE INDEX idx_interview_sessions_status ON interview_sessions(status);
CREATE INDEX idx_interview_sessions_started_at ON interview_sessions(started_at);