package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.storage.BookinRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class BookinService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookinRepository bookinRepository;

    @Autowired
    public BookinService(ItemRepository itemRepository, UserRepository userRepository, BookinRepository bookinRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookinRepository = bookinRepository;
    }

    public Collection<Booking> findAll(String status, long userId) {
        //return itemDbStorage.getAll();
        Date createdDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        if (status.equals("REJECTED")){
            return bookinRepository.findAllByStatus(Status.REJECTED);
        } else if (status.equals("WAITING")){
            return bookinRepository.findAllByStatus(Status.WAITING);
        } else if (status.equals("FUTURE")){
            return bookinRepository.findAllByStartAfter(createdDate);
        } else if (status.equals("PAST")){
            return bookinRepository.findAllByEndBefore(createdDate);
        } else if (status.equals("CURRENT")){
            return bookinRepository.findAllByStartBeforeAndEndAfter(createdDate, createdDate);
        } else {
            ///return bookinRepository.findAll();
            return bookinRepository.findByBookerId((int) userId);
        }

    }

    public Booking findById(Long id,Long userId) {
        Optional<Booking> booking = bookinRepository.findById(id);
        if (booking.get().getBooker().getId().intValue() != userId.intValue()) {
            if (booking.get().getItem().getOwner().getId().intValue() != userId.intValue()) {
                throw new ValidationException("Access denied for user");
            }
        }
        if (booking.isPresent()) {
            return booking.get();
        }else{
            throw new NotFoundException("Booking not found");
        }

    }

    public Collection<Booking> findAllByUserId(long userId,String status) {
        //return itemDbStorage.getAll();

        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new NotFoundException(String.format("User with id = %s not found", user.get().getId()));
        }

        return bookinRepository.findByBookerId((int) userId);
    }

    public Booking create(BookingCreateDto booking) throws ParseException {

        Optional<User> existingUser = userRepository.findById(Long.valueOf(booking.getBookerId()));
        if (existingUser.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", booking.getBookerId()));
        }
        Optional<Item> existingItem = itemRepository.findById(Long.valueOf(booking.getItemId()));
        if (existingItem.isEmpty()) {
            throw new NotFoundException(String.format("Item with id = %s not found", booking.getItemId()));
        }
        if (existingItem.get().getAvailable() == false){
            throw new ValidationException(String.format("Item with id = %s is available", booking.getItemId()));
        }

        Booking bookingNew = new Booking();
        bookingNew.setBooker(existingUser.get());
        bookingNew.setItem(existingItem.get());
        bookingNew.setStart(booking.getStart());
        bookingNew.setEnd(booking.getEnd());
        bookingNew.setStatus(Status.WAITING);
        bookingNew = bookinRepository.save(bookingNew);

        return bookingNew;
    }

    public Booking update(int bookingId, long userId, boolean approved) {
        Optional<User> existingUser = userRepository.findById(userId);
        if (!existingUser.isPresent()) {
            throw new ValidationException(String.format("User with id = %s not found", userId));
        }
        Optional<Booking> existingBooking = bookinRepository.findById(Long.valueOf(bookingId));
        if (!existingBooking.isPresent()) {
            throw new NotFoundException(String.format("Booking with id = %s not found", Long.valueOf(bookingId)));
        }
        Booking booking = existingBooking.get();
        if (approved == false) {
            booking.setStatus(Status.REJECTED);
        }else {
            booking.setStatus(Status.APPROVED);
        }

        booking = bookinRepository.save(booking);
        return booking;
    }

}
