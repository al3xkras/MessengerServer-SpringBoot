package com.al3xkras.messenger_chat_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
public class ChatUserId implements Serializable {
    private Long chatId;
    private Long userId;
}
