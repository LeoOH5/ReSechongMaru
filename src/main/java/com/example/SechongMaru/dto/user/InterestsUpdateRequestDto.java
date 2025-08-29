// src/main/java/com/example/SechongMaru/dto/user/InterestsUpdateRequestDto.java
package com.example.SechongMaru.dto.user;

import java.util.List;

public record InterestsUpdateRequestDto(
        List<Long> interestIds,
        List<String> interestNames
) {}
