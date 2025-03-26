package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.user.User;

@Data
public class ItemDto {
    Integer id;
    String name;
    String description;
    String available;
    User user;
}
