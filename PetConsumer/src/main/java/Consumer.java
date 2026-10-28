
package com.comp713;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;

public class Consumer {
    public static void main(String[] args) {
        System.out.println("Connecting to Artemis at tcp://localhost:61616...");
        try {
            ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61616");
            
            
            try (JMSContext context = cf.createContext("admin", "admin")) {
                Queue queue = context.createQueue("PetEventsQueue");
                JMSConsumer consumer = context.createConsumer(queue);
                
                System.out.println("🎧 [Consumer] Started! Waiting for events from the Web App...");
                
                while (true) {
                    String msg = consumer.receiveBody(String.class);
                    System.out.println("🔔 [Consumer] Received Event: " + msg);
                }
            }
        } catch (Exception e) {
            System.out.println("================= ERROR =================");
            System.out.println("❌ Failed to connect or receive message!");
            System.out.println("Error Message: " + e.getMessage());
            System.out.println("Root Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "Unknown"));
            System.out.println("=========================================");
        }
    }
}