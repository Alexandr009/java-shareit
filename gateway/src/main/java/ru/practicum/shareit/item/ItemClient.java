package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";
    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> findAll(long userId) {
        Map<String, Object> parameters = Map.of(
                "userId", userId
        );
        return get("?userId={userId}", userId, parameters);
    }

    public ResponseEntity<Object> searchItem(long userId, String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> getItemById(long id, long userId) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> create(long userId, ItemCreateDto item) {
        return post("", userId, item);
    }

    public ResponseEntity<Object> updatePatch(long userId, long id, ItemPatchDto item) {
        return patch("/" + id, userId, item);
    }

    public ResponseEntity<Object> update(ItemCreateDto item) {
        return put("", 0L, item); // 0L как заглушка для userId
    }

    public ResponseEntity<Object> createComment(long userId, long itemId, CommentCreateDto comment) {
        return post("/" + itemId + "/comment", userId, comment);
    }
}