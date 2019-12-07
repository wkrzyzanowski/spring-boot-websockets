package pl.wiktor.chatexample.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import pl.wiktor.chatexample.model.ChatMessage;

import java.time.Instant;

@Controller
@Slf4j
public class ChatController {

    @MessageMapping("/chat.register/{channel}")
    @SendTo("/topic/public/{channel}")
    public ChatMessage register(@Payload ChatMessage cm,
                                SimpMessageHeaderAccessor headerAccessor,
                                @DestinationVariable String channel) {
        headerAccessor.getSessionAttributes().put("username", cm.getSender());
        headerAccessor.getSessionAttributes().put("channel", channel);
        cm.setChannel(channel);
        cm.setUnixTimestamp(Instant.now().getEpochSecond());
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
