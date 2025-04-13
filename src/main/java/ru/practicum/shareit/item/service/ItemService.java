package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookinRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.text.ParseException;
import java.util.*;

@Slf4j
@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookinRepository bookinRepository;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemService(ItemRepository itemRepository, UserRepository userRepository, CommentRepository commentRepository, BookinRepository bookinRepository, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookinRepository = bookinRepository;
        this.itemMapper = itemMapper;
    }

    public Collection<Item> findAll() {
        return itemRepository.findAll();
    }

    public Collection<Item> findAllByUserId(long userId) {
        Optional<User> user = userRepository.findById(userId);
        return itemRepository.findByOwner(user.orElse(null));
    }

    public ItemDto findItemByIdAndUserId(long id, long userId) {

        Optional<User> user = userRepository.findById(userId);
        Optional<Item> items = itemRepository.findById(id);
        Item item = items.get();
        List<Comment> comments = commentRepository.findAllByItem_Id(id);
        List<CommentCreateDto> commentDtos = comments.stream()
                .map(comment -> new CommentCreateDto(
                        comment.getText()
                ))
                .toList();
        Date currentDate = new Date();

        Booking lastBooking = bookinRepository.findByItem(item).stream()
                .filter(booking -> booking.getItem().getId().equals(item.getId()))
                .filter(booking -> booking.getStart().before(currentDate))
                .max(Comparator.comparing(Booking::getStart))
                .orElse(null);

        Booking nextBooking = bookinRepository.findByItem(item).stream()
                .filter(booking -> booking.getItem().getId().equals(item.getId()))
                .filter(booking -> booking.getStart().after(currentDate))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);

        ItemDto itemDto = itemMapper.toItemDto(items.get(), commentDtos, lastBooking, nextBooking, id);
        return itemDto;
    }

    public Collection<Item> findAllByText(long userId, String text) {
        return itemRepository.searchByText(userId, text);
    }

    public Optional<Item> getItemById(long id) {
        return itemRepository.findById(id);
    }

    public Item create(ItemCreateDto item) throws ParseException {
        Optional<User> existingUser = userRepository.findById(item.getUserId());
        if (existingUser.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", item.getId()));
        }
        if (item.getName() == null || item.getName().isEmpty()) {
            throw new ValidationException("Name cannot be empty for itemId: %s" + item.getId());
        }

        Item itemNew = new Item();
        itemNew.setName(item.getName());
        itemNew.setOwner(existingUser.get());
        itemNew.setAvailable(item.getAvailable());
        itemNew.setDescription(item.getDescription());
        itemNew = itemRepository.save(itemNew);

        return itemNew;
    }

    public Item update(ItemCreateDto item) throws ParseException {
        Optional<Item> existingItem = itemRepository.findById(Long.valueOf(item.getId()));
        if (existingItem.isEmpty()) {
            throw new NotFoundException(String.format("Item with id: %s not found", item.getId()));
        }
        Item currentItem = existingItem.get();
        if (item.getUserId() != (int) currentItem.getOwner().getId()) {
            throw new NotFoundException(String.format("User id: %s wrong",item.getUserId().intValue()));
        }

        Item itemNew = new Item();
        itemNew.setName(item.getName());
        itemNew.setAvailable(item.getAvailable());
        itemNew.setDescription(item.getDescription());
        itemNew = itemRepository.save(itemNew);

        return itemNew;
    }

    public Item updatePatch(ItemPatchDto item) throws ParseException {
        Item existingItem = itemRepository.findById(Long.valueOf(item.getId()))
                .orElseThrow(() -> new NotFoundException(
                        String.format("Item with id = %d not found", item.getId())));

        if (!item.getUserId().equals(Long.valueOf(existingItem.getOwner().getId()))) {
            throw new NotFoundException(String.format("UserId: %d is not the owner of this itemId: %d",item.getUserId().intValue(),item.getId()));
        }
        if (item.getName() != null && !item.getName().isBlank()) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            existingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }
        return itemRepository.save(existingItem);

    }

    public CommentInfoDto createComment(CommentCreateDto commentDto, long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("ItemId: %s not found",itemId)));

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("UserId: %s not found",userId)));

        Date currentDate = new Date();

        List<Booking> userBookings = bookinRepository.findByBookerIdAndItemId(userId, itemId);
        if (userBookings.isEmpty()) {
            throw new ValidationException(String.format("UserId: %s never booked itemId: %s",userId,itemId));
        }

        boolean canComment = userBookings.stream()
                .anyMatch(booking ->
                        booking.getStatus() == Status.APPROVED &&
                                booking.getEnd().before(currentDate)
                );

        log.info("Can comment: {}, Booking end: {}, Current: {}",
                canComment, userBookings.get(0).getEnd(), currentDate);

        if (!canComment) {
            throw new ValidationException(String.format("BookingId: %s not ended yet",userBookings.getFirst().getId()));
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(currentDate);

        return itemMapper.toCommentDto(commentRepository.save(comment));
    }
}
