package com.al3xkras.messenger.message_service.model;

import com.al3xkras.messenger.entity.MessengerUser;
import com.al3xkras.messenger.model.ChatUserRole;
import com.al3xkras.messenger.model.security.JwtTokenAuth;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Date;

import static com.al3xkras.messenger.model.security.JwtTokenAuth.Param.*;


@Slf4j
public class JwtAccessTokens {

    @Autowired
    private RestTemplate restTemplate;

    private String userServiceAccessToken;
    private String userServiceRefreshToken;

    private String chatServiceAccessToken;
    private String chatServiceRefreshToken;

    private final Algorithm algorithm = JwtTokenAuth.getJwtAuthAlgorithm();
    private final JWTVerifier jwtVerifier = JWT.require(algorithm).build();

    public String getUserServiceAccessToken() throws Exception {

        //TODO remove hardcode
        String uri = "http://localhost:10001/user/login";
        String refreshUri = "http://localhost:10001/user/refresh";

        if (userServiceAccessToken!=null && userServiceRefreshToken!=null){
            if (jwtVerifier.verify(userServiceAccessToken).getExpiresAt().after(new Date(System.currentTimeMillis()+30*1000L))){
                try {
                    userServiceAccessToken = refreshToken(userServiceRefreshToken,refreshUri, jwtVerifier);
                    return userServiceAccessToken;
                } catch (Exception e){
                    userServiceRefreshToken=null;
                    userServiceAccessToken=null;
                    return getUserServiceAccessToken();
                }
            }
        }

        String requestUri = UriComponentsBuilder.fromUriString(uri)
                .queryParam(USERNAME.value(), MessengerUser.FIRST_ADMIN.getUsername())
                .queryParam(PASSWORD.value(),MessengerUser.FIRST_ADMIN.getPassword())
                .toUriString();
        RequestEntity<?> requestEntity = RequestEntity.post(requestUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .build();
        ResponseEntity<?> responseEntity = restTemplate.exchange(requestEntity,Object.class);
        String accessToken = responseEntity.getHeaders().getFirst(HEADER_ACCESS_TOKEN.value());
        String refreshToken = responseEntity.getHeaders().getFirst(HEADER_REFRESH_TOKEN.value());
        if (accessToken==null || refreshToken==null)
            throw new IllegalStateException("user service access or refresh token is null");
        userServiceAccessToken = accessToken;
        userServiceRefreshToken = refreshToken;
        return userServiceAccessToken;
    }

    public String getChatServiceAccessToken(String chatName) throws Exception {
        //TODO remove hardcode
        String uri = "http://localhost:10002/auth";
        String refreshUri = "http://localhost:10002/refresh";

        if (chatServiceAccessToken!=null && chatServiceRefreshToken!=null){
            if (jwtVerifier.verify(chatServiceAccessToken).getExpiresAt().after(new Date(System.currentTimeMillis()+30*1000L))){
                try {
                    chatServiceAccessToken = refreshToken(chatServiceRefreshToken,refreshUri, jwtVerifier);
                    return chatServiceAccessToken;
                } catch (Exception e){
                    chatServiceRefreshToken=null;
                    chatServiceAccessToken=null;
                    return getChatServiceAccessToken(chatName);
                }
            }
        }

        String userAccessToken = getUserServiceAccessToken();
        String requestUri;
        if (chatName==null){
            log.info("Chat name argument is null. Result token access is "+ChatUserRole.ANONYMOUS);
            requestUri = UriComponentsBuilder.fromUriString(uri)
                    .queryParam(USER_TOKEN.value(), userAccessToken)
                    .toUriString();
        } else {
            requestUri = UriComponentsBuilder.fromUriString(uri)
                    .queryParam(USER_TOKEN.value(), userAccessToken)
                    .queryParam(CHAT_NAME.value(),chatName)
                    .toUriString();
        }

        RequestEntity<?> requestEntity = RequestEntity.post(requestUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .build();
        ResponseEntity<?> responseEntity = restTemplate.exchange(requestEntity,Object.class);
        String accessToken = responseEntity.getHeaders().getFirst(HEADER_ACCESS_TOKEN.value());
        String refreshToken = responseEntity.getHeaders().getFirst(HEADER_REFRESH_TOKEN.value());
        if (accessToken==null || refreshToken==null)
            throw new IllegalStateException("chat service access or refresh token is null");
        chatServiceAccessToken = accessToken;
        chatServiceRefreshToken = refreshToken;
        return chatServiceAccessToken;
    }

    private String refreshToken(String refreshToken, String refreshUri, JWTVerifier jwtVerifier) {
        jwtVerifier.verify(refreshToken);

        String requestUri = UriComponentsBuilder.fromUriString(refreshUri)
                .queryParam(HEADER_ACCESS_TOKEN.value(), refreshToken)
                .toUriString();
        RequestEntity<?> requestEntity = RequestEntity.post(requestUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .build();
        ResponseEntity<?> responseEntity = restTemplate.exchange(requestEntity,Object.class);
        String accessToken = responseEntity.getHeaders().getFirst(HEADER_ACCESS_TOKEN.value());
        if (accessToken==null)
            throw new IllegalStateException("user service access token is null");
        return accessToken;
    }

}