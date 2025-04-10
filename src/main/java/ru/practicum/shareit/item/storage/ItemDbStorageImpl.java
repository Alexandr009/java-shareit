package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("ItemDbStorageImpl")
@Slf4j
@Repository
public class ItemDbStorageImpl implements ItemStorage {

    public HashMap<Integer, Item> itemMap = new HashMap<>();

    public ItemDbStorageImpl() {
        this.itemMap = new HashMap<>();
    }


    @Override
    public Collection<Item> getAllByUserId(long userId) {
        Collection<Item> result = itemMap.values().stream().filter(item -> item.getOwner().getId() == userId).collect(Collectors.toList());
        return result;
    }

    @Override
    public Collection<Item> getAllByText(long userId, String text) {
        Collection<Item> result = itemMap.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(item -> item.getAvailable().toString().contains("true"))
                .collect(Collectors.toList());
        return result;
    }

    @Override
    public Collection<Item> getAll() {
        return itemMap.values();
    }

    @Override
    public Item creat(ItemCreateDto item, User user) {
        Item itemNew = new Item();
        itemNew.setName(item.getName());
        itemNew.setDescription(item.getDescription());
        itemNew.setAvailable(item.getAvailable());
        itemNew.setId(item.getId());
        itemNew.setOwner(user);
        itemMap.put(item.getId(), itemNew);
        Item newItem = itemMap.get(item.getId());
        return newItem;
    }

    @Override
    public Item update(ItemCreateDto item) {
        Item itemNew = itemMap.get(item.getId());
        itemNew.setName(item.getName());
        itemNew.setDescription(item.getDescription());
        itemNew.setAvailable(item.getAvailable());
        return itemNew;
    }

    @Override
    public Item updatePatch(ItemPatchDto item) {
        Item itemNew = itemMap.get(item.getId());
        if (item.getName() != null) {
            itemNew.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemNew.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemNew.setAvailable(item.getAvailable());
        }
        return itemNew;
    }

    @Override
    public Item delete(Item item) {
        return null;
    }

    @Override
    public Optional<Item> get(long id) {
        Item item = itemMap.get((int) id);
        return Optional.ofNullable(item);
    }
}
