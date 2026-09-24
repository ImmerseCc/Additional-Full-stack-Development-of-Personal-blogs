package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.common.TimeFormats;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查（契约 §四 · 1）：用于确认"后端已启动"，不引入 actuator。
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ApiResponse<Map<String, String>> health() {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("status", "UP");
        data.put("time", TimeFormats.now());
        return ApiResponse.ok(data);
    }
}
