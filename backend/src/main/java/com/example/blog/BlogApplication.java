package com.example.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用入口（阶段 1 批 4：最小可启动骨架）。
 *
 * <p>启动后：
 * <ul>
 *   <li>内嵌 Tomcat 监听 8080 端口（见 src/main/resources/application.yml）</li>
 *   <li>自动执行 schema.sql 建表、data.sql 写入幂等种子数据</li>
 *   <li>在 backend/data/ 下生成 SQLite 数据库文件 blog.db</li>
 * </ul>
 *
 * <p>阶段 2 起在此工程内补上 controller / service / repository 三层实现与统一响应。
 */
@SpringBootApplication
public class BlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }
}
