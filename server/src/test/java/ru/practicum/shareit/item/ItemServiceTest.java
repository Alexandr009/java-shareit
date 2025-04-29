package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookinRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.text.ParseException;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookinRepository bookinRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemService itemService;

    private User user;
    private Item item;
    private ItemCreateDto itemCreateDto;
    private ItemPatchDto itemPatchDto;
    private Comment comment;
    private CommentCreateDto commentCreateDto;
    private Booking booking;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        item = new Item();
        item.setId(1);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(user);

        itemCreateDto = new ItemCreateDto();
        itemCreateDto.setId(1);
        itemCreateDto.setName("Drill");
        itemCreateDto.setDescription("Powerful drill");
        itemCreateDto.setAvailable(true);
        itemCreateDto.setUserId(1L);

        itemPatchDto = new ItemPatchDto();
        itemPatchDto.setId(1);
        itemPatchDto.setName("Updated Drill");
        itemPatchDto.setDescription("More powerful drill");
        itemPatchDto.setUserId(1L);

        comment = new Comment();
        comment.setId(1);
        comment.setText("Great item!");
        comment.setItem(item);
        comment.setAuthor(user);

        commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("Great item!");

        booking = new Booking();
        booking.setId(1);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.APPROVED);
        booking.setStart(new Date(System.currentTimeMillis() - 10000));
        booking.setEnd(new Date(System.currentTimeMillis() + 10000));

        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("Need a drill");
    }

    @Test
    void findAll_shouldReturnAllItems() {
        when(itemRepository.findAll()).thenReturn(List.of(item));

        Collection<Item> result = itemService.findAll();

        assertThat(result, hasSize(1));
        assertThat(result.iterator().next(), equalTo(item));
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void findAllByUserId_shouldReturnUserItems() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findByOwner(user)).thenReturn(List.of(item));

        Collection<Item> result = itemService.findAllByUserId(1L);

        assertThat(result, hasSize(1));
        assertThat(result.iterator().next(), equalTo(item));
        verify(userRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).findByOwner(user);
    }

    @Test
    void findAllByUserId_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.findAllByUserId(999L));
        verify(userRepository, times(1)).findById(999L);
        verify(itemRepository, never()).findByOwner(any());
    }

    @Test
    void findAllByText_shouldReturnMatchingItems() {
        when(itemRepository.searchByText(1L, "drill")).thenReturn(List.of(item));

        Collection<Item> result = itemService.findAllByText(1L, "drill");

        assertThat(result, hasSize(1));
        assertThat(result.iterator().next(), equalTo(item));
        verify(itemRepository, times(1)).searchByText(1L, "drill");
    }

    @Test
    void create_shouldCreateNewItem() throws ParseException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenReturn(item);

        Item result = itemService.create(itemCreateDto);

        assertThat(result, equalTo(item));
        verify(userRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void create_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        itemCreateDto.setUserId(999L);
        assertThrows(NotFoundException.class, () -> itemService.create(itemCreateDto));
        verify(userRepository, times(1)).findById(999L);
        verify(itemRepository, never()).save(any());
    }

    @Test
    void updatePatch_shouldPatchExistingItem() throws ParseException {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        Item result = itemService.updatePatch(itemPatchDto);

        assertThat(result, equalTo(item));
        verify(itemRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void createComment_shouldCreateNewComment() {
        Booking completedBooking = new Booking();
        completedBooking.setId(1);
        completedBooking.setItem(item);
        completedBooking.setBooker(user);
        completedBooking.setStatus(Status.APPROVED);
        completedBooking.setStart(new Date(System.currentTimeMillis() - 20000));
        completedBooking.setEnd(new Date(System.currentTimeMillis() - 10000));

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookinRepository.findByBookerIdAndItemId(1L, 1L)).thenReturn(List.of(completedBooking));
        when(commentRepository.save(any())).thenReturn(comment);
        when(itemMapper.toCommentDto(any())).thenReturn(new CommentInfoDto());

        CommentInfoDto result = itemService.createComment(commentCreateDto, 1L, 1L);

        assertNotNull(result);
        verify(itemRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(bookinRepository, times(1)).findByBookerIdAndItemId(1L, 1L);
        verify(commentRepository, times(1)).save(any());
        verify(itemMapper, times(1)).toCommentDto(any());
    }

    @Test
    void createComment_shouldThrowWhenNoCompletedBookings() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookinRepository.findByBookerIdAndItemId(1L, 1L)).thenReturn(Collections.emptyList());

        assertThrows(ValidationException.class,
                () -> itemService.createComment(commentCreateDto, 1L, 1L));
    }

    @Test
    void findItemByIdAndUserId_shouldReturnItemWithBookingsAndComments() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItem_Id(1L)).thenReturn(List.of(comment));

        when(bookinRepository.findByItem(item)).thenReturn(List.of(booking));

        ItemDto expectedDto = new ItemDto();
        expectedDto.setId(1);
        expectedDto.setName("Drill");
        expectedDto.setDescription("Powerful drill");
        expectedDto.setAvailable(true);

        when(itemMapper.toItemDto(
                eq(item),
                anyList(),
                any(),
                any(),
                eq(1L)
        )).thenReturn(expectedDto);

        ItemDto result = itemService.findItemByIdAndUserId(1L, 1L);

        assertNotNull(result);
        assertEquals(expectedDto, result);

        verify(userRepository).findById(1L);
        verify(itemRepository).findById(1L);
        verify(commentRepository).findAllByItem_Id(1L);

        verify(bookinRepository, times(2)).findByItem(item);

        verify(itemMapper).toItemDto(
                eq(item),
                anyList(),
                any(),
                any(),
                eq(1L)
        );
    }

    @Test
    void findItemByIdAndUserId_shouldThrowWhenItemNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> itemService.findItemByIdAndUserId(1L, 1L));
    }

    @Test
    void getItemById_shouldReturnItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Optional<Item> result = itemService.getItemById(1L);

        assertTrue(result.isPresent());
        assertEquals(item, result.get());
    }

    @Test
    void update_shouldUpdateExistingItem() throws ParseException {
        ItemCreateDto updateDto = new ItemCreateDto();
        updateDto.setId(1);
        updateDto.setName("Updated Drill");
        updateDto.setDescription("More powerful");
        updateDto.setAvailable(false);
        updateDto.setUserId(1L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        Item result = itemService.update(updateDto);

        assertNotNull(result);
        verify(itemRepository).findById(1L);
        verify(itemRepository).save(any());
    }

    @Test
    void update_shouldThrowWhenUserNotOwner() throws ParseException {
        ItemCreateDto updateDto = new ItemCreateDto();
        updateDto.setId(1);
        updateDto.setUserId(999L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class,
                () -> itemService.update(updateDto));
    }

    @Test
    void create_shouldCreateItemWithRequest() throws ParseException {
        itemCreateDto.setRequestId(1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.getItemRequestsById(1)).thenReturn(itemRequest);
        when(itemRepository.save(any())).thenReturn(item);

        Item result = itemService.create(itemCreateDto);

        assertNotNull(result);
        verify(itemRequestRepository).getItemRequestsById(1);
    }

    @Test
    void create_shouldThrowWhenNameEmpty() {
        itemCreateDto.setName("");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(ValidationException.class,
                () -> itemService.create(itemCreateDto));
    }

    @Test
    void updatePatch_shouldUpdateOnlyName() throws ParseException {
        itemPatchDto.setDescription(null);
        itemPatchDto.setAvailable(null);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        Item result = itemService.updatePatch(itemPatchDto);

        assertNotNull(result);
        assertEquals("Updated Drill", result.getName());
        assertEquals("Powerful drill", result.getDescription());
    }

    @Test
    void createComment_shouldThrowWhenBookingNotEnded() {
        Booking ongoingBooking = new Booking();
        ongoingBooking.setId(2);
        ongoingBooking.setItem(item);
        ongoingBooking.setBooker(user);
        ongoingBooking.setStatus(Status.APPROVED);
        ongoingBooking.setStart(new Date(System.currentTimeMillis() - 10000));
        ongoingBooking.setEnd(new Date(System.currentTimeMillis() + 10000));

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookinRepository.findByBookerIdAndItemId(1L, 1L)).thenReturn(List.of(ongoingBooking));

        assertThrows(ValidationException.class,
                () -> itemService.createComment(commentCreateDto, 1L, 1L));
    }

    @Test
    void findAllByText_shouldTrimText() {
        when(itemRepository.searchByText(1L, "drill")).thenReturn(List.of(item));

        Collection<Item> result = itemService.findAllByText(1L, "drill");

        assertEquals(1, result.size());
        verify(itemRepository).searchByText(1L, "drill");
    }

    @Test
    void updatePatch_shouldThrowWhenItemNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.updatePatch(itemPatchDto));
    }

    @Test
    void createComment_shouldThrowWhenItemNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.createComment(commentCreateDto, 1L, 1L));
    }

    @Test
    void findAllByText_shouldHandleSpecialCharacters() {
        when(itemRepository.searchByText(1L, "drill#")).thenReturn(List.of(item));

        Collection<Item> result = itemService.findAllByText(1L, "drill#");
        assertEquals(1, result.size());
    }

}
