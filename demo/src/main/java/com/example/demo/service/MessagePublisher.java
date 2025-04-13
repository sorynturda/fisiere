package com.example.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessagePublisher {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void publish(String channel, Object messageObject) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(messageObject);
            redisTemplate.convertAndSend(channel, jsonMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
