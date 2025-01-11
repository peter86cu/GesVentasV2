package com.ayalait.gesventas;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class NotificacionesWebSocketHandler extends TextWebSocketHandler {

	private final List<WebSocketSession> sessions = new ArrayList<>();
    private final Map<String, WebSocketSession> sessions1 = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
    	sessions.add(session);
    
        System.out.println("Nueva conexión WebSocket establecida: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

   
    
    public void sendMessageToUser(String username, String message) throws IOException {
        WebSocketSession session = sessions1.get(username);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        }
    }
}

