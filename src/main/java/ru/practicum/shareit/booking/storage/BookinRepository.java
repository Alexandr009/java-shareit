package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

@Repository
public interface BookinRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findByBookerId(Integer bookerId);

    Optional<Booking> findByItem_Id(Integer itemId);

    Collection<Booking> findAllByStatus(Status status);

    Collection<Booking> findAllByStartAfter(Date startAfter);

    Collection<Booking> findAllByEndBefore(Date createdDate);

    Collection<Booking> findAllByStartBefore(Date startBefore);

    Collection<Booking> findAllByStartBeforeAndEndAfter(Date startBefore, Date endAfter);

    Collection<Booking> findByItem(Item item);
}
