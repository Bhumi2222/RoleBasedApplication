package com.example.demo.controller;

import com.example.demo.Entity.User;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.security.SseService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(Authentication authentication) {
         CustomUserDetails userDetails =
            (CustomUserDetails) authentication.getPrincipal();
                UUID userId = userDetails.getUserId();

        System.out.println("SSE connection request received for user: " + userId);

        return sseService.connect(userId);
    }

    // @GetMapping("/test")
    // public String test() {

    // System.out.println("Sending TEST event...");

    // sseService.sendTest(TEST_USER_ID);

    // return "Event sent";
    // }
}