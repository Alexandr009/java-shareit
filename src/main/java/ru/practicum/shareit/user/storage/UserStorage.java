package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserPatchDto;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    public Collection<User> getAll();

    Optional<User> getUserById(long id);

    User addUser(User user);

    User updateUser(User updatedUser);

    User patchUser(UserPatchDto updatedUser, long id);

    void removeUser(long id);

}
