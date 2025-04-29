package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController controller;
    private MockMvc mvc;
    private User user;
    private UserPatchDto userPatchDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        user = new User();
        user.setId(1);
        user.setName("John Doe");
        user.setEmail("john.doe@mail.com");

        userPatchDto = new UserPatchDto();
        userPatchDto.setName("John Updated");
        userPatchDto.setEmail("updated@mail.com");
    }

    @Test
    void findAll_shouldReturnUsersList() throws Exception {
        when(userService.findAll())
                .thenReturn(Collections.singletonList(user));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(user.getId())))
                .andExpect(jsonPath("$[0].name", is(user.getName())))
                .andExpect(jsonPath("$[0].email", is(user.getEmail())));
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(Optional.of(user));

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void create_shouldCreateUser() throws Exception {
        when(userService.create(any()))
                .thenReturn(user);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

        verify(userService, times(1)).create(any());
    }

    @Test
    void update_shouldUpdateUser() throws Exception {
        when(userService.update(any()))
                .thenReturn(user);

        mvc.perform(put("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

        verify(userService, times(1)).update(any());
    }

    @Test
    void updatePatch_shouldPatchUser() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setName("John Updated");
        updatedUser.setEmail("updated@mail.com");

        when(userService.updatePatch(any(), anyLong()))
                .thenReturn(updatedUser);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userPatchDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedUser.getId())))
                .andExpect(jsonPath("$.name", is(updatedUser.getName())))
                .andExpect(jsonPath("$.email", is(updatedUser.getEmail())));

        verify(userService, times(1)).updatePatch(any(), eq(1L));
    }

    @Test
    void remove_shouldDeleteUser() throws Exception {
        doNothing().when(userService).remove(anyLong());

        mvc.perform(delete("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).remove(1L);
    }

    @Test
    void getUserById_whenUserNotFound_shouldReturnNotFound() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(Optional.empty());

        mvc.perform(get("/users/999")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).getUserById(999L);
    }
}