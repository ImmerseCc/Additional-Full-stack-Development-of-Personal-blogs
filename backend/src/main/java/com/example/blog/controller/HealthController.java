package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.common.TimeFormats;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查（契约 §四 · 1）：用于确认"后端已启动"，不引入 actuator。
 */
@Tag(name = "健康检查", description = "确认后端是否已启动（契约 §四 · 1）")
@RestController
@RequestMapping("/api")
public class HealthController {

    @Operation(summary = "健康检查", description = "返回 status 与服务器当前时间，用于确认后端已启动。")
    @GetMapping("/health")
    public ApiResponse<Map<String, String>> health() {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("status", "UP");
        data.put("time", TimeFormats.now());
        return ApiResponse.ok(data);
    }
}
