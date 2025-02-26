package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * TODO Sprint add-controllers.
 */

@Setter
@Getter
@Data
public class ItemCreateDto {
    @NonNull
    String name;
    String description;
    Boolean available;
    Long userId;
}
