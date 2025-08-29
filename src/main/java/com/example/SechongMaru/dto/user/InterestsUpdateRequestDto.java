// src/main/java/com/example/SechongMaru/dto/user/InterestsUpdateRequestDto.java
package com.example.SechongMaru.dto.user;

import java.util.List;
import java.util.UUID;

public record InterestsUpdateRequestDto(
        List<UUID> interestIds,
        List<String> interestNames
) {}
