package pl.wiktor.chatexample.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class ChatMessage {
    private String content;
    private String sender;
    private String channel;
    private long unixTimestamp;
    private MessageType type;

    public ChatMessage() {
    }

    public ChatMessage(String content, String sender, String channel, long unixTimestamp, MessageType type) {
        this.content = content;
        this.sender = sender;
        this.channel = channel;
        this.unixTimestamp = unixTimestamp;
        this.type = type;
    }

    public enum MessageType {
        CHAT,
        LEAVE,
        JOIN,
        NOTIFICATION
    }
}
