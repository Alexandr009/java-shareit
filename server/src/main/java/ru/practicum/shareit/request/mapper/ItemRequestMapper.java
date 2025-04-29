package ru.practicum.shareit.request.mapper;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

@NoArgsConstructor
@Component
public class ItemRequestMapper {
    public ItemRequestInfoDto toDto(ItemRequest itemRequest, Collection<Item> items) {
        ItemRequestInfoDto itemRequestDto = new ItemRequestInfoDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setRequestor(itemRequest.getRequestor());
        itemRequestDto.setItems(items);
        return itemRequestDto;
    }

}
