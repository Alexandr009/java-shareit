package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    public static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    @Autowired
    private ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequest createItemRequest(@RequestBody ItemRequestDto itemRequestDto, @RequestHeader(SHARER_USER_ID_HEADER) long userId) {
        log.info("create ItemRequest - %s, userId - %s", itemRequestDto, userId);
        itemRequestDto.setRequestorId((int) userId);
        ItemRequest ItemNew = itemRequestService.createItemRequest(itemRequestDto);
        log.info("created ItemRequest - %s", ItemNew);
        return ItemNew;
    }

    @GetMapping
    public Collection<ItemRequest> getItemRequest(@RequestHeader(SHARER_USER_ID_HEADER) long userId) {
        log.info("getItemRequest - %s", userId);
        Collection<ItemRequest> itemRequestsList = itemRequestService.getItemRequests(userId);
        log.info("getItemRequest - %s", itemRequestsList);
        return itemRequestsList;
    }

    @GetMapping("/{id}")
    public ItemRequestInfoDto getItemRequestId(@PathVariable long id, @RequestHeader(SHARER_USER_ID_HEADER) long userId) {
        log.info("getItemRequest - %s", id);
        ItemRequestInfoDto itemRequest = itemRequestService.getItemRequestId(id, userId);
        log.info("getItemRequest - %s", itemRequest);
        return itemRequest;
    }

    @GetMapping("/all")
    public Collection<ItemRequest> getAll(@RequestHeader(SHARER_USER_ID_HEADER) long userId) {
        log.info("getAllItemRequest - %s", userId);
        Collection<ItemRequest> itemRequestsList = itemRequestService.getCollectionItemRequest();
        return itemRequestsList;
    }
}