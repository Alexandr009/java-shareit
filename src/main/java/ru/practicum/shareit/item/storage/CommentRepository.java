package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment findAllByItem(Optional<Item> items);

    List<Comment> findAllByItem(Item item);

    List<Comment> findAllByItem_Id(Long id);
}
