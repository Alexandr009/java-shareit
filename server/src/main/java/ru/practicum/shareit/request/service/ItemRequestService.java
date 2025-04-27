package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.itemRequestInfoDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;

    public ItemRequestService(ItemRequestRepository itemRequestRepository, UserRepository userRepository, ItemRepository itemRepository, ItemRequestMapper itemRequestMapper) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.itemRequestMapper = itemRequestMapper;
    }
    public ItemRequest createItemRequest(ItemRequestDto itemRequestDto) {
        Optional<User> user = userRepository.findById(Long.valueOf(itemRequestDto.getRequestorId()));
        if (user.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", itemRequestDto.getRequestorId()));
        }
        Date createdDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(createdDate);
        itemRequest.setRequestor(user.get());
        return itemRequestRepository.save(itemRequest);
    }

    public Collection<ItemRequest> getItemRequests(long userId) {
        Optional<User> user = userRepository.findById(Long.valueOf(userId));
        if (user.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", userId));
        }
        return itemRequestRepository.getItemRequestsByRequestor_Id((int) userId);
    }

    public itemRequestInfoDto getItemRequestId(long requestId, long userId) {
        Optional<User> user = userRepository.findById(Long.valueOf(userId));
        if (user.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", userId));
        }
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("itemRequest with id = %s not found", requestId)));

        Collection<Item> items = itemRepository.findAllByItemRequest_Id(itemRequest.getId());
        itemRequestInfoDto itemRequestInfoDto = itemRequestMapper.toDto(itemRequest,items);
        return itemRequestInfoDto;
    }

    public Collection<ItemRequest> getCollectionItemRequest(){
        return itemRequestRepository.findAllItemRequestsOrderByDateDesc();
    }
}