package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.AutorDto;

import java.util.List;

@NoArgsConstructor
@Component
public class ItemMapper {
    public CommentInfoDto toCommentDto(Comment comment) {
        CommentInfoDto commentInfoDto = new CommentInfoDto();
        AutorDto autorDto = new AutorDto();
        autorDto.setId(comment.getAuthor().getId());
        autorDto.setAuthorName(comment.getAuthor().getName());
        autorDto.setEmail(comment.getAuthor().getEmail());

        commentInfoDto.setAuthor(autorDto);
        commentInfoDto.setItem(comment.getItem());
        commentInfoDto.setCreated(comment.getCreated());
        commentInfoDto.setText(comment.getText());
        commentInfoDto.setId(comment.getId());
        commentInfoDto.setAuthorName(comment.getAuthor().getName());
        return commentInfoDto;
    }

    public ItemDto toItemDto(Item item, List<CommentCreateDto> comment, Booking lastBooking, Booking nextBooking, long id) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setUser(item.getOwner());

        itemDto.setComments(comment);

        BookingCreateDto bookingDto = new BookingCreateDto();
        if (lastBooking != null && id != lastBooking.getId()) {
            bookingDto.setId(lastBooking.getId());
            bookingDto.setStatus(lastBooking.getStatus());
            bookingDto.setEnd(lastBooking.getEnd());
            bookingDto.setStart(lastBooking.getStart());
            bookingDto.setStatus(lastBooking.getStatus());
            itemDto.setLastBooking(bookingDto);
        } else {
            itemDto.setLastBooking(null);
        }


        BookingCreateDto bookingDtoNext = new BookingCreateDto();
        if (nextBooking != null && id != nextBooking.getId()) {
            bookingDtoNext.setId(nextBooking.getId());
            bookingDtoNext.setStatus(nextBooking.getStatus());
            bookingDtoNext.setEnd(nextBooking.getEnd());
            bookingDtoNext.setStart(nextBooking.getStart());
            bookingDtoNext.setStatus(nextBooking.getStatus());
            itemDto.setNextBooking(bookingDtoNext);
            itemDto.setNextBooking(null);
        } else {
            itemDto.setNextBooking(null);
        }


        return itemDto;

    }
}
