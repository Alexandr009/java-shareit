package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.service.UserService;

import java.text.ParseException;
import java.util.Collection;
import java.util.Optional;

/**
 * TODO Sprint add-controllers.
 */
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
        log.info(String.format("findAll user started - %s",userService.findAll().toString()));
        Collection<User> user = userService.findAll();
        log.info(String.format("findAll user finished - %s",user.toString()));
        return user;
    }

    @GetMapping("/{id}")
    public Optional<Optional<User>> getUserById(@PathVariable long id) {
        log.info(String.format("getUserById started - %s - ", id));
        return Optional.ofNullable(userService.getUserById(id));
    }

    @PostMapping
    public User create(@RequestBody User user) throws ParseException {
        log.info(String.format("create user started - %s",String.valueOf(user)));
        User userNew = userService.create(user);
        log.info(String.format("create user finished - %s",userNew.toString()));
        return userNew;
    }

    @PutMapping
    public User update(@RequestBody User user) throws ParseException {
        log.info(String.format("update user started - %s",String.valueOf(user)));
        User userNew = userService.update(user);
        log.info(String.format("update user finished - %s",String.valueOf(userNew)));
        return userNew;
    }
}
