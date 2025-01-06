package it.nenno.basicaiclient.v1.models;


import java.io.Serializable;

public class AiMessage implements Serializable {
    private final String role; // e.g., "system", "user", "assistant"
    private final String content;

    public AiMessage(String role, String content) {
        if (role == null || role.isEmpty() || content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Both role and content are required for a message.");
        }
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "AiMessage{" +
                "role='" + role + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}

