package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.User;

@Setter
@Getter
@Data
public class ItemDto {
    Integer id;
    String name;
    String description;
    String available;
    User user;
}
