package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.util.Date;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String text;

    @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "item_id")
    Item item;

    @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "author_id")
    User author;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date created;
}
