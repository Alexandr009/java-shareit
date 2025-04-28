package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ItemPatchDto {

    String name;
    String description;
    Boolean available;
    Long userId;
    Integer id;
}

