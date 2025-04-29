package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookinService;
import ru.practicum.shareit.booking.storage.BookinRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import java.util.Date;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookinServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookinRepository bookinRepository;

    @InjectMocks
    private BookinService bookinService;

    private User user;
    private Item item;
    private Booking booking;
    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setName("Test User");
        user.setEmail("test@mail.com");

        item = new Item();
        item.setId(1);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);

        booking = new Booking();
        booking.setId(1);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(new Date(System.currentTimeMillis() - 10000));
        booking.setEnd(new Date(System.currentTimeMillis() + 10000));
        booking.setStatus(Status.WAITING);

        bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(1);
        bookingCreateDto.setBookerId(1);
        bookingCreateDto.setStart(new Date(System.currentTimeMillis() - 10000));
        bookingCreateDto.setEnd(new Date(System.currentTimeMillis() + 10000));
    }

    @Test
    void findAll_shouldReturnAllBookingsForAllStatus() {
        when(bookinRepository.findByBookerId(anyInt()))
                .thenReturn(Collections.singletonList(booking));

        Collection<Booking> result = bookinService.findAll("ALL", 1L);

        assertThat(result, hasSize(1));
        assertThat(result.iterator().next(), equalTo(booking));
        verify(bookinRepository, times(1)).findByBookerId(1);
    }

    @Test
    void findAll_shouldReturnRejectedBookings() {
        booking.setStatus(Status.REJECTED);
        when(bookinRepository.findAllByStatus(Status.REJECTED))
                .thenReturn(Collections.singletonList(booking));

        Collection<Booking> result = bookinService.findAll("REJECTED", 1L);

        assertThat(result, hasSize(1));
        assertThat(result.iterator().next().getStatus(), equalTo(Status.REJECTED));
        verify(bookinRepository, times(1)).findAllByStatus(Status.REJECTED);
    }

    @Test
    void findById_shouldReturnBooking() {
        when(bookinRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Booking result = bookinService.findById(1L, 1L);

        assertThat(result, equalTo(booking));
        verify(bookinRepository, times(1)).findById(1L);
    }

    @Test
    void findById_shouldThrowWhenAccessDenied() {
        User otherUser = new User();
        otherUser.setId(2);
        booking.setBooker(otherUser);
        item.setOwner(otherUser);

        when(bookinRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookinService.findById(1L, 1L));
        verify(bookinRepository, times(1)).findById(1L);
    }

    @Test
    void findAllByUserId_shouldReturnUserBookings() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookinRepository.findByBookerId(anyInt()))
                .thenReturn(Collections.singletonList(booking));

        Collection<Booking> result = bookinService.findAllByUserId(1L, "ALL");

        assertThat(result, hasSize(1));
        assertThat(result.iterator().next(), equalTo(booking));
        verify(bookinRepository, times(1)).findByBookerId(1);
    }

    @Test
    void findAllByUserId_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookinService.findAllByUserId(1, "ALL"));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void create_shouldCreateBooking() throws Exception {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookinRepository.save(any()))
                .thenReturn(booking);

        Booking result = bookinService.create(bookingCreateDto);

        assertThat(result, equalTo(booking));
        verify(bookinRepository, times(1)).save(any());
    }

    @Test
    void create_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookinService.create(bookingCreateDto));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void create_shouldThrowWhenItemNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookinService.create(bookingCreateDto));
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void create_shouldThrowWhenItemNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookinService.create(bookingCreateDto));
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void update_shouldApproveBooking() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookinRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookinRepository.save(any()))
                .thenReturn(booking);

        Booking result = bookinService.update(1, 1L, true);

        assertThat(result.getStatus(), equalTo(Status.APPROVED));
        verify(bookinRepository, times(1)).save(any());
    }

    @Test
    void update_shouldRejectBooking() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookinRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookinRepository.save(any()))
                .thenReturn(booking);

        Booking result = bookinService.update(1, 1L, false);

        assertThat(result.getStatus(), equalTo(Status.REJECTED));
        verify(bookinRepository, times(1)).save(any());
    }

    @Test
    void update_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> bookinService.update(1, 1L, true));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void update_shouldThrowWhenBookingNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookinRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookinService.update(1, 1L, true));
        verify(bookinRepository, times(1)).findById(1L);
    }
}
