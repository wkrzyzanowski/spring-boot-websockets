package pl.wiktor.chatexample.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatMessage {
    private String content;
    private String sender;
    private String channel;
    private long unixTimestamp;
    private MessageType type;

    public enum MessageType {
        CHAT,
        LEAVE,
        JOIN
    }
}
