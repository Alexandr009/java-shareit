package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class User {
    @NotNull(groups = {Create.class})
    String name;

    @Email(groups = {Update.class, Create.class})
    @NotNull(groups = {Create.class})
    String email;

    Integer id;
}

