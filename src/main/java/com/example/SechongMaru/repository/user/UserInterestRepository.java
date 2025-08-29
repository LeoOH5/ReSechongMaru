// src/main/java/com/example/SechongMaru/repository/user/UserInterestRepository.java
package com.example.SechongMaru.repository.user;

import com.example.SechongMaru.entity.user.User;
import com.example.SechongMaru.entity.user.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
    void deleteByUser(User user);
}