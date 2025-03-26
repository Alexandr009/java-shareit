package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Item findByName(String name);
    Collection<Item> findByOwner(User owner);
}
