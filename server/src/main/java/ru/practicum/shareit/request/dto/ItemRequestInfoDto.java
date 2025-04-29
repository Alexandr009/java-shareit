package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Date;

@Data
public class ItemRequestInfoDto {
    Integer id;
    String description;
    User requestor;
    Date created;
    Collection<Item> items;
}
