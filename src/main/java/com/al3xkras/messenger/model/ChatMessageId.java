package com.al3xkras.messenger.model;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class ChatMessageId implements Serializable {
    @NotNull
    private Long chatId;
    @NotNull
    private Long userId;
    @NotNull
    private LocalDateTime submissionDate;

    public ChatMessageId(){

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessageId that = (ChatMessageId) o;
        return chatId.equals(that.chatId) && userId.equals(that.userId) && submissionDate.equals(that.submissionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, userId, submissionDate);
    }

    //<editor-fold defaultstate="collapsed" desc="delombok">
    @SuppressWarnings("all")
    public ChatMessageId(final Long chatId, final Long userId, final LocalDateTime submissionDate) {
        this.chatId = chatId;
        this.userId = userId;
        this.submissionDate = submissionDate;
    }

    @SuppressWarnings("all")
    public Long getChatId() {
        return this.chatId;
    }

    @SuppressWarnings("all")
    public Long getUserId() {
        return this.userId;
    }

    @SuppressWarnings("all")
    public LocalDateTime getSubmissionDate() {
        return this.submissionDate;
    }

    @SuppressWarnings("all")
    public void setChatId(final Long chatId) {
        this.chatId = chatId;
    }

    @SuppressWarnings("all")
    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    @SuppressWarnings("all")
    public void setSubmissionDate(final LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }
    //</editor-fold>
}
