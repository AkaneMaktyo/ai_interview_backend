-- H2数据库初始化脚本

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100),
    nickname VARCHAR(50),
    avatar VARCHAR(255),
    level VARCHAR(20) DEFAULT 'beginner',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- 创建知识点标签表
CREATE TABLE IF NOT EXISTS knowledge_tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    category VARCHAR(20) DEFAULT 'technical',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- 创建题目表
CREATE TABLE IF NOT EXISTS questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    question_type VARCHAR(20) NOT NULL,
    difficulty VARCHAR(10) NOT NULL,
    position VARCHAR(20) NOT NULL,
    tags TEXT,
    expected_answer TEXT,
    evaluation_criteria TEXT,
    hints TEXT,
    ai_generated BOOLEAN DEFAULT TRUE,
    ai_prompt TEXT,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- 创建答题记录表
CREATE TABLE IF NOT EXISTS answer_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    user_answer TEXT NOT NULL,
    score INTEGER,
    interview_type VARCHAR(20),
    difficulty VARCHAR(10),
    position VARCHAR(20),
    time_spent INTEGER,
    attempt_count INTEGER DEFAULT 1,
    status VARCHAR(20) DEFAULT 'completed',
    ai_evaluation TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (question_id) REFERENCES questions(id)
);

-- 创建错题集表
CREATE TABLE IF NOT EXISTS wrong_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    answer_record_id BIGINT,
    mistake_count INTEGER DEFAULT 1,
    last_mistake_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mastered_at TIMESTAMP NULL,
    is_mastered BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_question (user_id, question_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (question_id) REFERENCES questions(id),
    FOREIGN KEY (answer_record_id) REFERENCES answer_records(id)
);

-- 创建学习统计表
CREATE TABLE IF NOT EXISTS learning_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    stat_date DATE NOT NULL,
    questions_answered INTEGER DEFAULT 0,
    correct_answers INTEGER DEFAULT 0,
    total_score INTEGER DEFAULT 0,
    time_spent INTEGER DEFAULT 0,
    avg_score DECIMAL(4,2) DEFAULT 0.00,
    accuracy_rate DECIMAL(4,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_date (user_id, stat_date),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 创建面试会话表
CREATE TABLE IF NOT EXISTS interview_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_name VARCHAR(100),
    interview_type VARCHAR(20) NOT NULL,
    difficulty VARCHAR(10) NOT NULL,
    position VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'active',
    total_questions INTEGER DEFAULT 0,
    answered_questions INTEGER DEFAULT 0,
    total_score INTEGER DEFAULT 0,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP NULL,
    session_data TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 插入测试数据
INSERT INTO users (username, email, nickname, level) VALUES 
    ('testuser', 'test@example.com', '测试用户', 'intermediate');

INSERT INTO knowledge_tags (name, description, category) VALUES 
    ('Java基础', 'Java语言基础知识点', 'technical'),
    ('Spring框架', 'Spring相关技术栈', 'technical'),
    ('数据结构', '基础数据结构和算法', 'technical'),
    ('数据库', '数据库设计和优化', 'technical'),
    ('系统设计', '分布式系统架构设计', 'technical');

INSERT INTO questions (title, content, question_type, difficulty, position, tags, expected_answer, ai_generated, created_by) VALUES 
    ('Java中的多态是什么？', '请详细解释Java中的多态概念，并举例说明。', 'technical', 'easy', 'java_developer', '["Java基础","面向对象"]', 
     '多态是指同一个接口可以有多种不同的实现形式。在Java中，主要通过继承和接口实现多态...', TRUE, 1),
    
    ('Spring Bean的生命周期', '请描述Spring Bean从创建到销毁的完整生命周期。', 'technical', 'medium', 'java_developer', '["Spring框架","Bean管理"]',
     'Spring Bean的生命周期包括：实例化、属性填充、初始化、使用、销毁等阶段...', TRUE, 1),
     
    ('如何优化SQL查询性能？', '假设你有一个查询很慢的SQL语句，你会如何进行性能优化？', 'technical', 'medium', 'java_developer', '["数据库","性能优化"]',
     'SQL性能优化可以从索引优化、查询语句优化、表结构设计等方面入手...', TRUE, 1);