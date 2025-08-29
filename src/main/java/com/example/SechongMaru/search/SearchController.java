package com.example.SechongMaru.search;

import com.example.SechongMaru.entity.policy.Policy;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 예: GET /api/search?q=월세
     */
    @GetMapping
    public List<Policy> search(@RequestParam("q") String q) {
        return searchService.searchByTitle(q);
    }
}
