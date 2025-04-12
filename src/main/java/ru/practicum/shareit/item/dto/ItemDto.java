package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Data
public class ItemDto {
    Integer id;
    String name;
    String description;
    Boolean available;
    User user;
    List<CommentCreateDto> comments;
    BookingCreateDto lastBooking;
    BookingCreateDto nextBooking;
}
