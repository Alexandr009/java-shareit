package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.Collection;

@Repository
public interface BookinRepository extends JpaRepository<Booking, Long>{
    Collection<Booking> findByBookerId(Integer booker_id);
}
