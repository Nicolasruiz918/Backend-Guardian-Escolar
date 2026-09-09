package com.guardianescolar.api.shared.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

class GlobalExceptionHandlerTest {

    @Test
    void handlesApiExceptionWithConfiguredStatus() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test");

        var response = handler.handleApiException(
                new ApiException(HttpStatus.CONFLICT, "Conflict detected"),
                request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Conflict detected");
        assertThat(response.getBody().path()).isEqualTo("/api/test");
    }
}
