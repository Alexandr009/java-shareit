package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.AutorDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemMapperTest {

    private final ItemMapper itemMapper = new ItemMapper();

    @Test
    void toCommentDto_shouldReturnNullWhenCommentNull() {
        assertNull(itemMapper.toCommentDto(null));
    }

    @Test
    void toCommentDto_shouldMapCommentToDto() {
        User author = new User();
        author.setId(1);
        author.setName("John Doe");
        author.setEmail("john@example.com");

        Item item = new Item();
        item.setId(1);
        item.setName("Drill");

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("Great item!");
        Date createdDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        comment.setCreated(createdDate);
        comment.setAuthor(author);
        comment.setItem(item);

        CommentInfoDto result = itemMapper.toCommentDto(comment);

        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
        assertEquals(comment.getCreated(), result.getCreated());
        assertEquals("John Doe", result.getAuthorName());

        AutorDto authorDto = result.getAuthor();
        assertNotNull(authorDto);
        assertEquals(author.getId(), authorDto.getId());
        assertEquals(author.getName(), authorDto.getAuthorName());
        assertEquals(author.getEmail(), authorDto.getEmail());
    }

    @Test
    void toCommentDto_shouldHandleNullAuthor() {

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("Great item!");
        Date createdDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        comment.setCreated(createdDate);
        comment.setAuthor(null);

        CommentInfoDto result = itemMapper.toCommentDto(comment);

        assertNotNull(result);
        assertNull(result.getAuthorName());
    }

    @Test
    void toItemDto_shouldReturnNullWhenItemNull() {
        assertNull(itemMapper.toItemDto(null, null, null, null, 1L));
    }

    @Test
    void toItemDto_shouldMapItemWithoutBookings() {
        Item item = new Item();
        item.setId(1);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);

        User owner = new User();
        owner.setId(1);
        item.setOwner(owner);

        ItemDto result = itemMapper.toItemDto(item, List.of(), null, null, 1L);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(owner, result.getUser());
        assertTrue(result.getComments().isEmpty());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    void toItemDto_shouldHandleNullComments() {
        Item item = new Item();
        item.setId(1);

        ItemDto result = itemMapper.toItemDto(item, null, null, null, 1L);

        assertNotNull(result);
        assertNotNull(result.getComments());
        assertTrue(result.getComments().isEmpty());
    }
}