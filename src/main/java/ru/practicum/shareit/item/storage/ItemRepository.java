package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Item findByName(String name);
    Collection<Item> findByOwner(User owner);

    @Query("SELECT i FROM Item i " +
            "WHERE i.owner.id = :userId " +
            "AND (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND i.available = true")
    Collection<Item> searchByText(@Param("userId") long userId, @Param("text") String text);
}
