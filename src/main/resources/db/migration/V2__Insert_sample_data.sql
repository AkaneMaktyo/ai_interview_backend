-- V2__Insert_sample_data.sql
-- 插入初始化数据

-- 插入知识点标签
INSERT INTO knowledge_tags (name, category) VALUES
-- 后端开发
('Java基础', '后端开发'),
('Spring Framework', '后端开发'),
('Spring Boot', '后端开发'),
('Spring Cloud', '后端开发'),
('MyBatis', '后端开发'),
('MyBatis Plus', '后端开发'),
('JVM调优', '后端开发'),
('设计模式', '后端开发'),
('微服务架构', '后端开发'),

-- 前端开发
('JavaScript基础', '前端开发'),
('Vue.js', '前端开发'),
('React', '前端开发'),
('HTML/CSS', '前端开发'),
('TypeScript', '前端开发'),
('前端工程化', '前端开发'),
('性能优化', '前端开发'),
('移动端开发', '前端开发'),

-- 数据库
('数据库设计', '数据库'),
('SQL优化', '数据库'),
('MySQL', '数据库'),
('PostgreSQL', '数据库'),
('Redis', '数据库'),
('MongoDB', '数据库'),
('数据库事务', '数据库'),

-- 中间件
('消息队列', '中间件'),
('RabbitMQ', '中间件'),
('Apache Kafka', '中间件'),
('Elasticsearch', '中间件'),
('Nginx', '中间件'),

-- 架构设计
('系统架构', '架构设计'),
('分布式系统', '架构设计'),
('高并发设计', '架构设计'),
('缓存策略', '架构设计'),
('负载均衡', '架构设计'),

-- 算法数据结构
('算法基础', '算法'),
('数据结构', '算法'),
('动态规划', '算法'),
('图算法', '算法'),
('排序算法', '算法'),

-- DevOps
('Docker', 'DevOps'),
('Kubernetes', 'DevOps'),
('CI/CD', 'DevOps'),
('监控告警', 'DevOps'),
('Linux运维', 'DevOps');

