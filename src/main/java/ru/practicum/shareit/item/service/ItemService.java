package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookinRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemDbStorageImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.storage.UserDbStorageImpl;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemService {
    private final ItemDbStorageImpl itemDbStorage;
    private final UserDbStorageImpl userDbStorage;
    private int nextID;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookinRepository bookinRepository;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemService(ItemDbStorageImpl itemDbStorage, UserDbStorageImpl userDbStorage, ItemRepository itemRepository, UserRepository userRepository, CommentRepository commentRepository,BookinRepository bookinRepository,ItemMapper itemMapper) {
        this.itemDbStorage = itemDbStorage;
        this.userDbStorage = userDbStorage;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookinRepository = bookinRepository;
        this.itemMapper = itemMapper;
    }

    public Collection<Item> findAll() {
        //return itemDbStorage.getAll();
        return itemRepository.findAll();
    }

    public Collection<Item> findAllByUserId(long userId) {
        //return itemDbStorage.getAllByUserId(userId);
        Optional<User> user = userRepository.findById(userId);
        return itemRepository.findByOwner(user.orElse(null));
    }

    public ItemDto findItemByIdAndUserId(long id, long userId) {

        Optional<User> user = userRepository.findById(userId);
        Optional<Item> items =itemRepository.findById(id);
        Item item = items.get();
        List<Comment> comments = commentRepository.findAllByItem_Id(id);
        List<CommentCreateDto> commentDtos = comments.stream()
                .map(comment -> new CommentCreateDto(
                        comment.getText()
                ))
                .toList();

//        Date createdDate = Date.from(
//                LocalDateTime.now()
//                        .plusHours(3)
//                        .atZone(ZoneId.systemDefault())
//                        .toInstant()
//        );
        Date currentDate = new Date();

        //Optional<Booking> lastBooking = bookinRepository.findByItem_Id(items.get().getId());
        //Optional<Booking> nextBooking = bookinRepository.findByItem_Id(items.get().getId());
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

        ItemDto itemDto = itemMapper.toItemDto(items.get(),commentDtos, lastBooking, nextBooking, id);
        return itemDto;
        //return itemRepository.findByOwner(user.orElse(null));
    }

    public Collection<Item> findAllByText(long userId, String text) {
        //return itemDbStorage.getAllByText(userId, text);//
        return itemRepository.searchByText(userId, text);//
    }

    public Optional<Item> getItemById(long id) {
        //return itemDbStorage.get(id);
        return itemRepository.findById(id);
    }

    public Item create(ItemCreateDto item) throws ParseException {
        //Optional<User> existingUser = userDbStorage.getUserById(item.getUserId());
        Optional<User> existingUser = userRepository.findById(item.getUserId());
        if (existingUser.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", item.getId()));
        }
        if (item.getName() == null || item.getName().isEmpty()) {
            throw new ValidationException("Name cannot be empty");
        }
       // nextID++;
        //item.setId(nextID);
        //Item itemNew = itemDbStorage.creat(item, existingUser.get());
        Item itemNew = new Item();
        itemNew.setName(item.getName());
        itemNew.setOwner(existingUser.get());
        itemNew.setAvailable(item.getAvailable());
        itemNew.setDescription(item.getDescription());
        itemNew = itemRepository.save(itemNew);

        return itemNew;
    }

    public Item update(ItemCreateDto item) throws ParseException {
        //Optional<Item> existingItem = itemDbStorage.get(item.getId());
        Optional<Item> existingItem = itemRepository.findById(Long.valueOf(item.getId()));
        if (existingItem.isEmpty()) {
            throw new NotFoundException(String.format("Item with id = %s not found", item.getId()));
        }
        Item currentItem = existingItem.get();
        if (item.getUserId() != (int) currentItem.getOwner().getId()) {
            throw new NotFoundException("User id wrong");
        }

        Item itemNew = new Item();
        itemNew.setName(item.getName());
       // itemNew.setOwner(item.getUserId());
        itemNew.setAvailable(item.getAvailable());
        itemNew.setDescription(item.getDescription());
        itemNew = itemRepository.save(itemNew);

        //return itemDbStorage.update(item);
        return itemNew;
    }

    public Item updatePatch(ItemPatchDto item) throws ParseException {
        //Optional<Item> existingItem = itemDbStorage.get(item.getId());
        Item existingItem = itemRepository.findById(Long.valueOf(item.getId()))
                .orElseThrow(() -> new NotFoundException(
                        String.format("Item with id = %d not found", item.getId())));

        if (item.getUserId() != Long.valueOf(existingItem.getOwner().getId())) {
            throw new NotFoundException("User is not the owner of this item");
        }

        //existingItem.setOwner(existingItem.getOwner());

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

    public CommentInfoDto createComment(CommentCreateDto comment, long itemId, long userId) throws ParseException {
        Optional<Item> existingItem = itemRepository.findById(Long.valueOf(itemId));
        if (existingItem.isEmpty()) {
            throw new NotFoundException(String.format("Item with id = %s not found", itemId));
        }
        Optional<User> existingUser = userRepository.findById(userId);
        if (existingUser.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", userId));
        }
        //Date createdDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        Date createdDate = Date.from(
                LocalDateTime.now()
                        .plusHours(3) // Добавляем 2 часа
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
        Collection<Booking> existingBookingUser = bookinRepository.findByBookerId((int)userId);
        if (existingBookingUser.isEmpty()) {
            throw new ValidationException(String.format("User id = %s can't write comments", (int)userId));
        }

        Optional<Booking> existBooking = bookinRepository.findByItem_Id((int)itemId);
        if (existBooking.isEmpty()) {
            throw new NotFoundException(String.format("Booking with id = %s not found", itemId));
        }else {
            Collection<Booking> listBookingUser = existBooking.stream().filter(booking -> booking.getBooker().getId() == userId).collect(Collectors.toList());
            Collection<Booking> listBookingEnd = listBookingUser.stream().filter(booking -> booking.getEnd().before(createdDate)).collect(Collectors.toList());
            if (listBookingEnd.isEmpty()) {
                throw new ValidationException(String.format("Access denied for user = %s", userId));
            }
        }

        Comment currentComment = new Comment();
        currentComment.setItem(existingItem.get());
        currentComment.setAuthor(existingUser.get());
        currentComment.setText(comment.getText());

        currentComment.setCreated(createdDate);
        currentComment = commentRepository.save(currentComment);

        CommentInfoDto newComment = itemMapper.toCommentDto(currentComment); //new CommentInfoDto();

        return newComment;
    }
}
