package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserDbStorageImpl;
import ru.practicum.shareit.validation.ValidationUtils;

import java.text.ParseException;
import java.util.Collection;
import java.util.Optional;

@Service
public class UserService {

    private final UserDbStorageImpl userDbStorage;

    @Autowired
    public UserService(UserDbStorageImpl userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    public Collection<User> findAll() {
        return userDbStorage.getAll();
    }

    public Optional<User> getUserById(long id) {
        return userDbStorage.getUserById(id);
    }

    public User create(User user) throws ParseException {
        ValidationUtils.validateUser(user);

        return userDbStorage.addUser(user);
    }

    public User update(User user) throws ParseException {
        ValidationUtils.validateUser(user);

        if (user.getId() == null || user.getId().toString().isBlank()) {
            throw new ConditionsNotMetException("ID must be specified");
        }

        Optional<User> existingUser = userDbStorage.getUserById((long) user.getId());
        if (existingUser.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", user.getId()));
        }

        return userDbStorage.updateUser(user);
    }
}
