package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPatchDto {
    @NotNull(groups = {ru.practicum.shareit.user.dto.Create.class})
    String name;

    @Email(groups = {ru.practicum.shareit.user.dto.Update.class, ru.practicum.shareit.user.dto.Create.class})
    @NotNull(groups = {ru.practicum.shareit.user.dto.Create.class})
    String email;
}

