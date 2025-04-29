package ru.practicum.shareit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {

    @InjectMocks
    private ErrorHandler errorHandler;

    @Test
    void handleNotFoundException_ShouldReturnNotFoundStatus() {
        NotFoundException exception = new NotFoundException("Item not found");

        ErrorResponse response = errorHandler.handleException(exception);

        assertEquals("NotFoundException", response.getError());
        assertEquals("Item not found", response.getDescription());
    }

    @Test
    void handleValidationException_WithValidationException_ShouldReturnBadRequest() {
        ValidationException exception = new ValidationException("Validation failed");

        ErrorResponse response = errorHandler.handleValidationException(exception);

        assertEquals("error", response.getError());
        assertEquals("Validation failed", response.getDescription());
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerError() {
        RuntimeException exception = new RuntimeException("Unexpected error");

        ErrorResponse response = errorHandler.handle(exception);

        assertEquals("error", response.getError());
        assertEquals("Unexpected error", response.getDescription());
    }

    @Test
    void errorResponse_ShouldContainCorrectFields() {
        ErrorResponse response = new ErrorResponse("testError", "Test message");

        assertEquals("testError", response.getError());
        assertEquals("Test message", response.getDescription());
    }
}