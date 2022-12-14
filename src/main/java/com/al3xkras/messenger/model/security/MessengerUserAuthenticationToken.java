package com.al3xkras.messenger.model.security;

import com.al3xkras.messenger.model.MessengerUserType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class MessengerUserAuthenticationToken extends AbstractAuthenticationToken {
    private final String username;
    private final Long messengerUserId;
    private MessengerUserType messengerUserType;

    public MessengerUserAuthenticationToken(String username, Long messengerUserId, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.username = username;
        this.messengerUserId = messengerUserId;
    }

    public String getUsername() {
        return username;
    }

    public Long getMessengerUserId() {
        return messengerUserId;
    }

    public MessengerUserType getMessengerUserType() {
        return messengerUserType;
    }

    public void setMessengerUserType(MessengerUserType messengerUserType) {
        this.messengerUserType = messengerUserType;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }
}
