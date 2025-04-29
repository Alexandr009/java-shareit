package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import java.util.Date;

@Setter
@Getter

@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    Integer id;
    String text;
    Item item;
    User author;
    Date created;
}
