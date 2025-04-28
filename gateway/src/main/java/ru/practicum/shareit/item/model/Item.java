package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//import ru.practicum.shareit.request.model.ItemRequest;
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
