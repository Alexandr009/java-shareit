package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.mapper.ItemRowMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component("ItemDbStorageImpl")
@Slf4j
@Repository
public class ItemDbStorageImpl implements ItemStorage {

    private final JdbcTemplate jdbc;
    private final ItemRowMapper mapper;

    @Autowired
    public ItemDbStorageImpl(JdbcTemplate jdbc, ItemRowMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }
    /*
        Integer id;
        String name;
        String description;
        String available;
        User user;
        ItemRequest request;
 */

    @Override
    public Collection<Item> getAll() {
        String query = "SELECT * FROM item";
        Collection<Item> item = jdbc.query(query, mapper);
        return item;
    }

    @Override
    public Item creat(Item item) {
        String sql = "INSERT INTO item (name, description) " + "VALUES (?, ?)";
        jdbc.update(sql, item.getName(), item.getDescription());
        String query = "SELECT * FROM item WHERE name = ? and description = ?";
        return jdbc.queryForObject(query,mapper,item.getName(), item.getDescription());
    }

    @Override
    public Item update(Item item) {
        return null;
    }

    @Override
    public Item delete(Item item) {
        return null;
    }

    @Override
    public Optional<Item> get(long id) {
        String query = "SELECT * FROM item WHERE id = ?";
        try {
            Item result = jdbc.queryForObject(query, mapper, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }
}
