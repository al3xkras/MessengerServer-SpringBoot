package com.al3xkras.messenger.message_service.repository;

import com.al3xkras.messenger.entity.ChatMessage;
import com.al3xkras.messenger.model.ChatMessageId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, ChatMessageId> {

    Page<ChatMessage> findAllByChatIdOrderBySubmissionDate(Long chatId, Pageable pageable);

    @Query("select chatMessage from ChatMessage chatMessage where chatMessage.chatId=" +
            "(select chat.chatId from Chat chat where chat.chatName=?1) order by chatMessage.submissionDate")
    Page<ChatMessage> findAllByChatNameOrderBySubmissionDate(String chatName, Pageable pageable);

}
