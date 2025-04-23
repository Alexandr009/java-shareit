package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    public static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    @Autowired
    private ItemRequestService itemRequestService;

    public ItemRequestController (ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequest getItemRequest(@RequestBody ItemRequestDto itemRequestDto, @RequestHeader(SHARER_USER_ID_HEADER) long userId) {
        log.info("create ItemRequest - %s, userId - %s", itemRequestDto, userId);
        itemRequestDto.setRequestorId((int) userId);
        ItemRequest ItemNew = itemRequestService.createItemRequest(itemRequestDto);
        log.info("created ItemRequest - %s", ItemNew);
        return ItemNew;
    }
}
