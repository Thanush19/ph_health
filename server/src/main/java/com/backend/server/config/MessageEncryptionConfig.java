package com.backend.server.config;

import com.backend.server.util.MessageEncryption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageEncryptionConfig {

    @Bean
    public MessageEncryption messageEncryption(@Value("${chat.encryption.key}") String base64Key) {
        return new MessageEncryption(base64Key);
    }
}
