package ru.practicum.shareit.item.model;

import io.micrometer.core.ipc.http.HttpSender;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Setter
@Getter
@Data
public class Item {
    Integer id;
    String name;
    String description;
    String available;
    User user;
    ItemRequest request;
}
