// src/main/java/com/example/SechongMaru/policy/controller/AuthDebugController.java
package com.example.SechongMaru.policy.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class AuthDebugController {

    @GetMapping("/whoami")
    public Map<String, Object> whoami() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("hasAuth", auth != null);
        out.put("authenticated", auth != null && auth.isAuthenticated());
        out.put("principal", auth != null && auth.getPrincipal() != null ? auth.getPrincipal().toString() : null);
        out.put("detailsClass", auth != null && auth.getDetails() != null ? auth.getDetails().getClass().getName() : null);
        out.put("authClass", auth != null ? auth.getClass().getName() : null);
        return out;
    }
}