-- 插入演示用户
INSERT INTO users (username, email, password_hash, nickname) VALUES
('demo_user', 'demo@aiinterview.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '演示用户'),
('test_user', 'test@aiinterview.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '测试用户');

-- 插入示例题目
INSERT INTO questions (title, content, question_type, difficulty, position, tags, expected_answer, evaluation_criteria, hints, ai_prompt) VALUES
-- 前端题目
(
    'JavaScript闭包机制详解',
    '请详细解释JavaScript的闭包机制，并提供一个实际应用场景。',
    'technical',
    'medium',
    'frontend',
    '["JavaScript基础", "闭包", "作用域"]'::jsonb,
    'JavaScript闭包是指函数能够记住并访问其词法作用域，即使在其词法作用域之外执行也是如此。实际应用包括数据私有化、模块化、事件处理等。',
    '["是否正确解释了闭包概念", "是否提供了代码示例", "是否说明了实际应用场景", "是否理解作用域链的概念"]'::jsonb,
    '["考虑函数内部函数的作用域", "思考变量的生命周期", "可以举例说明模块模式的使用"]'::jsonb,
    '生成一道关于JavaScript闭包机制的中等难度前端面试题'
),

-- Vue.js题目
(
    'Vue.js生命周期钩子详解',
    'Vue.js的生命周期钩子有哪些？请详细说明每个钩子的作用和调用时机。',
    'technical',
    'medium',
    'frontend',
    '["Vue.js", "生命周期", "钩子函数"]'::jsonb,
    'Vue.js生命周期钩子包括beforeCreate、created、beforeMount、mounted、beforeUpdate、updated、beforeDestroy、destroyed等，每个钩子在组件不同阶段被调用，用于执行特定的初始化、更新、清理逻辑。',
    '["是否列举了主要的生命周期钩子", "是否说明了各钩子的调用时机", "是否提及了实际应用场景"]'::jsonb,
    '["考虑组件的创建、挂载、更新、销毁过程", "思考每个阶段适合做什么操作"]'::jsonb,
    '生成一道关于Vue.js生命周期钩子的中等难度前端面试题'
),

-- 后端题目
(
    'Spring Boot自动配置原理',
    '请详细解释Spring Boot的自动配置机制是如何工作的？',
    'technical',
    'medium',
    'backend',
    '["Spring Boot", "自动配置", "注解"]'::jsonb,
    'Spring Boot通过@EnableAutoConfiguration注解启用自动配置，基于条件注解@Conditional系列实现按需配置，通过starter依赖和spring.factories文件实现配置类的自动加载。',
    '["是否提到@EnableAutoConfiguration", "是否解释条件装配", "是否说明配置类加载机制", "是否提及starter的作用"]'::jsonb,
    '["考虑@Conditional注解的作用", "思考starter和spring.factories的关系", "可以举例说明具体的自动配置类"]'::jsonb,
    '生成一道关于Spring Boot自动配置原理的中等难度后端面试题'
),

-- 数据库题目
(
    '数据库索引优化策略',
    '如何设计和优化数据库索引？请从多个角度详细说明。',
    'technical',
    'hard',
    'backend',
    '["数据库", "索引", "性能优化", "MySQL"]'::jsonb,
    '数据库索引优化包括：选择合适的索引类型（B+树、哈希等）、建立复合索引、避免过多索引、考虑索引覆盖、定期维护索引、分析执行计划等策略。',
    '["是否说明了不同索引类型", "是否提及复合索引的使用", "是否考虑了索引的负面影响", "是否提到执行计划分析"]'::jsonb,
    '["考虑查询频率和更新频率的平衡", "思考索引的存储空间开销", "可以举例说明具体的优化案例"]'::jsonb,
    '生成一道关于数据库索引优化的困难级别后端面试题'
),

-- 系统设计题目
(
    '分布式缓存系统设计',
    '请设计一个分布式缓存系统，需要考虑数据分片、一致性哈希和故障恢复。',
    'system_design',
    'hard',
    'backend',
    '["分布式系统", "缓存", "一致性哈希", "故障恢复"]'::jsonb,
    '分布式缓存系统设计需要考虑：一致性哈希实现数据分片、副本策略保证可用性、故障检测和自动恢复机制、数据同步和一致性保证、负载均衡和性能监控等。',
    '["是否设计了合理的数据分片策略", "是否考虑了故障恢复机制", "是否提及一致性保证", "是否考虑了性能和扩展性"]'::jsonb,
    '["思考CAP理论的权衡", "考虑Redis Cluster的实现方式", "可以参考Memcached的分布式方案"]'::jsonb,
    '生成一道关于分布式缓存系统设计的困难级别系统设计面试题'
),

-- 行为面试题目
(
    '技术挑战解决经历',
    '请描述一个你在工作中遇到的最大技术挑战，以及你是如何解决的。',
    'behavioral',
    'medium',
    'fullstack',
    '["技术挑战", "问题解决", "项目经验"]'::jsonb,
    '好的回答应该包括：具体的技术挑战描述、分析问题的思路、解决方案的制定过程、实施过程中的困难、最终的解决效果和经验总结。',
    '["是否具体描述了遇到的挑战", "是否说明了解决思路", "是否体现了技术能力", "是否有经验总结"]'::jsonb,
    '["使用STAR方法回答", "重点突出你的技术贡献", "可以提及团队协作的部分"]'::jsonb,
    '生成一道关于技术挑战解决经历的中等难度行为面试题'
);

-- 插入学习统计样例数据（近7天）
INSERT INTO learning_stats (user_id, stat_date, questions_attempted, questions_correct, total_score, time_spent, tags_practiced) VALUES
(1, CURRENT_DATE - INTERVAL '6 day', 5, 3, 35, 1800, '["JavaScript基础", "Vue.js"]'::jsonb),
(1, CURRENT_DATE - INTERVAL '5 day', 3, 2, 22, 1200, '["Spring Boot", "数据库"]'::jsonb),
(1, CURRENT_DATE - INTERVAL '4 day', 4, 3, 28, 1500, '["算法基础"]'::jsonb),
(1, CURRENT_DATE - INTERVAL '3 day', 6, 4, 42, 2100, '["Vue.js", "前端工程化"]'::jsonb),
(1, CURRENT_DATE - INTERVAL '2 day', 2, 1, 15, 900, '["系统设计"]'::jsonb),
(1, CURRENT_DATE - INTERVAL '1 day', 4, 4, 36, 1800, '["JavaScript基础", "Vue.js"]'::jsonb),
(1, CURRENT_DATE, 3, 2, 24, 1200, '["Spring Boot"]'::jsonb);