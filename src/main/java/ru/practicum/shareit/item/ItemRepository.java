package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.stream.Collectors;

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

//public Collection<Item> getAllByText(long userId, String text) {
//    Collection<Item> result = itemMap.values().stream()
//            .filter(item -> item.getOwner().getId() == userId)
//            .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
//                    item.getDescription().toLowerCase().contains(text.toLowerCase()))
//            .filter(item -> item.getAvailable().toString().contains("true"))
//            .collect(Collectors.toList());
//    return result;
//}