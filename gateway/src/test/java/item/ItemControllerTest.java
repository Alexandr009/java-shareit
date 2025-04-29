package item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private ItemClient itemClient;
    @InjectMocks
    private ItemController controller;
    private MockMvc mvc;
    private ItemCreateDto itemCreateDto;
    private ItemPatchDto itemPatchDto;
    private Item item;
    private CommentCreateDto commentCreateDto;
    private User user;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Drill");
        itemCreateDto.setDescription("Powerful drill");
        itemCreateDto.setAvailable(true);

        itemPatchDto = new ItemPatchDto();
        itemPatchDto.setId(1);
        itemPatchDto.setName("Updated Drill");
        itemPatchDto.setDescription("More powerful drill");

        commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("Great drill!");

        item = new Item();
        item.setId(1);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);

        user = new User();
        user.setId(1);
        user.setName("John Doe");
        user.setEmail("john.doe@mail.com");
    }

    @Test
    void findAll_shouldCallClient() throws Exception {
        when(itemClient.findAll(anyLong()))
                .thenReturn(new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK));

        mvc.perform(get("/items")
                        .header(ItemController.SHARER_USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).findAll(1L);
    }

    @Test
    void searchItem_shouldCallClient() throws Exception {
        when(itemClient.searchItem(anyLong(), anyString()))
                .thenReturn(new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK));

        mvc.perform(get("/items/search")
                        .header(ItemController.SHARER_USER_ID_HEADER, 1L)
                        .param("text", "drill")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).searchItem(1L, "drill");
    }

    @Test
    void searchItem_shouldReturnEmptyWhenTextEmpty() throws Exception {
        mvc.perform(get("/items/search")
                        .header(ItemController.SHARER_USER_ID_HEADER, 1L)
                        .param("text", "")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getItemById_shouldCallClient() throws Exception {
        when(itemClient.getItemById(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<>(item, HttpStatus.OK));

        mvc.perform(get("/items/1")
                        .header(ItemController.SHARER_USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Drill"));

        verify(itemClient, times(1)).getItemById(1L, 1L);
    }

    @Test
    void create_shouldCallClient() throws Exception {
        Item mockItem = new Item();
        mockItem.setId(1);
        mockItem.setName("Drill");

        when(itemClient.create(anyLong(), any(ItemCreateDto.class)))
                .thenReturn(new ResponseEntity<>(mockItem, HttpStatus.OK));

        mvc.perform(post("/items")
                        .header(ItemController.SHARER_USER_ID_HEADER, 1)
                        .content(mapper.writeValueAsString(itemCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Drill"));

        verify(itemClient, times(1)).create(eq(1L), any(ItemCreateDto.class));
    }

    @Test
    void updatePatch_shouldCallClient() throws Exception {
        Item mockItem = new Item();
        mockItem.setId(1);
        mockItem.setName("Updated Item");

        when(itemClient.updatePatch(eq(1L), eq(1L), any(ItemPatchDto.class)))
                .thenReturn(new ResponseEntity<>(mockItem, HttpStatus.OK));

        mvc.perform(patch("/items/1")
                        .header(ItemController.SHARER_USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(itemPatchDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Item"));

        verify(itemClient, times(1)).updatePatch(eq(1L), eq(1L), any(ItemPatchDto.class));
    }

    @Test
    void update_shouldCallClient() throws Exception {
        Item mockItem = new Item();
        mockItem.setId(1);
        mockItem.setName("Updated Drill");

        when(itemClient.update(any(ItemCreateDto.class)))
                .thenReturn(new ResponseEntity<>(mockItem, HttpStatus.OK));

        mvc.perform(put("/items")
                        .content(mapper.writeValueAsString(itemCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Drill"));

        verify(itemClient, times(1)).update(any(ItemCreateDto.class));
    }

    @Test
    void createComment_shouldCallClient() throws Exception {
        Comment mockComment = new Comment();
        mockComment.setId(1);
        mockComment.setText("Great drill!");

        when(itemClient.createComment(anyLong(), anyLong(), any(CommentCreateDto.class)))
                .thenReturn(new ResponseEntity<>(mockComment, HttpStatus.OK));

        mvc.perform(post("/items/1/comment")
                        .header(ItemController.SHARER_USER_ID_HEADER, 1)
                        .content(mapper.writeValueAsString(commentCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Great drill!"));

        verify(itemClient, times(1)).createComment(eq(1L), eq(1L), any(CommentCreateDto.class));
    }
}