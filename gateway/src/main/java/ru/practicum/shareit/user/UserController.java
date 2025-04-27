package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.Create;
import ru.practicum.shareit.user.model.Update;
import ru.practicum.shareit.user.model.User;

import java.text.ParseException;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Request to get all users");
        ResponseEntity<Object> response = userClient.findAll();
        log.info("Received response for all users");
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable long id) {
        log.info("Request to get user by id: {}", id);
        ResponseEntity<Object> response = userClient.getUserById(id);
        log.info("Received response for user with id: {}", id);
        return response;
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @Validated({Create.class, Update.class}) @RequestBody User user) throws ParseException {
        log.info("Request to create user: {}", user);
        ResponseEntity<Object> response = userClient.create(user);
        log.info("User created: {}", response.getBody());
        return response;
    }

    @PutMapping
    public ResponseEntity<Object> update(
            @Validated({Create.class, Update.class}) @RequestBody User user) throws ParseException {
        log.info("Request to update user: {}", user);
        ResponseEntity<Object> response = userClient.update(user);
        log.info("User updated: {}", response.getBody());
        return response;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updatePatch(
            @Validated({Create.class, Update.class}) @RequestBody UserPatchDto user,
            @PathVariable long id) throws ParseException {
        log.info("Request to patch user with id {}: {}", id, user);
        ResponseEntity<Object> response = userClient.updatePatch(user, id);
        log.info("User patched: {}", response.getBody());
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> remove(@PathVariable long id) {
        log.info("Request to delete user with id: {}", id);
        ResponseEntity<Object> response = userClient.remove(id);
        log.info("User with id {} deleted", id);
        return response;
    }
}