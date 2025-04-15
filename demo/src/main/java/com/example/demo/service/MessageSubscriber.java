package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@Service
public class MessageSubscriber implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        System.out.println("Received message: " + message.toString());
    }
}