package ru.practicum.shareit.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class User {
    String name;
    String email;
    Integer id;
}
