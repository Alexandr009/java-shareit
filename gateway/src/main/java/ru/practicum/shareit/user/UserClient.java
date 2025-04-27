package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;

import java.util.Map;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> findAll() {
        return get("");
    }

    public ResponseEntity<Object> getUserById(long id) {
        return get("/" + id);
    }

    public ResponseEntity<Object> create(User user) {
        return post("", user);
    }

    public ResponseEntity<Object> update(User user) {
        return put("", 0L, user); // 0L как заглушка для userId
    }

    public ResponseEntity<Object> updatePatch(UserPatchDto user, long id) {
        return patch("/" + id, 0L, user); // 0L как заглушка для userId
    }

    public ResponseEntity<Object> remove(long id) {
        return delete("/" + id);
    }
}