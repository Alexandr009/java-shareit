package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.AutorDto;
import ru.practicum.shareit.user.model.User;

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
