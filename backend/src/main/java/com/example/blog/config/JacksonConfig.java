package com.example.blog.config;

import com.example.blog.common.TimeFormats;
import java.time.LocalDateTime;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.module.SimpleModule;

/**
 * 统一时间出参格式为 {@code yyyy-MM-dd'T'HH:mm:ss}（契约 §一「时间格式」）。
 *
 * <p>Spring Boot 4 使用 Jackson 3（{@code tools.jackson.*}）。Jackson 默认的
 * {@code ISO_LOCAL_DATE_TIME} 在秒为 0 时会省略秒位（如 {@code 2026-09-03T09:00}），
 * 与契约要求的固定格式不一致，因此这里给 {@link LocalDateTime} 注册固定格式的序列化器。
 */
@Configuration
public class JacksonConfig {

    @Bean
    public JsonMapperBuilderCustomizer blogDateTimeFormatCustomizer() {
        return builder -> {
            SimpleModule module = new SimpleModule("blog-date-time");
            module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(TimeFormats.DATE_TIME));
            builder.addModule(module);
        };
    }
}
