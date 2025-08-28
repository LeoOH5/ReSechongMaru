// UserRepository.java (이미 있음, 필요시 fetch join 최적화만 추가)
package com.example.SechongMaru.repository.user;

import com.example.SechongMaru.entity.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // N+1 방지용 (옵션)
    @EntityGraph(attributePaths = {"interests", "interests.interest"})
    Optional<User> findWithInterestsById(Long id);
}
