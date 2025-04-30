package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    public static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private static final String REQUEST_ID_PATH = "/{id}";
    private static final String ALL_REQUESTS_PATH = "/all";
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader(SHARER_USER_ID_HEADER) long userId) {
        log.info("Creating item request: {}, userId: {}", itemRequestDto, userId);
        ResponseEntity<Object> response = itemRequestClient.createItemRequest(itemRequestDto, userId);
        log.info("Created item request: {}", response.getBody());
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequests(
            @RequestHeader(SHARER_USER_ID_HEADER) long userId) {
        log.info("Getting item requests for user: {}", userId);
        ResponseEntity<Object> response = itemRequestClient.getItemRequests(userId);
        log.info("Retrieved {} item requests", ((Collection<?>) response.getBody()).size());
        return response;
    }

    @GetMapping(REQUEST_ID_PATH)
    public ResponseEntity<Object> getItemRequestById(
            @PathVariable long id,
            @RequestHeader(SHARER_USER_ID_HEADER) long userId) {
        log.info("Getting item request by id: {}, userId: {}", id, userId);
        ResponseEntity<Object> response = itemRequestClient.getItemRequestById(id, userId);
        log.info("Retrieved item request: {}", response.getBody());
        return response;
    }

    @GetMapping(ALL_REQUESTS_PATH)
    public ResponseEntity<Object> getAllItemRequests(
            @RequestHeader(SHARER_USER_ID_HEADER) long userId) {
        log.info("Getting all item requests for user: {}", userId);
        ResponseEntity<Object> response = itemRequestClient.getAllItemRequests(userId);
        log.info("Retrieved {} all item requests", ((Collection<?>) response.getBody()).size());
        return response;
    }
}