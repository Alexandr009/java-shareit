package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.itemRequestInfoDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @InjectMocks
    private ItemRequestService itemRequestService;

    private User user;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;
    private itemRequestInfoDto itemRequestInfoDto;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setName("John Doe");
        user.setEmail("john.doe@mail.com");

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Need a drill");
        itemRequestDto.setRequestorId(1);

        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("Need a drill");
        itemRequest.setCreated(new Date());
        itemRequest.setRequestor(user);

        item = new Item();
        item.setId(1);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(user);
        item.setItemRequest(itemRequest);

        itemRequestInfoDto = new itemRequestInfoDto();
        itemRequestInfoDto.setId(1);
        itemRequestInfoDto.setDescription("Need a drill");
        itemRequestInfoDto.setCreated(itemRequest.getCreated());
    }

    @Test
    void createItemRequest_shouldCreateRequestWhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequest result = itemRequestService.createItemRequest(itemRequestDto);

        assertThat(result, equalTo(itemRequest));
        verify(userRepository, times(1)).findById(1L);
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    void createItemRequest_shouldThrowWhenUserNotExists() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        itemRequestDto.setRequestorId(999);
        assertThrows(NotFoundException.class, () -> itemRequestService.createItemRequest(itemRequestDto));
        verify(userRepository, times(1)).findById(999L);
        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void getItemRequests_shouldReturnUserRequests() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.getItemRequestsByRequestor_Id(1)).thenReturn(List.of(itemRequest));

        Collection<ItemRequest> result = itemRequestService.getItemRequests(1L);

        assertThat(result, hasSize(1));
        assertThat(result.iterator().next(), equalTo(itemRequest));
        verify(userRepository, times(1)).findById(1L);
        verify(itemRequestRepository, times(1)).getItemRequestsByRequestor_Id(1);
    }

    @Test
    void getItemRequests_shouldThrowWhenUserNotExists() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequests(999L));
        verify(userRepository, times(1)).findById(999L);
        verify(itemRequestRepository, never()).getItemRequestsByRequestor_Id(anyInt());
    }

    @Test
    void getItemRequestId_shouldReturnRequestWithItems() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByItemRequest_Id(1)).thenReturn(List.of(item));
        when(itemRequestMapper.toDto(itemRequest, List.of(item))).thenReturn(itemRequestInfoDto);

        itemRequestInfoDto result = itemRequestService.getItemRequestId(1L, 1L);

        assertThat(result, equalTo(itemRequestInfoDto));
        verify(userRepository, times(1)).findById(1L);
        verify(itemRequestRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).findAllByItemRequest_Id(1);
        verify(itemRequestMapper, times(1)).toDto(itemRequest, List.of(item));
    }

    @Test
    void getItemRequestId_shouldThrowWhenUserNotExists() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestId(1L, 999L));
        verify(userRepository, times(1)).findById(999L);
        verify(itemRequestRepository, never()).findById(anyLong());
    }

    @Test
    void getItemRequestId_shouldThrowWhenRequestNotExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestId(999L, 1L));
        verify(userRepository, times(1)).findById(1L);
        verify(itemRequestRepository, times(1)).findById(999L);
        verify(itemRepository, never()).findAllByItemRequest_Id(any());
    }

    @Test
    void getCollectionItemRequest_shouldReturnAllRequestsOrderedByDate() {
        when(itemRequestRepository.findAllItemRequestsOrderByDateDesc()).thenReturn(List.of(itemRequest));

        Collection<ItemRequest> result = itemRequestService.getCollectionItemRequest();

        assertThat(result, hasSize(1));
        assertThat(result.iterator().next(), equalTo(itemRequest));
        verify(itemRequestRepository, times(1)).findAllItemRequestsOrderByDateDesc();
    }
}
