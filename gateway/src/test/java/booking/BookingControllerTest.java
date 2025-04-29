package booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.nio.charset.StandardCharsets;
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
    private BookingClient bookingClient;

    @InjectMocks
    private BookingController controller;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(1);
        bookingCreateDto.setStart(new Date(System.currentTimeMillis() - 10000));
        bookingCreateDto.setEnd(new Date(System.currentTimeMillis() + 10000));
    }

    @Test
    void findAll_shouldReturnBookings() throws Exception {
        when(bookingClient.getBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(Collections.singletonList(bookingCreateDto)));

        mvc.perform(get("/bookings")
                        .header(BookingController.SHARER_USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].itemId", is(bookingCreateDto.getItemId()), int.class));

        verify(bookingClient, times(1)).getBookings(anyLong(), eq("ALL"), anyInt(), anyInt());
    }

    @Test
    void findById_shouldReturnBooking() throws Exception {
        when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(bookingCreateDto));

        mvc.perform(get("/bookings/1")
                        .header(BookingController.SHARER_USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId", is(bookingCreateDto.getItemId()), int.class));

        verify(bookingClient, times(1)).getBooking(anyLong(), eq(1L));
    }

    @Test
    void findByOwner_shouldReturnOwnerBookings() throws Exception {
        when(bookingClient.getBookingsByOwner(anyLong(), anyString()))
                .thenReturn(ResponseEntity.ok(Collections.singletonList(bookingCreateDto)));

        mvc.perform(get("/bookings/owner")
                        .header(BookingController.SHARER_USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].itemId", is(bookingCreateDto.getItemId()), int.class));

        verify(bookingClient, times(1)).getBookingsByOwner(anyLong(), eq("ALL"));
    }

    @Test
    void create_shouldCreateBooking() throws Exception {
        when(bookingClient.create(anyLong(), any()))
                .thenReturn(ResponseEntity.ok(bookingCreateDto));

        mvc.perform(post("/bookings")
                        .header(BookingController.SHARER_USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId", is(bookingCreateDto.getItemId()), int.class));

        verify(bookingClient, times(1)).create(eq(1L), any());
    }

    @Test
    void update_shouldUpdateBooking() throws Exception {
        ResponseEntity<Object> responseEntity = ResponseEntity.ok().build();
        when(bookingClient.update(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(responseEntity);
        mvc.perform(patch("/bookings/1")
                        .header(BookingController.SHARER_USER_ID_HEADER, 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(bookingClient, times(1)).update(eq(1L), eq(1L), eq(true));
    }
}
