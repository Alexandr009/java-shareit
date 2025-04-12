package ru.practicum.shareit.item;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
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
        if (comment == null) {
            return null;
        }

        AutorDto autorDto = new AutorDto();
        if (comment.getAuthor() != null) {
            autorDto.setId(comment.getAuthor().getId());
            autorDto.setAuthorName(comment.getAuthor().getName());
            autorDto.setEmail(comment.getAuthor().getEmail());
        }

        CommentInfoDto commentInfoDto = new CommentInfoDto();
        commentInfoDto.setAuthor(autorDto);
        commentInfoDto.setItem(comment.getItem());
        commentInfoDto.setCreated(comment.getCreated());
        commentInfoDto.setText(comment.getText());
        commentInfoDto.setId(comment.getId());
        commentInfoDto.setAuthorName(comment.getAuthor() != null ? comment.getAuthor().getName() : null);

        return commentInfoDto;
    }

    public ItemDto toItemDto(Item item, List<CommentCreateDto> comments, Booking lastBooking, Booking nextBooking, long itemId) {
        if (item == null) {
            return null;
        }

        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setUser(item.getOwner());
        itemDto.setComments(comments != null ? comments : List.of());

        if (lastBooking != null && lastBooking.getItem() != null && item.getId() == lastBooking.getItem().getId()) {
            BookingCreateDto lastBookingDto = new BookingCreateDto();
            lastBookingDto.setId(lastBooking.getId());
            lastBookingDto.setStart(lastBooking.getStart());
            lastBookingDto.setEnd(lastBooking.getEnd());
            lastBookingDto.setStatus(lastBooking.getStatus());
            itemDto.setLastBooking(null);
        } else {
            itemDto.setLastBooking(null);
        }


        if (nextBooking != null && nextBooking.getItem() != null && item.getId() == nextBooking.getItem().getId()) {
            BookingCreateDto nextBookingDto = new BookingCreateDto();
            nextBookingDto.setId(nextBooking.getId());
            nextBookingDto.setStart(nextBooking.getStart());
            nextBookingDto.setEnd(nextBooking.getEnd());
            nextBookingDto.setStatus(nextBooking.getStatus());
            itemDto.setNextBooking(null);
        } else {
            itemDto.setNextBooking(null);
        }

        return itemDto;
    }
}