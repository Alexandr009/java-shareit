package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.itemRequestInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private ItemRequestService itemRequestService;
    @InjectMocks
    private ItemRequestController controller;
    private MockMvc mvc;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private itemRequestInfoDto itemRequestInfo;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("Need a drill");

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Need a drill");

        itemRequestInfo = new itemRequestInfoDto();
        itemRequestInfo.setId(1);
        itemRequestInfo.setDescription("Need a drill");
    }

    @Test
    void createItemRequest_shouldCreateRequest() throws Exception {
        when(itemRequestService.createItemRequest(any()))
                .thenReturn(itemRequest);

        mvc.perform(post("/requests")
                        .header(ItemRequestController.SHARER_USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId()), int.class))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())));

        verify(itemRequestService, times(1)).createItemRequest(any());
    }

    @Test
    void getItemRequest_shouldReturnUserRequests() throws Exception {
        when(itemRequestService.getItemRequests(anyLong()))
                .thenReturn(Collections.singletonList(itemRequest));

        mvc.perform(get("/requests")
                        .header(ItemRequestController.SHARER_USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequest.getId()), int.class))
                .andExpect(jsonPath("$[0].description", is(itemRequest.getDescription())));

        verify(itemRequestService, times(1)).getItemRequests(1L);
    }

    @Test
    void getItemRequestId_shouldReturnRequestById() throws Exception {
        when(itemRequestService.getItemRequestId(anyLong(), anyLong()))
                .thenReturn(itemRequestInfo);

        mvc.perform(get("/requests/1")
                        .header(ItemRequestController.SHARER_USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestInfo.getId()), int.class))
                .andExpect(jsonPath("$.description", is(itemRequestInfo.getDescription())));

        verify(itemRequestService, times(1)).getItemRequestId(1L, 1L);
    }

    @Test
    void getAll_shouldReturnAllRequests() throws Exception {
        when(itemRequestService.getCollectionItemRequest()).thenReturn(Collections.singletonList(itemRequest));

        mvc.perform(get("/requests/all")
                        .header(ItemRequestController.SHARER_USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequest.getId()), int.class))
                .andExpect(jsonPath("$[0].description", is(itemRequest.getDescription())));

        verify(itemRequestService, times(1)).getCollectionItemRequest();
    }
}
