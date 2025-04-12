package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.storage.UserDbStorageImpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(long id) {
        return userRepository.findById(id);
    }

    public User create(User user) throws ParseException {
        List<User> userList = new ArrayList<>(userRepository.findAll());
        List<User> checkName = userList.stream().filter(u -> u.getName().equals(user.getName())).toList();
        List<User> checkEmail = userList.stream().filter(u -> u.getEmail().equals(user.getEmail())).toList();

        if (!checkName.isEmpty() || !checkEmail.isEmpty()) {
            throw new ConditionsNotMetException(String.format("User with name '%s' already exists", user.getName()));
        }

        return userRepository.save(user);
    }

    public User updatePatch(UserPatchDto user, long id) throws ParseException {
        List<User> userList = new ArrayList<>(userRepository.findAll());
        List<User> checkEmail = userList.stream().filter(u -> u.getEmail().equals(user.getEmail())).toList();

        if (!checkEmail.isEmpty()) {
            throw new ConditionsNotMetException(String.format("User with email '%s' already exists", user.getName()));
        }


        Optional<User> userCheck = userRepository.findById(id);
        if (!userCheck.isEmpty()) {
            if(userCheck.get().getName() != user.getName() && user.getName() != null) {
                userCheck.get().setName(user.getName());
            }

            if(user.getEmail() != null && userCheck.get().getEmail() != user.getEmail()) {
                userCheck.get().setEmail(user.getEmail());
            }
            return userRepository.save(userCheck.get());
        }
        throw new NotFoundException(String.format("User with id = %s not found", id));
    }

    public User update(User user) throws ParseException {
        Optional<User> userCheck = userRepository.findById(Long.valueOf(user.getId()));
        if (!userCheck.isEmpty()) {
            return userRepository.save(user);
        }
        throw new NotFoundException(String.format("User with id = %s not found", user.getId()));
    }

    public void remove(long id) {
        userRepository.deleteById(id);
    }


}
