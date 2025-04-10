package ru.practicum.shareit.item.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentCreateDto {
    String text;
//    Item item;
//    User author;
//    Date created;
}
