package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ItemCreateDto {

    @NotNull(groups = {ru.practicum.shareit.item.dto.Create.class})
    String name;

    @NotNull(groups = {ru.practicum.shareit.item.dto.Create.class})
    String description;

    @NotNull(groups = {ru.practicum.shareit.item.dto.Create.class})
    Boolean available;
    Long userId;
    Integer id;
    Integer requestId;
}
