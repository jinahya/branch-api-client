package com.github.jinahya.branch.api.client.export.custom.message;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;

import static com.github.jinahya.branch.api.client.BranchApiClientUtilities.Jackson.applyObjectReader;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class ExportCreationResponseTest
        extends AbstractCustomExportMessageTest<ExportCreationResponse> {

    ExportCreationResponseTest() {
        super(ExportCreationResponse.class);
    }

    @Test
    void read() throws IOException {
        try (var resource = ExportCreationResponse.class.getResourceAsStream("export_job_response.json")) {
            final var deserialized = applyObjectReader(r -> {
                try {
                    return r.readValue(resource, ExportCreationResponse.class);
                } catch (final IOException ioe) {
                    throw new UncheckedIOException(ioe);
                }
            });
            log.debug("deserialized: {}", deserialized);
            assertThat(deserialized.getErrors()).isNotEmpty();
            deserialized.hasErrors(v -> {
                log.debug("errors: {}", v);
            });
            final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
            assertThat(validator.validate(deserialized)).isEmpty();
        }
    }
}