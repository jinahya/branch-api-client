package com.github.jinahya.branch.api.client.export.custom.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class ExportRequest
        extends AbstractCustomExportMessage {

    private static final long serialVersionUID = 136177180459030742L;

    public static final String RESPONSE_FORMAT_JSON = "json";

    public static final String RESPONSE_FORMAT_CSV = "csv";

    public static final String PATTERN_REGEXP_RESPONSE_FORMAT
            = '(' + RESPONSE_FORMAT_JSON + '|' + RESPONSE_FORMAT_CSV + ')';

    /**
     * The maximum value for {@code $.limit} field.
     */
    public static final int MAX_LIMIT = 2000000;

    @Override
    public String toString() {
        return super.toString() + '{'
               + "organizationId=" + organizationId
               + ",reportType=" + reportType
               + ",startDateLocal=" + startDateLocal
               + ",endDateLocal=" + endDateLocal
               + ",timezone=" + timezone
               + ",fields=" + fields
               + ",limit=" + limit
               + ",responseFormat=" + responseFormat
               + ",filter=" + filter
               + '}';
    }

    @AssertTrue
    private boolean isStartDateBeforeEndDate() {
        if (startDateLocal == null || endDateLocal == null) {
            return true;
        }
        return startDateLocal.isBefore(endDateLocal);
    }

    @JsonProperty
    public ZonedDateTime getStartDate() {
        if (startDateLocal == null) {
            throw new IllegalStateException("startDateLocal is currently null");
        }
        return startDateLocal
                .atZone(Optional.ofNullable(timezone).orElse(ZoneId.systemDefault()))
                .withZoneSameInstant(ZoneOffset.UTC)
                .truncatedTo(ChronoUnit.MILLIS);
    }

    @JsonProperty
    public ZonedDateTime getEndDate() {
        if (endDateLocal == null) {
            throw new IllegalStateException("endDateLocal is currently null");
        }
        return endDateLocal
                .atZone(Optional.ofNullable(timezone).orElse(ZoneId.systemDefault()))
                .withZoneSameInstant(ZoneOffset.UTC)
                .truncatedTo(ChronoUnit.MILLIS);
    }

    private String organizationId;

    @javax.validation.constraints.NotBlank
    @NotBlank
    private String reportType;

    @JsonIgnore
    @javax.validation.constraints.NotNull
    @NotNull
    private LocalDateTime startDateLocal;

    @JsonIgnore
    @javax.validation.constraints.NotNull
    @NotNull
    private LocalDateTime endDateLocal;

    @JsonIgnore
    private ZoneId timezone;

    @javax.validation.constraints.NotEmpty
    @NotEmpty
    private Set<@javax.validation.constraints.NotBlank @NotBlank String> fields;

    // branch.io may adjust the maximum value of the 'limit' and that's why the @Max annotations are commented out
    //@javax.validation.constraints.Max(MAX_LIMIT)
    @javax.validation.constraints.Positive
    //@Max(MAX_LIMIT)
    @Positive
    @Builder.Default
    private int limit = MAX_LIMIT;

    @javax.validation.constraints.Pattern(regexp = PATTERN_REGEXP_RESPONSE_FORMAT)
    @Pattern(regexp = PATTERN_REGEXP_RESPONSE_FORMAT)
    private String responseFormat;

    private List<Object> filter;
}
