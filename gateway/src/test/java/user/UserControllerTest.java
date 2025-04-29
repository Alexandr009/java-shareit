package user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
        import static org.mockito.ArgumentMatchers.*;
        import static org.mockito.Mockito.*;
        import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserClient userClient;

    @InjectMocks
    private UserController controller;

    private final ObjectMapper mapper = new ObjectMapper();
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
        when(userClient.findAll())
                .thenReturn(ResponseEntity.ok(Collections.singletonList(user)));

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
        when(userClient.getUserById(anyLong()))
                .thenReturn(ResponseEntity.ok(user));

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

        verify(userClient, times(1)).getUserById(1L);
    }

    @Test
    void create_shouldCreateUser() throws Exception {
        when(userClient.create(any()))
                .thenReturn(ResponseEntity.ok(user));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

        verify(userClient, times(1)).create(any());
    }

    @Test
    void update_shouldUpdateUser() throws Exception {
        when(userClient.update(any()))
                .thenReturn(ResponseEntity.ok(user));

        mvc.perform(put("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

        verify(userClient, times(1)).update(any());
    }

    @Test
    void updatePatch_shouldPatchUser() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setName("John Updated");
        updatedUser.setEmail("updated@mail.com");

        when(userClient.updatePatch(any(), anyLong()))
                .thenReturn(ResponseEntity.ok(updatedUser));

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userPatchDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedUser.getId())))
                .andExpect(jsonPath("$.name", is(updatedUser.getName())))
                .andExpect(jsonPath("$.email", is(updatedUser.getEmail())));

        verify(userClient, times(1)).updatePatch(any(), eq(1L));
    }

//    @Test
//    void remove_shouldDeleteUser() throws Exception {
//        doNothing().when(userClient).remove(anyLong());
//
//        mvc.perform(delete("/users/1")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//
//        verify(userClient, times(1)).remove(1L);
//    }

    @Test
    void getUserById_whenUserNotFound_shouldReturnNotFound() throws Exception {
        when(userClient.getUserById(anyLong()))
                .thenReturn(ResponseEntity.notFound().build());

        mvc.perform(get("/users/999")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userClient, times(1)).getUserById(999L);
    }
}
