package ru.practicum.shareit.item.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

@Setter
@Getter
public class Item {

    Integer id;
    String name;
    String description;
    Boolean available;
    User owner;

    //ItemRequest itemRequest;
}
