package pl.wiktor.chatexample.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import pl.wiktor.chatexample.model.ChatMessage;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
public class ChatController {

    @Autowired
    private SimpMessageSendingOperations messageTemplate;

    private List<ChatMessage> pendingMessages = new ArrayList<>();

    @PostConstruct
    public void fillPendingMessages() {
        pendingMessages.add(
                ChatMessage.builder()
                        .content("Notification one!")
                        .sender("System")
                        .type(ChatMessage.MessageType.NOTIFICATION)
                        .channel("TEST")
                        .unixTimestamp(Instant.now().getEpochSecond())
                        .build()
        );
    }


    @MessageMapping("/chat.register/{channel}")
    @SendTo("/topic/public/{channel}")
    public ChatMessage register(@Payload ChatMessage cm,
                                SimpMessageHeaderAccessor headerAccessor,
                                @DestinationVariable String channel) {
        headerAccessor.getSessionAttributes().put("username", cm.getSender());
        headerAccessor.getSessionAttributes().put("channel", channel);
        cm.setChannel(channel);
        cm.setUnixTimestamp(Instant.now().getEpochSecond());
        messageTemplate.convertAndSend("/topic/public/" + pendingMessages.get(0).getChannel(), pendingMessages.get(0));
        return cm;
    }

    @MessageMapping("/chat.send/{channel}")
    @SendTo("/topic/public/{channel}")
    public ChatMessage sendMessage(@Payload ChatMessage cm,
                                   @DestinationVariable String channel) {
        log.info("Channel: " + channel + " Message: " + cm.toString());
        cm.setChannel(channel);
        cm.setUnixTimestamp(Instant.now().getEpochSecond());
        return cm;
    }

}
