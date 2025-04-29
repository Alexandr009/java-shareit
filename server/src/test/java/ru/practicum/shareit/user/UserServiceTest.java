package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserPatchDto userPatchDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setName("John Doe");
        user.setEmail("john.doe@mail.com");

        userPatchDto = new UserPatchDto();
        userPatchDto.setName("John Updated");
        userPatchDto.setEmail("updated@mail.com");
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        Collection<User> result = userService.findAll();

        assertThat(result, hasSize(1));
        assertThat(result.iterator().next(), equalTo(user));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_shouldReturnUserWhenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1);

        assertTrue(result.isPresent());
        assertThat(result.get(), equalTo(user));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_shouldReturnEmptyWhenNotExists() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(999L);

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void create_shouldSaveNewUser() throws ParseException {
        when(userRepository.findAll()).thenReturn(List.of());
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.create(user);

        assertThat(result, equalTo(user));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void create_shouldThrowWhenNameExists() {
        User existingUser = new User();
        existingUser.setName("John Doe");
        existingUser.setEmail("john.doe@mail.com");
        when(userRepository.findAll()).thenReturn(List.of(existingUser));
        assertThrows(ConditionsNotMetException.class, () -> userService.create(user));
    }

    @Test
    void updatePatch_shouldUpdateUserFields() throws ParseException {
        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@mail.com");

        when(userRepository.findAll()).thenReturn(List.of());
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User result = userService.updatePatch(userPatchDto, 1);

        assertThat(result.getName(), equalTo("John Updated"));
        assertThat(result.getEmail(), equalTo("updated@mail.com"));
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void updatePatch_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updatePatch(userPatchDto, 999L));
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_shouldUpdateUser() throws ParseException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.update(user);

        assertThat(result, equalTo(user));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void update_shouldThrowWhenUserNotFound() {
        User nonExistingUser = new User();
        nonExistingUser.setId(999);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.update(nonExistingUser));
    }

    @Test
    void remove_shouldDeleteUser() {
        doNothing().when(userRepository).deleteById(1L);

        userService.remove(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }
}
