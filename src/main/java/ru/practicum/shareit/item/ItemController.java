package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemDbStorageImpl;

import java.text.ParseException;
import java.util.Collection;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    public static final String SHARER_USER_ID = "X-shareit-user-id";
    @Autowired
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public Collection<Item> findAll() {
        Collection<Item> item = itemService.findAll();
        log.info(String.format("findAll  finished - %s",item.toString()));
        return item;
    }

    @GetMapping("/{id}")
    public Optional<Optional<Item>> getItemById(@PathVariable long id) {
        log.info(String.format("getById started - %s - ", id));
        return Optional.ofNullable(itemService.getItemById(id));
    }

    @PostMapping
    Item create(@RequestHeader(SHARER_USER_ID) long userId,@RequestBody ItemCreateDto item) throws ParseException {
        log.info(String.format("create started - %s",String.valueOf(item)));
        item.setUserId(userId);
        Item itemNew = itemService.create(item);
        log.info(String.format("create finished - %s",itemNew.toString()));
        return itemNew;
    }

    @PutMapping
    public Item update(@RequestBody Item user) throws ParseException {
        log.info(String.format("update started - %s",String.valueOf(user)));
        Item itemNew = itemService.update(user);
        log.info(String.format("update finished - %s",String.valueOf(itemNew)));
        return itemNew;
    }
}
