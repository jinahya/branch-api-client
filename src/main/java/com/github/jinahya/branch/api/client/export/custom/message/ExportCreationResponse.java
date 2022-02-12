package com.github.jinahya.branch.api.client.export.custom.message;

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
@NoArgsConstructor
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

    public Optional<Object> getErrors() {
        return Optional.ofNullable(getUnknownProperties().get("errors"));
    }

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
        });
    }

    @javax.validation.constraints.Pattern(regexp = PATTERN_REGEXP_NOT_BLANK)
    @Pattern(regexp = PATTERN_REGEXP_NOT_BLANK)
    private String handle;

    @javax.validation.constraints.Pattern(regexp = PATTERN_REGEXP_NOT_BLANK)
    @Pattern(regexp = PATTERN_REGEXP_NOT_BLANK)
    private String exportJobStatusUrl;
}
