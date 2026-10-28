package com.comp713;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/pet-updates")
public class PetUpdateSocket {
private static final Set<Session> sessions = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("WebSocket Connected: " + session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("WebSocket Disconnected: " + session.getId());
    }
    public static void broadcast(String message) {
        for (Session session : sessions) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
