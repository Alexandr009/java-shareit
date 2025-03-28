package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.storage.UserDbStorageImpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserDbStorageImpl userDbStorage;
    private int nextID;

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
        List<User> userList = new ArrayList<>(userDbStorage.getAll());
        List<User> checkName = userList.stream().filter(u -> u.getName().equals(user.getName())).toList();
        List<User> checkEmail = userList.stream().filter(u -> u.getEmail().equals(user.getEmail())).toList();

        if (!checkName.isEmpty() || !checkEmail.isEmpty()) {
            throw new ConditionsNotMetException(String.format("User with name '%s' already exists", user.getName()));
        }

        nextID++;
        user.setId(nextID);
        return userDbStorage.addUser(user);
    }

    public User updatePatch(UserPatchDto user, long id) throws ParseException {
        List<User> userList = new ArrayList<>(userDbStorage.getAll());
        List<User> checkEmail = userList.stream().filter(u -> u.getEmail().equals(user.getEmail())).toList();

        if (!checkEmail.isEmpty()) {
            throw new ConditionsNotMetException(String.format("User with email '%s' already exists", user.getName()));
        }

        Optional<User> userCheck = userDbStorage.getUserById(id);
        if (!userCheck.isEmpty()) {
            return userDbStorage.patchUser(user, id);
        }
        throw new NotFoundException(String.format("User with id = %s not found", id));
    }

    public User update(User user) throws ParseException {
        Optional<User> userCheck = userDbStorage.getUserById(Long.valueOf(user.getId()));
        if (!userCheck.isEmpty()) {
            return userDbStorage.updateUser(user);
        }
        throw new NotFoundException(String.format("User with id = %s not found", user.getId()));
    }

    public void remove(long id) {
        userDbStorage.removeUser(id);
    }

    private long getNextId() {
        Integer size = userDbStorage.userMap.size();
        return userDbStorage.userMap.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }

    private boolean existsByName(String name) {
        return userDbStorage.getAll().stream()
                .anyMatch(user -> user.getName().equalsIgnoreCase(name));
    }
}
