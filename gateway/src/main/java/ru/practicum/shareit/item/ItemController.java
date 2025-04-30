package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.Create;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;

import java.text.ParseException;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    public static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private static final String ITEM_ID_PATH = "/{id}";
    private static final String COMMENT_PATH = "/{itemId}/comment";
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(SHARER_USER_ID_HEADER) long userId) {
        log.info("Request to get all items for user {}", userId);
        return itemClient.findAll(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(
            @RequestHeader(SHARER_USER_ID_HEADER) long userId,
            @RequestParam(value = "text") String searchText) {
        log.info("Searching for {} by user {}", searchText, userId);
        return itemClient.searchItem(userId, searchText);
    }

    @GetMapping(ITEM_ID_PATH)
    public ResponseEntity<Object> getItemById(
            @PathVariable long id,
            @RequestHeader(SHARER_USER_ID_HEADER) long userId) {
        log.info("Request to get item {} by user {}", id, userId);
        return itemClient.getItemById(id, userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(SHARER_USER_ID_HEADER) long userId,
            @Validated({Create.class}) @RequestBody ItemCreateDto item) throws ParseException {
        log.info("Request to create item {} by user {}", item, userId);
        return itemClient.create(userId, item);
    }

    @PatchMapping(ITEM_ID_PATH)
    public ResponseEntity<Object> updatePatch(
            @RequestHeader(SHARER_USER_ID_HEADER) long userId,
            @PathVariable long id,
            @RequestBody ItemPatchDto item) throws ParseException {
        log.info("Request to patch item {} by user {} with data {}", id, userId, item);
        return itemClient.updatePatch(userId, id, item);
    }

    @PutMapping
    public ResponseEntity<Object> update(
            @RequestBody ItemCreateDto item) throws ParseException {
        log.info("Request to update item {}", item);
        return itemClient.update(item);
    }

    @PostMapping(COMMENT_PATH)
    public ResponseEntity<Object> createComment(
            @RequestHeader(SHARER_USER_ID_HEADER) long userId,
            @PathVariable long itemId,
            @RequestBody CommentCreateDto comment) throws ParseException {
        log.info("Request to create comment for item {} by user {}", itemId, userId);
        return itemClient.createComment(userId, itemId, comment);
    }
}