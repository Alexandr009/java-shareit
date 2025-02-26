package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    public Collection<User> getAll();
    Optional<User> getUserById(Long id);

    User addUser(User user);

    User updateUser(User updatedUser);

    void removeUser(long id);

  }
