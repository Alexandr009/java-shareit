package ru.practicum.shareit.request.dto;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
public class ItemRequestDto {

    String description;
    Integer requestorId;
}
