package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.AutorDto;
import ru.practicum.shareit.user.model.User;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookingDto {
    Integer id;
    Date start;
    Date end;
    Item item;
    AutorDto author;
    Status status;
}
