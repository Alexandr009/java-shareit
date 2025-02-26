package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {
    public Collection<Item> getAll();
    public Item creat(Item item);
    public Item update(Item item);
    public Item delete(Item item);
    public Optional<Item> get(long id);

}
