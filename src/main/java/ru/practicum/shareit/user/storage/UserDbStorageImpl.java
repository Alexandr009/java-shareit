package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserRowMapper;

import java.util.Collection;
import java.util.Optional;

@Component("UserDbStorageImpl")
@Slf4j
@Repository
public class UserDbStorageImpl implements UserStorage {

    private final JdbcTemplate jdbc;
    private final UserRowMapper mapper;

    @Autowired
    public UserDbStorageImpl(JdbcTemplate jdbc, UserRowMapper mapper) {
        this.mapper = mapper;
        this.jdbc = jdbc;
    }

    @Override
    public Collection<User> getAll() {
        String sqlRequest = "SELECT * FROM users";
        return jdbc.query(sqlRequest, mapper);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        String query = "SELECT * FROM users WHERE id = ?";
        try {
            User result = jdbc.queryForObject(query, mapper, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public User addUser(User user) {
        String sqlCreateUser = "INSERT INTO users (email, name) " +
                "VALUES (?,?)";
        jdbc.update(sqlCreateUser,
                user.getEmail(),
                user.getName());
        String query = "SELECT * FROM users WHERE email = ?";
        return jdbc.queryForObject(query, mapper, user.getEmail());
    }

    @Override
    public User updateUser(User user) {
        String sqlUpdateUser = "UPDATE users SET email = ?, name = ?, WHERE id= ?";
        jdbc.update(sqlUpdateUser,
                user.getEmail(),
                user.getName(),
                user.getId());
        String query = "SELECT * FROM users WHERE id = ?";
        return jdbc.queryForObject(query, mapper, user.getId());
    }

    @Override
    public void removeUser(long id) {

    }
}
