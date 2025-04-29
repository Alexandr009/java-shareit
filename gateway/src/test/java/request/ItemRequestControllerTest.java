package request;

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
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private ItemRequestClient itemRequestClient;
    @InjectMocks
    private ItemRequestController controller;
    private MockMvc mvc;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Need a drill");
    }

    @Test
    void createItemRequest_shouldCreateRequest() throws Exception {
        when(itemRequestClient.createItemRequest(any(), anyLong()))
                .thenReturn(ResponseEntity.ok(itemRequestDto));

        mvc.perform(post("/requests")
                        .header(ItemRequestController.SHARER_USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));

        verify(itemRequestClient, times(1)).createItemRequest(any(), eq(1L));
    }

    @Test
    void getItemRequests_shouldReturnUserRequests() throws Exception {
        when(itemRequestClient.getItemRequests(anyLong()))
                .thenReturn(ResponseEntity.ok(Collections.singletonList(itemRequestDto)));

        mvc.perform(get("/requests")
                        .header(ItemRequestController.SHARER_USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())));

        verify(itemRequestClient, times(1)).getItemRequests(1L);
    }

    @Test
    void getItemRequestById_shouldReturnRequestById() throws Exception {
        when(itemRequestClient.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(itemRequestDto));

        mvc.perform(get("/requests/1")
                        .header(ItemRequestController.SHARER_USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));

        verify(itemRequestClient, times(1)).getItemRequestById(1L, 1L);
    }

    @Test
    void getAllItemRequests_shouldReturnAllRequests() throws Exception {
        when(itemRequestClient.getAllItemRequests(anyLong()))
                .thenReturn(ResponseEntity.ok(Collections.singletonList(itemRequestDto)));

        mvc.perform(get("/requests/all")
                        .header(ItemRequestController.SHARER_USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())));

        verify(itemRequestClient, times(1)).getAllItemRequests(1L);
    }
}