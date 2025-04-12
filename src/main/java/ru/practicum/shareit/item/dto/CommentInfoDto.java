package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.AutorDto;

import java.util.Date;

@Getter
@Setter
public class CommentInfoDto {

    Integer id;
    String text;
    Item item;
    AutorDto author;
    Date created;
    String authorName;
}
