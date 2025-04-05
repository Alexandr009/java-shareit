package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemDbStorageImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.storage.UserDbStorageImpl;

import java.text.ParseException;
import java.util.Collection;
import java.util.Optional;

@Service
public class ItemService {
    private final ItemDbStorageImpl itemDbStorage;
    private final UserDbStorageImpl userDbStorage;
    private int nextID;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemService(ItemDbStorageImpl itemDbStorage, UserDbStorageImpl userDbStorage, ItemRepository itemRepository, UserRepository userRepository) {
        this.itemDbStorage = itemDbStorage;
        this.userDbStorage = userDbStorage;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public Collection<Item> findAll() {
        //return itemDbStorage.getAll();
        return itemRepository.findAll();
    }

    public Collection<Item> findAllByUserId(long userId) {
        //return itemDbStorage.getAllByUserId(userId);
        Optional<User> user = userRepository.findById(userId);
        return itemRepository.findByOwner(user.orElse(null));
    }

    public Collection<Item> findAllByText(long userId, String text) {
        //return itemDbStorage.getAllByText(userId, text);//
        return itemRepository.searchByText(userId, text);//
    }

    public Optional<Item> getItemById(long id) {
        //return itemDbStorage.get(id);
        return itemRepository.findById(id);
    }

    public Item create(ItemCreateDto item) throws ParseException {
        //Optional<User> existingUser = userDbStorage.getUserById(item.getUserId());
        Optional<User> existingUser = userRepository.findById(item.getUserId());
        if (existingUser.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", item.getId()));
        }
        if (item.getName() == null || item.getName().isEmpty()) {
            throw new ValidationException("Name cannot be empty");
        }
       // nextID++;
        //item.setId(nextID);
        //Item itemNew = itemDbStorage.creat(item, existingUser.get());
        Item itemNew = new Item();
        itemNew.setName(item.getName());
        itemNew.setOwner(existingUser.get());
        itemNew.setAvailable(item.getAvailable());
        itemNew.setDescription(item.getDescription());
        itemNew = itemRepository.save(itemNew);

        return itemNew;
    }

    public Item update(ItemCreateDto item) throws ParseException {
        //Optional<Item> existingItem = itemDbStorage.get(item.getId());
        Optional<Item> existingItem = itemRepository.findById(Long.valueOf(item.getId()));
        if (existingItem.isEmpty()) {
            throw new NotFoundException(String.format("Item with id = %s not found", item.getId()));
        }
        Item currentItem = existingItem.get();
        if (item.getUserId() != (int) currentItem.getOwner().getId()) {
            throw new NotFoundException("User id wrong");
        }

        Item itemNew = new Item();
        itemNew.setName(item.getName());
       // itemNew.setOwner(item.getUserId());
        itemNew.setAvailable(item.getAvailable());
        itemNew.setDescription(item.getDescription());
        itemNew = itemRepository.save(itemNew);

        //return itemDbStorage.update(item);
        return itemNew;
    }

    public Item updatePatch(ItemPatchDto item) throws ParseException {
        //Optional<Item> existingItem = itemDbStorage.get(item.getId());
        Item existingItem = itemRepository.findById(Long.valueOf(item.getId()))
                .orElseThrow(() -> new NotFoundException(
                        String.format("Item with id = %d not found", item.getId())));

        if (item.getUserId() != Long.valueOf(existingItem.getOwner().getId())) {
            throw new NotFoundException("User is not the owner of this item");
        }

        //existingItem.setOwner(existingItem.getOwner());

        if (item.getName() != null && !item.getName().isBlank()) {
            existingItem.setName(item.getName());
        }

        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            existingItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }
        return itemRepository.save(existingItem);

    }
}
