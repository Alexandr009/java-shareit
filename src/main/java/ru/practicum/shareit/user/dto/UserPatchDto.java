package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPatchDto {
    @NotNull(groups = {ru.practicum.shareit.user.dto.Create.class})
    private String name;

    @Email(groups = {ru.practicum.shareit.user.dto.Update.class, ru.practicum.shareit.user.dto.Create.class})
    @NotNull(groups = {ru.practicum.shareit.user.dto.Create.class})
    private String email;
}

interface Create {
}

interface Update {
}