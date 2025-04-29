package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookinService;
import ru.practicum.shareit.exception.ValidationException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookinService bookinService;

    @InjectMocks
    private BookingController controller;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private Booking booking;
    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        booking = new Booking();
        booking.setId(1);
        booking.setStart(new Date(System.currentTimeMillis() - 10000));
        booking.setEnd(new Date(System.currentTimeMillis() + 10000));

        bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(1);
        bookingCreateDto.setStart(new Date(System.currentTimeMillis() - 10000));
        bookingCreateDto.setEnd(new Date(System.currentTimeMillis() + 10000));
    }

    @Test
    void findAll_shouldReturnBookings() throws Exception {
        when(bookinService.findAll(anyString(), anyLong()))
                .thenReturn(Collections.singletonList(booking));

        mvc.perform(get("/bookings")
                        .header(BookingController.SHARER_USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(booking.getId()), int.class));

        verify(bookinService, times(1)).findAll("ALL", 1L);
    }

    @Test
    void findAll_shouldUseDefaultState() throws Exception {
        when(bookinService.findAll(eq("ALL"), anyLong()))
                .thenReturn(Collections.singletonList(booking));

        mvc.perform(get("/bookings")
                        .header(BookingController.SHARER_USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookinService, times(1)).findAll("ALL", 1L);
    }

    @Test
    void findById_shouldReturnBooking() throws Exception {
        when(bookinService.findById(anyLong(), anyLong()))
                .thenReturn(booking);

        mvc.perform(get("/bookings/1")
                        .header(BookingController.SHARER_USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), int.class));

        verify(bookinService, times(1)).findById(1L, 1L);
    }

    @Test
    void findByOwner_shouldReturnOwnerBookings() throws Exception {
        when(bookinService.findAllByUserId(anyLong(), anyString()))
                .thenReturn(Collections.singletonList(booking));

        mvc.perform(get("/bookings/owner")
                        .header(BookingController.SHARER_USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(booking.getId()), int.class));

        verify(bookinService, times(1)).findAllByUserId(1L, "ALL");
    }

    @Test
    void create_shouldCreateBooking() throws Exception {
        when(bookinService.create(any()))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                        .header(BookingController.SHARER_USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), int.class));

        verify(bookinService, times(1)).create(any());
    }

    @Test
    void update_shouldUpdateBooking() throws Exception {
        when(bookinService.update(anyInt(), anyLong(), anyBoolean()))
                .thenReturn(booking);

        mvc.perform(patch("/bookings/1")
                        .header(BookingController.SHARER_USER_ID_HEADER, 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), int.class));

        verify(bookinService, times(1)).update(1, 1L, true);
    }

    @Test
    void update_shouldUseDefaultApproved() throws Exception {
        when(bookinService.update(anyInt(), anyLong(), eq(false)))
                .thenReturn(booking);

        mvc.perform(patch("/bookings/1")
                        .header(BookingController.SHARER_USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookinService, times(1)).update(1, 1L, false);
    }
}
