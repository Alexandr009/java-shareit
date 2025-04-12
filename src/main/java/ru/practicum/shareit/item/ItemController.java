package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    public static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    @Autowired
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public Collection<Item> findAll(@RequestHeader(SHARER_USER_ID_HEADER) long userId) {
        if (userId != 0) {
            return itemService.findAllByUserId(userId);
        } else {
            return itemService.findAll();
        }
    }

    @GetMapping("/search")
    public Collection<Item> searchItem(@RequestHeader(SHARER_USER_ID_HEADER) long userId, @RequestParam(value = "text") String searchText) {
        log.info("Searching for " + searchText + " in " + userId);
        if (searchText.isEmpty() || searchText.equals("")) {
            return Collections.emptyList();
        }
        return itemService.findAllByText(userId, searchText);
    }

    @GetMapping("/{id}")
    public Optional<ItemDto> getItemById(@PathVariable long id, @RequestHeader(SHARER_USER_ID_HEADER) long userId) {
        log.info(String.format("getById started - %s - ", id));
        return Optional.ofNullable(itemService.findItemByIdAndUserId(id, userId));

    }

    @PostMapping
    Item create(@RequestHeader(SHARER_USER_ID_HEADER) long userId, @Validated({Create.class}) @RequestBody ItemCreateDto item) throws ParseException {
        log.info(String.format("create started - %s", String.valueOf(item)));
        item.setUserId(userId);
        Item itemNew = itemService.create(item);
        log.info(String.format("create finished - %s", itemNew.toString()));
        return itemNew;
    }

    @PatchMapping("/{id}")
    Item updatePatch(@RequestHeader(SHARER_USER_ID_HEADER) long userId, @RequestBody ItemPatchDto item, @PathVariable long id) throws ParseException {
        log.info(String.format("create started - %s", String.valueOf(item)));
        item.setUserId(userId);
        item.setId((int) id);
        Item itemNew = itemService.updatePatch(item);
        log.info(String.format("create finished - %s", itemNew.toString()));
        return itemNew;
    }

    @PutMapping
    public Item update(@RequestBody ItemCreateDto item) throws ParseException {
        log.info(String.format("update started - %s", String.valueOf(item)));
        Item itemNew = itemService.update(item);
        log.info(String.format("update finished - %s", String.valueOf(itemNew)));
        return itemNew;
    }

    @PostMapping("/{itemId}/comment")
    CommentInfoDto createComment(@RequestHeader(SHARER_USER_ID_HEADER) long userId, @PathVariable long itemId, @RequestBody CommentCreateDto comment) throws ParseException {
        log.info(String.format("create started - %s", String.valueOf(comment)));
        CommentInfoDto newComment = new CommentInfoDto();
        newComment = itemService.createComment(comment, itemId, userId);
        return newComment;
    }
}
