package com.github.jinahya.branch.api.client.export.custom.message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.Pattern;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static com.github.jinahya.branch.api.client.BranchApiClientConstants.BeanValidation.PATTERN_REGEXP_NOT_BLANK;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class ExportCreationResponse
        extends AbstractCustomExportMessage {

    @Override
    public String toString() {
        return super.toString() + '{'
               + "handle=" + handle
               + ",exportJobStatusUrl=" + exportJobStatusUrl
               + '}';
    }

    @AssertFalse
    private boolean isHandleBlankWithoutErrors() {
        if (hasErrors()) {
            return false;
        }
        return handle == null || handle.isBlank();
    }

    @AssertFalse
    private boolean isExportJobStatusUrlBlankWithoutErrors() {
        if (hasErrors()) {
            return false;
        }
        return exportJobStatusUrl == null || exportJobStatusUrl.isBlank();
    }

    /**
     * Returns the value of {@code $.errors}.
     *
     * @return an optional of the value of {@code $.errors}.
     */
    public Optional<Object> getErrors() {
        return Optional.ofNullable(getUnknownProperties().get("errors"));
    }

    /**
     * Checks whether this message has a value of {@code $.errors}, accepts to specified consumer if and only if it
     * exists, returns a boolean value.
     *
     * @param consumer the consumer which accepts the value of {@code $.errors}.
     * @return {@code true} if this message has a value of {@code $.errors}; {@code false} otherwise.
     */
    public boolean hasErrors(final Consumer<Object> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        return getErrors()
                .map(v -> {
                    consumer.accept(v);
                    return true;
                })
                .orElse(false);
    }

    public boolean hasErrors() {
        return hasErrors(v -> {
            // does nothing
        });
    }

    @javax.validation.constraints.Pattern(regexp = PATTERN_REGEXP_NOT_BLANK)
    @Pattern(regexp = PATTERN_REGEXP_NOT_BLANK)
    private String handle;

    @javax.validation.constraints.Pattern(regexp = PATTERN_REGEXP_NOT_BLANK)
    @Pattern(regexp = PATTERN_REGEXP_NOT_BLANK)
    private String exportJobStatusUrl;
}
