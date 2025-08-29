package com.example.SechongMaru.crawling;

import com.example.SechongMaru.dto.crawling.CrawlingPolicyRequestDto;
import com.example.SechongMaru.entity.policy.Policy;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crawling")
public class CrawlingPolicyController {

    private final CrawlingPolicyService service;

    @PostMapping("/policies")
    public ResponseEntity<?> ingestOne(@RequestBody CrawlingPolicyRequestDto req) {
        Policy saved = service.upsertOne(req);
        return ResponseEntity.ok(Map.of("id", saved.getId()));
    }

    @PostMapping("/policies/bulk")
    public ResponseEntity<?> ingestBulk(@RequestBody List<CrawlingPolicyRequestDto> list) {
        int count = service.upsertBulk(list);
        return ResponseEntity.ok(Map.of("count", count));
    }
}
