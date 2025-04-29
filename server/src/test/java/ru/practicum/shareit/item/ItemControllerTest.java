package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController controller;
    private MockMvc mvc;
    private Item item;
    private ItemDto itemDto;
    private ItemCreateDto itemCreateDto;
    private ItemPatchDto itemPatchDto;
    private CommentInfoDto commentInfoDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        item = new Item();
        item.setId(1);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);

        itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill");
        itemDto.setAvailable(true);

        itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Drill");
        itemCreateDto.setDescription("Powerful drill");
        itemCreateDto.setAvailable(true);

        itemPatchDto = new ItemPatchDto();
        itemPatchDto.setName("Updated Drill");
        itemPatchDto.setDescription("More powerful drill");

        commentInfoDto = new CommentInfoDto();
        commentInfoDto.setId(1);
        commentInfoDto.setText("Great drill!");
    }

    @Test
    void findAll_shouldReturnUserItems() throws Exception {
        when(itemService.findAllByUserId(anyLong()))
                .thenReturn(Collections.singletonList(item));

        mvc.perform(get("/items")
                        .header(ItemController.SHARER_USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(item.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(item.getName())));

        verify(itemService, times(1)).findAllByUserId(1L);
    }

    @Test
    void findAll_shouldReturnAllItemsWhenUserIdZero() throws Exception {
        when(itemService.findAll())
                .thenReturn(Collections.singletonList(item));

        mvc.perform(get("/items")
                        .header(ItemController.SHARER_USER_ID_HEADER, 0L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(itemService, times(1)).findAll();
    }

    @Test
    void searchItem_shouldReturnFoundItems() throws Exception {
        when(itemService.findAllByText(anyLong(), anyString()))
                .thenReturn(Collections.singletonList(item));

        mvc.perform(get("/items/search")
                        .header(ItemController.SHARER_USER_ID_HEADER, 1L)
                        .param("text", "drill")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(item.getId().intValue())));

        verify(itemService, times(1)).findAllByText(1L, "drill");
    }

    @Test
    void searchItem_shouldReturnEmptyListWhenTextEmpty() throws Exception {
        mvc.perform(get("/items/search")
                        .header(ItemController.SHARER_USER_ID_HEADER, 1L)
                        .param("text", "")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));

        verify(itemService, never()).findAllByText(anyLong(), anyString());
    }

    @Test
    void getItemById_shouldReturnItem() throws Exception {
        when(itemService.findItemByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(get("/items/1")
                        .header(ItemController.SHARER_USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(itemDto.getName())));

        verify(itemService, times(1)).findItemByIdAndUserId(1L, 1L);
    }

    @Test
    void create_shouldCreateItem() throws Exception {
        when(itemService.create(any()))
                .thenReturn(item);

        mvc.perform(post("/items")
                        .header(ItemController.SHARER_USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(itemCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId().intValue())))
                .andExpect(jsonPath("$.name", is(item.getName())));

        verify(itemService, times(1)).create(any());
    }

    @Test
    void updatePatch_shouldUpdateItem() throws Exception {
        when(itemService.updatePatch(any()))
                .thenReturn(item);

        mvc.perform(patch("/items/1")
                        .header(ItemController.SHARER_USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(itemPatchDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId().intValue())));

        verify(itemService, times(1)).updatePatch(any());
    }

    @Test
    void update_shouldUpdateItem() throws Exception {
        when(itemService.update(any()))
                .thenReturn(item);

        mvc.perform(put("/items")
                        .content(mapper.writeValueAsString(itemCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId().intValue())));

        verify(itemService, times(1)).update(any());
    }

    @Test
    void createComment_shouldCreateComment() throws Exception {
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("Great drill!");

        when(itemService.createComment(any(), anyLong(), anyLong()))
                .thenReturn(commentInfoDto);

        mvc.perform(post("/items/1/comment")
                        .header(ItemController.SHARER_USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(commentCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentInfoDto.getId().intValue())))
                .andExpect(jsonPath("$.text", is(commentInfoDto.getText())));

        verify(itemService, times(1)).createComment(any(), eq(1L), eq(1L));
    }
}