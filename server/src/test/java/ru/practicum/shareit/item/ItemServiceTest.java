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
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

        // when & then
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
}
