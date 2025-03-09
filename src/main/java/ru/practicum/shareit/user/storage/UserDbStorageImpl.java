package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserPatchDto;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Component(value = "UserDbStorageImpl")
@Slf4j
@Repository
public class UserDbStorageImpl implements UserStorage {

    public HashMap<Integer, User> userMap = new HashMap<>();

    public UserDbStorageImpl() {
        this.userMap = new HashMap<>();
    }

    @Override
    public Collection<User> getAll() {
        return userMap.values();
    }

    @Override
    public Optional<User> getUserById(long id) {
        User user = userMap.get((int) id);
        return Optional.ofNullable(user);

    }

    @Override
    public User addUser(User user) {
        userMap.put(user.getId(), user);
        User newUser = userMap.get(user.getId());
        return newUser;
    }

    @Override
    public User updateUser(User user) {
        User userOld = userMap.get(user.getId());
        userOld.setEmail(user.getEmail());
        userOld.setName(user.getName());
        return userOld;
    }

    @Override
    public User patchUser(UserPatchDto updatedUser, long id) {
        User userOld = userMap.get((int) id);
        if (updatedUser.getEmail() != null) {
            userOld.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getName() != null) {
            userOld.setName(updatedUser.getName());
        }
        return userOld;
    }

    @Override
    public void removeUser(long id) {
        userMap.remove((int) id);
    }
}
