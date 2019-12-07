package pl.wiktor.chatexample.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import pl.wiktor.chatexample.model.ChatMessage;

import java.time.Instant;

@Component
@Slf4j
public class WsEventListener {


    @Autowired
    private SimpMessageSendingOperations messageTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("Received a new web socket connection. Details: " + event.toString());
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String channel = (String) headerAccessor.getSessionAttributes().get("channel");

        if (username != null) {
            log.info("Dissconection - User: " + username + " Channel: " + channel);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(username);
            chatMessage.setUnixTimestamp(Instant.now().getEpochSecond());

            messageTemplate.convertAndSend("/topic/public/" + channel, chatMessage);
        }
    }


}
