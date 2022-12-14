package com.al3xkras.messenger.user_service.controller;

import com.al3xkras.messenger.dto.MessengerUserDTO;
import com.al3xkras.messenger.entity.MessengerUser;
import com.al3xkras.messenger.model.MessengerUserType;
import com.al3xkras.messenger.model.MessengerUtils;
import com.al3xkras.messenger.model.security.JwtTokenAuth;
import com.al3xkras.messenger.user_service.exception.MessengerUserNotFoundException;
import com.al3xkras.messenger.user_service.service.MessengerUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(MessengerUserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class MessengerUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    static ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private MessengerUserService messengerUserService;

    static MessengerUser firstUser = MessengerUser.builder()
            .messengerUserId(1L)
            .username("user1")
            .password("Password123.")
            .name("Max")
            .emailAddress("max@gmail.com")
            .phoneNumber("+34 111-22-33")
            .messengerUserType(MessengerUserType.ADMIN)
            .build();

    static MessengerUserDTO updatedFirstUserDTO = MessengerUserDTO.builder()
            .username("user1")
            .password("Password123.")
            .name(firstUser.getName())
            .email("maxim@gmail.com")
            .phoneNumber("+22 111-22-33")
            .password("Password123.")
            .messengerUserType(MessengerUserType.ADMIN)
            .build();
    static MessengerUser firstUserUpdate = MessengerUser.builder()
            .messengerUserId(1L)
            .username(updatedFirstUserDTO.getUsername())
            .password("Password123.")
            .name(updatedFirstUserDTO.getName())
            .surname(updatedFirstUserDTO.getSurname())
            .emailAddress(updatedFirstUserDTO.getEmail())
            .phoneNumber(updatedFirstUserDTO.getPhoneNumber())
            .messengerUserType(updatedFirstUserDTO.getMessengerUserType())
            .build();
    static MessengerUser firstUserAfterUpdate = MessengerUser.builder()
            .messengerUserId(1L)
            .username(updatedFirstUserDTO.getUsername())
            .password("Password123.")
            .name(firstUser.getName())
            .surname(updatedFirstUserDTO.getSurname())
            .emailAddress(updatedFirstUserDTO.getEmail())
            .phoneNumber(updatedFirstUserDTO.getPhoneNumber())
            .messengerUserType(updatedFirstUserDTO.getMessengerUserType())
            .build();

    @BeforeEach
    void setUp() {

    }

    @Test
    void whenFindExistingById_thenReturn() throws Exception {
        Mockito.when(messengerUserService.findMessengerUserById(1L))
                .thenReturn(firstUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(firstUser)));
    }

    @Test
    void whenFindUserThatDoesNotExist_thenExpectHttpStatusNotFound() throws Exception {
        Mockito.when(messengerUserService.findMessengerUserById(10L))
                        .thenThrow(MessengerUserNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/user/10"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(MessengerUtils.Messages.EXCEPTION_MESSENGER_USER_NOT_FOUND.value()));
    }

    @Test
    void whenFindExistingByUsername_thenReturn() throws Exception {
        Mockito.when(messengerUserService.findMessengerUserByUsername("user1"))
                .thenReturn(firstUser);
        mockMvc.perform(MockMvcRequestBuilders.get("/user").param("username","user1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(firstUser)));
    }

    @Test
    void whenFindUserWithoutUsernameParam_thenReturnHttpStatusBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void whenFindUserByUsernameThatDoesNotExist_thenReturnHttpStatusNotFound() throws Exception {
        Mockito.when(messengerUserService.findMessengerUserByUsername("user3"))
                .thenThrow(MessengerUserNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/user").param("username","user3"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(MessengerUtils.Messages.EXCEPTION_MESSENGER_USER_NOT_FOUND.value()));
    }

    @Test
    void whenSaveNewUser_thenReturnNewUserWithId() throws Exception {

        MessengerUserDTO pojo = MessengerUserDTO.builder()
                .username("user3")
                .password("Password.123")
                .name("Michael")
                .surname("Jackson")
                .phoneNumber("+9 123-45-67")
                .messengerUserType(MessengerUserType.USER)
                .build();

        MessengerUser userBeforeSave = MessengerUser.builder()
                .username(pojo.getUsername())
                .password(pojo.getPassword())
                .name(pojo.getName())
                .surname(pojo.getSurname())
                .emailAddress(pojo.getEmail())
                .phoneNumber(pojo.getPhoneNumber())
                .messengerUserType(pojo.getMessengerUserType())
                .build();

        MessengerUser userAfterSave = MessengerUser.builder()
                .messengerUserId(3L)
                .username(pojo.getUsername())
                .password(pojo.getPassword())
                .name(pojo.getName())
                .surname(pojo.getSurname())
                .emailAddress(pojo.getEmail())
                .phoneNumber(pojo.getPhoneNumber())
                .messengerUserType(pojo.getMessengerUserType())
                .build();

        Mockito.when(messengerUserService.saveUser(userBeforeSave))
                .thenReturn(userAfterSave);

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pojo)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(userAfterSave)));
    }

    @Test
    void whenUpdateUserThatExistsById_thenReturnUpdatedUser() throws Exception {
        Mockito.when(messengerUserService.updateUserById(firstUserUpdate))
                .thenReturn(firstUserAfterUpdate);
        Mockito.when(messengerUserService.updateUserByUsername(firstUserUpdate))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(MockMvcRequestBuilders.put("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedFirstUserDTO))
                .param("user-id","1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(firstUserAfterUpdate)));
    }

    @Test
    void whenUpdateUserThatExists_andBothIdAndUsernameAreSpecified_thenUpdateUserById() throws Exception {
        Mockito.when(messengerUserService.updateUserById(firstUserUpdate))
                .thenReturn(firstUserAfterUpdate);
        Mockito.when(messengerUserService.updateUserByUsername(firstUserUpdate))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(MockMvcRequestBuilders.put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFirstUserDTO))
                        .param("user-id","1")
                        .param("username","anonymouse"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(firstUserAfterUpdate)));
    }

    @Test
    void whenUpdateUserThatExistsByUsername_thenReturnUpdatedUser() throws Exception{

        MessengerUser firstUserUpdateByUsername = MessengerUser.builder()
                .username("user1")
                .password(updatedFirstUserDTO.getPassword())
                .name(updatedFirstUserDTO.getName())
                .surname(updatedFirstUserDTO.getSurname())
                .emailAddress(updatedFirstUserDTO.getEmail())
                .phoneNumber(updatedFirstUserDTO.getPhoneNumber())
                .messengerUserType(updatedFirstUserDTO.getMessengerUserType())
                .build();

        Mockito.when(messengerUserService.updateUserByUsername(firstUserUpdateByUsername))
                .thenReturn(firstUserAfterUpdate);

        mockMvc.perform(MockMvcRequestBuilders.put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFirstUserDTO))
                        .param("username","user1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(firstUserAfterUpdate)));
    }

    @Test
    void whenUpdateUser_andNoIdOrUsernameIsSpecified_thenReturnHttpStatusBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFirstUserDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(String.format(MessengerUtils.Messages.EXCEPTION_REQUIRED_PARAMETERS_ARE_NULL.value(),
                        String.join(", ",JwtTokenAuth.Param.USERNAME.value(),JwtTokenAuth.Param.USER_ID.value()))));
    }

    @Test
    void whenUpdateUserThatDoesNotExist_thenReturnHttpStatusNotFound() throws Exception{

        MessengerUser firstUserUpdateByUsername = MessengerUser.builder()
                .username("user1")
                .password(updatedFirstUserDTO.getPassword())
                .name(updatedFirstUserDTO.getName())
                .surname(updatedFirstUserDTO.getSurname())
                .emailAddress(updatedFirstUserDTO.getEmail())
                .phoneNumber(updatedFirstUserDTO.getPhoneNumber())
                .messengerUserType(updatedFirstUserDTO.getMessengerUserType())
                .build();

        Mockito.when(messengerUserService.updateUserById(firstUserUpdate))
                .thenThrow(MessengerUserNotFoundException.class);
        Mockito.when(messengerUserService.updateUserByUsername(firstUserUpdateByUsername))
                .thenThrow(MessengerUserNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFirstUserDTO))
                        .param("user-id","1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(MessengerUtils.Messages.EXCEPTION_MESSENGER_USER_NOT_FOUND.value()));

        mockMvc.perform(MockMvcRequestBuilders.put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFirstUserDTO))
                        .param("username","user1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(MessengerUtils.Messages.EXCEPTION_MESSENGER_USER_NOT_FOUND.value()));
    }

    @Test
    void whenDeleteUserThatExists_thenReturnResponseStatusOk() throws Exception {
        Mockito.doNothing()
                .when(messengerUserService).deleteById(firstUser.getMessengerUserId());
        Mockito.doNothing()
                .when(messengerUserService).deleteByUsername(firstUser.getUsername());

        Mockito.when(messengerUserService.getUserTypeById(firstUser.getMessengerUserId()))
                        .thenReturn(MessengerUserType.USER);
        Mockito.when(messengerUserService.getUserTypeByUsername(firstUser.getUsername()))
                .thenReturn(MessengerUserType.USER);

        mockMvc.perform(MockMvcRequestBuilders.delete("/user").param("user-id",firstUser.getMessengerUserId().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.delete("/user").param("username",firstUser.getUsername()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.when(messengerUserService.getUserTypeById(firstUser.getMessengerUserId()))
                .thenReturn(MessengerUserType.ADMIN);
        Mockito.when(messengerUserService.getUserTypeByUsername(firstUser.getUsername()))
                .thenReturn(MessengerUserType.ADMIN);

        mockMvc.perform(MockMvcRequestBuilders.delete("/user").param("user-id",firstUser.getMessengerUserId().toString()))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
        mockMvc.perform(MockMvcRequestBuilders.delete("/user").param("username",firstUser.getUsername()))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void whenDeleteUserThatDoesNotExist_thenThrowHttpStatusNotFound() throws Exception {
        Mockito.doThrow(MessengerUserNotFoundException.class)
                .when(messengerUserService).deleteById(firstUser.getMessengerUserId());
        Mockito.doThrow(MessengerUserNotFoundException.class)
                .when(messengerUserService).deleteByUsername(firstUser.getUsername());

        Mockito.when(messengerUserService.getUserTypeById(firstUser.getMessengerUserId()))
                .thenThrow(MessengerUserNotFoundException.class);
        Mockito.when(messengerUserService.getUserTypeByUsername(firstUser.getUsername()))
                .thenThrow(MessengerUserNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.delete("/user").param("user-id",firstUser.getMessengerUserId().toString()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(MessengerUtils.Messages.EXCEPTION_MESSENGER_USER_NOT_FOUND.value()));
        mockMvc.perform(MockMvcRequestBuilders.delete("/user").param("username",firstUser.getUsername()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(MessengerUtils.Messages.EXCEPTION_MESSENGER_USER_NOT_FOUND.value()));
    }

    @Test
    void whenDeleteUser_andNoUsernameOrIdIsSpecified_thenThrowHttpStatusBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/user"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(String.format(MessengerUtils.Messages.EXCEPTION_REQUIRED_PARAMETERS_ARE_NULL.value(),
                        String.join(", ",JwtTokenAuth.Param.USERNAME.value(),JwtTokenAuth.Param.USER_ID.value()))));
    }
}