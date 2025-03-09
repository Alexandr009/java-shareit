package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @NotNull(groups = {Create.class})
    private String name;

    @Email(groups = {Update.class, Create.class})
    @NotNull(groups = {Create.class})
    private String email;

    private Integer id;
}

