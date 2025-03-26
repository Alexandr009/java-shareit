package ru.practicum.shareit.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

//@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Entity
@Table(name = "users")
public class User {
    @NotNull(groups = {Create.class})
    String name;

    @Email(groups = {Update.class, Create.class})
    @NotNull(groups = {Create.class})
    String email;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
}

