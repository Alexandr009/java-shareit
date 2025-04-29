package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

public class ItemRequest {

    Integer id;
    String description;
    User requestor;
    Date created;

}
