package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemDbStorageImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserDbStorageImpl;

import java.text.ParseException;
import java.util.Collection;
import java.util.Optional;

@Service
public class ItemService {
    private final ItemDbStorageImpl itemDbStorage;
    private final UserDbStorageImpl userDbStorage;
    private int nextID;

    @Autowired
    public ItemService(ItemDbStorageImpl itemDbStorage, UserDbStorageImpl userDbStorage) {
        this.itemDbStorage = itemDbStorage;
        this.userDbStorage = userDbStorage;
    }

    public Collection<Item> findAll() {
        return itemDbStorage.getAll();
    }

    public Collection<Item> findAllByUserId(long userId) {
        return itemDbStorage.getAllByUserId(userId);
    }

    public Collection<Item> findAllByText(long userId, String text) {
        return itemDbStorage.getAllByText(userId, text);
    }

    public Optional<Item> getItemById(long id) {
        return itemDbStorage.get(id);
    }

    public Item create(ItemCreateDto item) throws ParseException {
        Optional<User> existingUser = userDbStorage.getUserById(item.getUserId());
        if (existingUser.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", item.getId()));
        }
        if (item.getName() == null || item.getName().isEmpty()) {
            throw new ValidationException("Name cannot be empty");
        }
        nextID++;
        item.setId(nextID);
        Item itemNew = itemDbStorage.creat(item, existingUser.get());
        return itemNew;
    }

    public Item update(ItemCreateDto item) throws ParseException {
        Optional<Item> existingItem = itemDbStorage.get(item.getId());
        if (existingItem.isEmpty()) {
            throw new NotFoundException(String.format("Item with id = %s not found", item.getId()));
        }
        Item currentItem = existingItem.get();
        if (item.getUserId() != (int) currentItem.getUser().getId()) {
            throw new NotFoundException("User id wrong");
        }

        return itemDbStorage.update(item);
    }

    public Item updatePatch(ItemPatchDto item) throws ParseException {
        Optional<Item> existingItem = itemDbStorage.get(item.getId());
        if (existingItem.isEmpty()) {
            throw new NotFoundException(String.format("Item with id = %s not found", item.getId()));
        }
        Item currentItem = existingItem.get();
        if (item.getUserId() != (int) currentItem.getUser().getId()) {
            throw new NotFoundException("User id wrong");
        }

        return itemDbStorage.updatePatch(item);
    }
}
