package com.github.jinahya.branch.api.client.export.custom.message;

import com.github.jinahya.branch.api.client.BranchApiClientUtilities.Jackson;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class ExportResponseTest
        extends AbstractCustomExportMessageTest<ExportResponse> {

    private static <R> R applyExample1(final Function<? super InputStream, ? extends R> function) throws IOException {
        Objects.requireNonNull(function, "function is null");
        try (var resource = ExportResponse.class.getResourceAsStream("export_response_example1.json")) {
            return function.apply(resource);
        }
    }

    ExportResponseTest() {
        super(ExportResponse.class);
    }

    @Test
    void read() throws IOException {
        final var value = applyExample1(r -> Jackson.readValue(ExportResponse.class, r));
        log.debug("value: {}", value);
        assertThat(value.getErrors()).isNotEmpty();
        value.hasErrors(v -> {
            log.debug("errors: {}", v);
        });
        final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        assertThat(validator.validate(value)).isEmpty();
    }
}