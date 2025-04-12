package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.dto.AutorDto;

public class BookingMapper {
    public BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        AutorDto autorDto = new AutorDto();
        autorDto.setId(booking.getBooker().getId());
        autorDto.setAuthorName(booking.getBooker().getName());
        autorDto.setEmail(booking.getBooker().getEmail());

        bookingDto.setAuthor(autorDto);
        bookingDto.setItem(booking.getItem());
        return bookingDto;
    }
}
