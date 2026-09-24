package com.example.blog.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间格式工具：全项目统一使用 ISO-8601 本地时区无偏移格式 {@code yyyy-MM-dd'T'HH:mm:ss}（契约 §一）。
 *
 * <p>SQLite 没有原生日期类型，{@code created_at} / {@code updated_at} 都是这种格式的 TEXT；
 * repository 解析、service 写入、JSON 出参序列化（见 {@code config/JacksonConfig}）共用这里的同一个
 * formatter，避免三处各写一份导致格式漂移。
 */
public final class TimeFormats {

    /** 唯一的时间格式定义。 */
    public static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private TimeFormats() {
    }

    /** 解析数据库中的时间文本；null / 空白安全。 */
    public static LocalDateTime parse(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(text, DATE_TIME);
    }

    /** 按统一格式序列化时间；null 安全。 */
    public static String format(LocalDateTime time) {
        return (time == null) ? null : time.format(DATE_TIME);
    }

    /** 当前时间（截到秒的文本），用于写入 created_at / updated_at。 */
    public static String now() {
        return LocalDateTime.now().withNano(0).format(DATE_TIME);
    }
}
