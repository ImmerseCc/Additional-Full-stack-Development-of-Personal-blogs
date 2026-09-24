package com.example.blog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 文档元信息（显示在 Swagger UI 顶部）。
 *
 * <p>接口分组与描述由各 controller 上的 {@code @Tag} / {@code @Operation} 提供；
 * 字段级契约的唯一来源仍是 {@code docs/api-contract.md}（v1.0 已确认），这里只做展示层补充。
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI blogOpenApi() {
        return new OpenAPI().info(new Info()
                .title("个人博客后端 API")
                .version("v1.0")
                .description("""
                        VibeCoding 个人博客全栈项目（Vue 3 + Spring Boot 4 + SQLite）的后端接口。
                        契约以仓库内 docs/api-contract.md（v1.0 已确认）为准。

                        - 统一返回：{"code":0,"message":"ok","data":...}
                        - 错误码：0 成功 / 40001 参数校验失败 / 40002 参数格式错误 / 40004 资源不存在 / 40009 资源冲突 / 50000 服务端异常 / 50001 数据库失败
                        - 分页：page 从 1 开始；size 默认 10、范围 1–20
                        - 本项目为演示项目，接口不做鉴权；评论删除与点赞使用前端生成的 visitorId 作为轻量归属标识
                        """));
    }
}
