package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.itemRequestInfoDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    private final ItemRequestMapper mapper = new ItemRequestMapper();

    @Test
    void toDto_shouldMapItemRequestToDtoWithItems() {

        User requestor = new User();
        requestor.setId(1);
        requestor.setName("John Doe");
        requestor.setEmail("john@example.com");

        Date createdDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("Need a drill");
        itemRequest.setCreated(createdDate);
        itemRequest.setRequestor(requestor);

        Item item = new Item();
        item.setId(1);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(requestor);
        item.setItemRequest(itemRequest);

        List<Item> items = List.of(item);

        itemRequestInfoDto dto = mapper.toDto(itemRequest, items);

        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Need a drill", dto.getDescription());
        assertEquals(createdDate, dto.getCreated());
        assertEquals(requestor, dto.getRequestor());
        assertEquals(1, dto.getItems().size());
        assertEquals(item, dto.getItems().iterator().next());
    }

    @Test
    void toDto_shouldMapItemRequestToDtoWithEmptyItems() {

        User requestor = new User();
        requestor.setId(1);
        requestor.setName("John Doe");
        requestor.setEmail("john@example.com");

        Date createdDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("Need a drill");
        itemRequest.setCreated(createdDate);
        itemRequest.setRequestor(requestor);

        itemRequestInfoDto dto = mapper.toDto(itemRequest, Collections.emptyList());

        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Need a drill", dto.getDescription());
        assertEquals(createdDate, dto.getCreated());
        assertEquals(requestor, dto.getRequestor());
        assertTrue(dto.getItems().isEmpty());
    }

    @Test
    void toDto_shouldHandleNullItems() {
        User requestor = new User();
        requestor.setId(1);
        requestor.setName("John Doe");
        requestor.setEmail("john@example.com");

        Date createdDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("Need a drill");
        itemRequest.setCreated(createdDate);
        itemRequest.setRequestor(requestor);

        itemRequestInfoDto dto = mapper.toDto(itemRequest, null);

        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Need a drill", dto.getDescription());
        assertEquals(createdDate, dto.getCreated());
        assertEquals(requestor, dto.getRequestor());
        assertNull(dto.getItems());
    }
}