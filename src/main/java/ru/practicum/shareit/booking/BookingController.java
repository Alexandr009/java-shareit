package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookinService;
import ru.practicum.shareit.exception.ValidationException;

import java.text.ParseException;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    public static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    @Autowired
    private final BookinService bookinService;

    public BookingController(BookinService bookinService) {
        this.bookinService = bookinService;
    }

    @GetMapping
    public Collection<Booking> findAll(@RequestHeader(SHARER_USER_ID_HEADER) long userId, @RequestParam(required = false) String state) {
        log.info("Finding all bookings user %s", userId);
        if (userId == 0) {
            throw new ValidationException("user cannot be empty");
        }
        if (state == null) {
            state = "ALL";
        }
        return bookinService.findAll(state, userId);
    }

    @GetMapping("/{id}")
    public Booking findById(@PathVariable("id") long id, @RequestHeader(SHARER_USER_ID_HEADER) long userId) {
        log.info(String.format("create started - %s", String.valueOf(id)));
        if (id == 0) {
            throw new ValidationException("id cannot be empty");
        }
        return bookinService.findById(id, userId);
    }

    @GetMapping("/owner")
    public Collection<Booking> findByOwner(@RequestHeader(SHARER_USER_ID_HEADER) long userId) {
        log.info(String.format("create started - %s", String.valueOf(userId)));
        if (userId == 0) {
            throw new ValidationException("owner cannot be empty");
        }
        return bookinService.findAllByUserId(userId,"ALL");
    }

    @PostMapping
    Booking create(@RequestHeader(SHARER_USER_ID_HEADER) long userId,@RequestBody BookingCreateDto booking) throws ParseException {
        log.info(String.format("create started - %s", String.valueOf(booking)));
        booking.setBookerId((int) userId);
        Booking bookingNew = bookinService.create(booking);
        return bookingNew;
    }

    @PatchMapping("/{id}")
    Booking update(@RequestHeader(SHARER_USER_ID_HEADER) long userId, @PathVariable int id, @RequestParam(required = false) boolean approved){
        log.info(String.format("update started - userId %s, bookingId %s", String.valueOf(userId), String.valueOf(id))); //, String.valueOf(approved));
        Booking booking = bookinService.update(id, userId, approved);
        return booking;
    }

}
