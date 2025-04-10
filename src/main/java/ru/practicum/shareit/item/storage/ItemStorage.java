package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {
    public Collection<Item> getAll();

    public Collection<Item> getAllByUserId(long userId);

    public Collection<Item> getAllByText(long userId, String text);

    public Item creat(ItemCreateDto item, User user);

    public Item update(ItemCreateDto item);

    public Item updatePatch(ItemPatchDto item);

    public Item delete(Item item);

    public Optional<Item> get(long id);
}
