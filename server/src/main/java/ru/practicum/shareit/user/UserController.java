package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.Create;
import ru.practicum.shareit.user.model.Update;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.text.ParseException;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info(String.format("findAll user started - %s", userService.findAll().toString()));
        Collection<User> user = userService.findAll();
        log.info(String.format("findAll user finished - %s", user.toString()));
        return user;
    }

    @GetMapping("/{id}")
    public Optional<Optional<User>> getUserById(@PathVariable long id) {
        log.info(String.format("getUserById started - %s - ", id));
        return Optional.ofNullable(userService.getUserById(id));
    }

    @PostMapping
    public User create( @RequestBody User user) throws ParseException {
        log.info(String.format("create user started - %s", String.valueOf(user)));
        User userNew = userService.create(user);
        log.info(String.format("create user finished - %s", userNew.toString()));
        return userNew;
    }

    @PutMapping
    public User update(@RequestBody User user) throws ParseException {
        log.info(String.format("update user started - %s", String.valueOf(user)));
        User userNew = userService.update(user);
        log.info(String.format("update user finished - %s", String.valueOf(userNew)));
        return userNew;
    }

    @PatchMapping("/{id}")
    public User updatePatch(@RequestBody UserPatchDto user, @PathVariable long id) throws ParseException {
        log.info(String.format("update user started - %s", String.valueOf(user)));
        User userNew = userService.updatePatch(user, id);
        User user1 = new User();
        user1.setEmail(user.getEmail());
        user1.setName(user.getName());
        user1.setId((int) id);
        log.info(String.format("update user finished - %s", String.valueOf(userNew)));
        return userNew;
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable long id) {
        log.info(String.format("removeUserById started - %s - ", id));
        userService.remove(id);
    }

}
