package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.user.User;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String name;
    String description;

    @Column(name = "is_available")
    Boolean available;
    //String available;
    @ManyToOne(fetch = FetchType.EAGER)//LAZY
            @JoinColumn(name = "owner_id")
    User owner;
}
