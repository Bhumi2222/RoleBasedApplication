package com.example.demo.security;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SseService {

    private final Map<UUID, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public SseService() {

        // Send heartbeat every 20 seconds
        scheduler.scheduleAtFixedRate(
                this::sendHeartbeats,
                20,
                20,
                TimeUnit.SECONDS);
    }

    public SseEmitter connect(UUID userId) {

        System.out.println(
                "Creating SSE emitter for user: " + userId);

        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);

        emitters
                .computeIfAbsent(
                        userId,
                        key -> new CopyOnWriteArrayList<>())
                .add(emitter);

        System.out.println(
                "Total connections for user "
                        + userId
                        + ": "
                        + emitters.get(userId).size());

        emitter.onCompletion(() -> {
            System.out.println(
                    "SSE completed: " + userId);

            remove(userId, emitter);
        });

        emitter.onTimeout(() -> {
            System.out.println(
                    "SSE timeout: " + userId);

            remove(userId, emitter);
        });

        emitter.onError(error -> {
            System.out.println(
                    "SSE error: " + userId);

            remove(userId, emitter);
        });

        // Initial event
        try {

            emitter.send(
                    SseEmitter.event()
                            .name("CONNECTED")
                            .data("SSE connection established"));

            System.out.println(
                    "Initial SSE event sent to user: "
                            + userId);

        } catch (IOException e) {

            System.out.println(
                    "Failed to send initial SSE event: "
                            + e.getMessage());

            remove(userId, emitter);
        }

        return emitter;
    }

    private void sendHeartbeats() {

        for (Map.Entry<UUID, List<SseEmitter>> entry : emitters.entrySet()) {

            UUID userId = entry.getKey();

            for (SseEmitter emitter : entry.getValue()) {

                try {

                    emitter.send(
                            SseEmitter.event()
                                    .comment("heartbeat"));

                } catch (IOException e) {

                    System.out.println(
                            "Heartbeat failed for user: "
                                    + userId);

                    remove(userId, emitter);
                }
            }
        }
    }

    private void remove(
            UUID userId,
            SseEmitter emitter) {

        List<SseEmitter> userEmitters = emitters.get(userId);

        if (userEmitters == null) {
            return;
        }

        userEmitters.remove(emitter);

        if (userEmitters.isEmpty()) {
            emitters.remove(userId);
        }
    }

    public void sendPermissionChanged(UUID userId) {

        List<SseEmitter> userEmitters = emitters.get(userId);

        if (userEmitters == null) {

            System.out.println(
                    "No SSE connection for user: "
                            + userId);

            return;
        }

        System.out.println(
                "Sending permission change event to "
                        + userId
                        + " on "
                        + userEmitters.size()
                        + " connection(s)");

        for (SseEmitter emitter : userEmitters) {

            try {

                emitter.send(
                        SseEmitter.event()
                                .name("PERMISSIONS_CHANGED")
                                .data("permissions_changed"));

                System.out.println(
                        "Permission change event sent successfully");

            } catch (IOException e) {

                System.out.println(
                        "Failed to send permission event: "
                                + e.getMessage());

                remove(userId, emitter);
            }
        }
    }

    public void sendTest(UUID userId) {

        List<SseEmitter> userEmitters = emitters.get(userId);

        if (userEmitters == null) {

            System.out.println(
                    "No SSE connection for user: "
                            + userId);

            return;
        }

        System.out.println(
                "Sending TEST event to "
                        + userEmitters.size()
                        + " connection(s)");

        for (SseEmitter emitter : userEmitters) {

            try {

                emitter.send(
                        SseEmitter.event()
                                .name("TEST")
                                .data("Hello from Spring Boot"));

                System.out.println(
                        "TEST event sent successfully");

            } catch (IOException e) {

                System.out.println(
                        "Failed to send TEST event: "
                                + e.getMessage());

                remove(userId, emitter);
            }
        }
    }
}