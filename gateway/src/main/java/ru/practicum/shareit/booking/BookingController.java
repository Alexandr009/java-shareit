package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    public static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> findAll(
            @RequestHeader(SHARER_USER_ID_HEADER) long userId,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Finding all bookings user {}", userId);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(
            @PathVariable long id,
            @RequestHeader(SHARER_USER_ID_HEADER) long userId) {
        log.info("Get booking {}", id);
        return bookingClient.getBooking(userId, id);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findByOwner(
            @RequestHeader(SHARER_USER_ID_HEADER) long userId,
            @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("Get bookings for owner {}", userId);
        return bookingClient.getBookingsByOwner(userId, state);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(SHARER_USER_ID_HEADER) long userId,
            @Valid @RequestBody BookingCreateDto booking) {
        log.info("Create booking {}", booking);
        return bookingClient.create(userId, booking);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(
            @RequestHeader(SHARER_USER_ID_HEADER) long userId,
            @PathVariable long id,
            @RequestParam boolean approved) {
        log.info("Update booking {}, approved={}", id, approved);
        return bookingClient.update(userId, id, approved);
    }
}