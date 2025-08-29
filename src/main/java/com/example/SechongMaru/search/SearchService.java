package com.example.SechongMaru.search;

import com.example.SechongMaru.entity.policy.Policy;
import com.example.SechongMaru.repository.policy.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final PolicyRepository policyRepository;

    /**
     * 제목에 q가 포함된 정책 목록을 반환한다. (대소문자 무시)
     * q가 null/blank인 경우 빈 리스트 반환.
     */
    public List<Policy> searchByTitle(String q) {
        if (q == null) return Collections.emptyList();
        String kw = q.trim();
        if (kw.isEmpty()) return Collections.emptyList();
        return policyRepository.findByTitleContainingIgnoreCase(kw);
    }
}
