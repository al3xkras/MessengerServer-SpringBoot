package com.al3xkras.messenger.user_service.service;

import com.al3xkras.messenger.entity.MessengerUser;
import com.al3xkras.messenger.model.MessengerUserType;
import com.al3xkras.messenger.model.MessengerUtils;
import com.al3xkras.messenger.user_service.exception.MessengerUserAlreadyExistsException;
import com.al3xkras.messenger.user_service.exception.MessengerUserNotFoundException;
import com.al3xkras.messenger.user_service.repository.MessengerUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;

import static com.al3xkras.messenger.model.MessengerUtils.Messages.EXCEPTION_ARGUMENT_ISNULL;
import static com.al3xkras.messenger.model.security.JwtTokenAuth.Param.USERNAME;
import static com.al3xkras.messenger.model.security.JwtTokenAuth.Param.USER_ID;

@Slf4j
@Service
public class MessengerUserService {

    @Autowired
    private MessengerUserRepository messengerUserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public MessengerUser findMessengerUserById(Long userId) throws MessengerUserNotFoundException{
        return messengerUserRepository.findById(userId)
                .orElseThrow(MessengerUserNotFoundException::new);
    }

    public MessengerUser findMessengerUserByUsername(String username) throws MessengerUserNotFoundException{
        return messengerUserRepository.findByUsername(username)
                .orElseThrow(MessengerUserNotFoundException::new);
    }

    public MessengerUser saveUser(MessengerUser messengerUser) throws MessengerUserAlreadyExistsException{
        try {
            messengerUser.setMessengerUserId(null);
            if (messengerUser.getPassword()!=null) {
                log.info("password encoded");
                messengerUser.setPassword(passwordEncoder.encode(messengerUser.getPassword()));
                log.info(passwordEncoder.encode(messengerUser.getPassword()));
            }
            return messengerUserRepository.saveAndFlush(messengerUser);
        } catch (DataIntegrityViolationException e){
            throw new MessengerUserAlreadyExistsException(messengerUser.getUsername());
        }
    }

    @Transactional
    public MessengerUser updateUserById(MessengerUser messengerUser) throws MessengerUserNotFoundException, MessengerUserAlreadyExistsException{
        if (messengerUser.getMessengerUserId()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,String.format(EXCEPTION_ARGUMENT_ISNULL.value(), USER_ID.value()));
        MessengerUser beforeUpdate = messengerUserRepository.findById(messengerUser.getMessengerUserId())
                .orElseThrow(MessengerUserNotFoundException::new);
        MessengerUser updated = MessengerUser.builder()
                .messengerUserId(messengerUser.getMessengerUserId())
                .username(messengerUser.getUsername()==null?beforeUpdate.getUsername():messengerUser.getUsername())
                .password(messengerUser.getPassword()==null?beforeUpdate.getPassword():passwordEncoder.encode(messengerUser.getPassword()))
                .name(messengerUser.getName()==null?beforeUpdate.getName():messengerUser.getName())
                .surname(messengerUser.getSurname()==null?beforeUpdate.getSurname():messengerUser.getSurname())
                .emailAddress(messengerUser.getEmailAddress()==null?beforeUpdate.getEmailAddress():messengerUser.getEmailAddress())
                .phoneNumber(messengerUser.getPhoneNumber()==null?beforeUpdate.getPhoneNumber():messengerUser.getPhoneNumber())
                .messengerUserType(messengerUser.getMessengerUserType()==null?beforeUpdate.getMessengerUserType():messengerUser.getMessengerUserType())
                .build();
        try {
            return messengerUserRepository.saveAndFlush(updated);
        } catch (DataIntegrityViolationException e){
            throw new MessengerUserAlreadyExistsException(messengerUser.getUsername());
        }
    }

    @Transactional
    public MessengerUser updateUserByUsername(MessengerUser messengerUser) throws MessengerUserNotFoundException {
        if (messengerUser.getUsername()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,String.format(EXCEPTION_ARGUMENT_ISNULL.value(), USERNAME.value()));
        MessengerUser beforeUpdate = messengerUserRepository.findByUsername(messengerUser.getUsername())
                .orElseThrow(MessengerUserNotFoundException::new);
        MessengerUser updated = MessengerUser.builder()
                .messengerUserId(beforeUpdate.getMessengerUserId())
                .username(messengerUser.getUsername())
                .password(messengerUser.getPassword()==null?beforeUpdate.getPassword():passwordEncoder.encode(messengerUser.getPassword()))
                .name(messengerUser.getName()==null?beforeUpdate.getName():messengerUser.getName())
                .surname(messengerUser.getSurname()==null?beforeUpdate.getSurname():messengerUser.getSurname())
                .emailAddress(messengerUser.getEmailAddress()==null?beforeUpdate.getEmailAddress():messengerUser.getEmailAddress())
                .phoneNumber(messengerUser.getPhoneNumber()==null?beforeUpdate.getPhoneNumber():messengerUser.getPhoneNumber())
                .messengerUserType(messengerUser.getMessengerUserType()==null?beforeUpdate.getMessengerUserType():messengerUser.getMessengerUserType())
                .build();
        return messengerUserRepository.saveAndFlush(updated);
    }

    public Page<MessengerUser> findAllUsersByChatId(Long chatId, Pageable pageable) {
        return messengerUserRepository.findAllByChatId(chatId, pageable);
    }

    public void deleteById(Long messengerUserId) throws MessengerUserNotFoundException{
        try {
            messengerUserRepository.deleteById(messengerUserId);
        } catch (EmptyResultDataAccessException e){
            throw new MessengerUserNotFoundException();
        }
    }

    @Transactional
    public void deleteByUsername(String username) throws MessengerUserNotFoundException {
        MessengerUser existing = messengerUserRepository.findByUsername(username)
                        .orElseThrow(MessengerUserNotFoundException::new);
        messengerUserRepository.deleteById(existing.getMessengerUserId());
    }

    public MessengerUserType getUserTypeById(Long messengerUserId) throws MessengerUserNotFoundException {
        return messengerUserRepository.getUserTypeById(messengerUserId)
                .orElseThrow(MessengerUserNotFoundException::new);
    }

    public MessengerUserType getUserTypeByUsername(String username) throws MessengerUserNotFoundException {
        return messengerUserRepository.getUserTypeByUsername(username)
                .orElseThrow(MessengerUserNotFoundException::new);
    }

    @PostConstruct
    private void postConstruct(){
        try {
            MessengerUser admin = MessengerUser.FIRST_ADMIN;
            MessengerUser adminCopy = MessengerUser.builder()
                    .messengerUserId(admin.getMessengerUserId())
                    .name(admin.getName())
                    .username(admin.getUsername())
                    .password(admin.getPassword())
                    .messengerUserType(admin.getMessengerUserType())
                    .emailAddress(admin.getEmailAddress())
                    .phoneNumber(admin.getPhoneNumber())
                    .build();
            saveUser(adminCopy);
            log.info("saved: "+adminCopy);
        } catch (MessengerUserAlreadyExistsException ignored){}
    }
}
