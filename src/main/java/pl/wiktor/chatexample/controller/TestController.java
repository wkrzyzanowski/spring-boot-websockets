package pl.wiktor.chatexample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.wiktor.chatexample.model.ChatMessage;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RestController
public class TestController {

    @Autowired
    private SimpMessageSendingOperations messageTemplate;

    @GetMapping("/testmessage")
    public ResponseEntity<String> sendTestMessage() {

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUnixTimestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        chatMessage.setSender("SYSTEM");
        chatMessage.setChannel("TEST");
        chatMessage.setType(ChatMessage.MessageType.NOTIFICATION);
        chatMessage.setContent("System message!");

//        chatController.sendMessage(chatMessage, chatMessage.getChannel());

        messageTemplate.convertAndSend("/topic/public/" + chatMessage.getChannel(), chatMessage);

        return ResponseEntity.ok("Sent successfully!");
    }


}
