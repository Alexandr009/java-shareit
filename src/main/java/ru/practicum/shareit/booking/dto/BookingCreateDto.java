package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Status;

import java.util.Date;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookingCreateDto {
    Integer id;
    Date start;
    Date end;
    Integer itemId;
    Integer bookerId;
    Status status;
}
