package com.example.SechongMaru.repository.user;

import com.example.SechongMaru.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 필요하면 커스텀 쿼리 메서드를 여기 추가하면 됩니다.
    // 예) Optional<User> findByName(String name);
}
