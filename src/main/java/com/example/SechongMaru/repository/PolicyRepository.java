// 임시 데이터용
package com.example.SechongMaru.repository;

import com.example.SechongMaru.entity.policy.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PolicyRepository extends JpaRepository<Policy, UUID> {}
