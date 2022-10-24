package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.ApprovedState;
import ru.practicum.shareit.booking.dto.BookingReceivingDto;
import ru.practicum.shareit.booking.dto.BookingRequestState;
import ru.practicum.shareit.base.client.BaseClient;

import java.util.Map;

/**
 * Клиент для работы с эндпоинтами бронирований
 */
@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> findByBookerIdAndStatus(Long userId, BookingRequestState state, Integer from,
                                                          Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> add(Long userId, BookingReceivingDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> findById(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> findByOwnerIdAndStatus(Long userId, BookingRequestState state, Integer from,
                                                         Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> handleStatus(Long userId, Long bookingId, ApprovedState approvedState) {
        Map<String, Object> parameters = Map.of(
                "approved", approvedState.name()
        );
        return patch("/" + bookingId + "?approved={approved}", userId, parameters);
    }
}
