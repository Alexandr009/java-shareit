package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemDbStorageImpl;

import java.text.ParseException;
import java.util.Collection;
import java.util.Optional;

@Service
public class ItemService {
    private final ItemDbStorageImpl itemDbStorage;

    @Autowired
    public ItemService(ItemDbStorageImpl itemDbStorage) {
        this.itemDbStorage = itemDbStorage;
    }

    public Collection<Item> findAll() {
        return itemDbStorage.getAll();
    }

    public Optional<Item> getItemById(long id) {
        return itemDbStorage.get(id);
    }

    public Item create(ItemCreateDto item) throws ParseException {
        return itemDbStorage.creat(item);
    }

    public Item update(Item item) throws ParseException {
        Optional<Item> existingItem = itemDbStorage.get(item.getId());
        if (existingItem.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", item.getId()));
        }

        return itemDbStorage.update(item);
    }
}
