package com.github.jinahya.branch.api.client.export.daily.message;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class ExportResponse
        extends AbstractDailyExportMessage {

    private static List<String> toList(final Iterable<?> iterable) {
        Objects.requireNonNull(iterable, "iterable is null");
        return StreamSupport
                .stream(iterable.spliterator(), false)
                .map(String::valueOf)
                .collect(Collectors.toList());
    }

    public List<String> getPaths(final String reportType) {
        Objects.requireNonNull(reportType, "reportType is null");
        return Optional.ofNullable(getUnknownProperties().get(reportType))
                .filter(Iterable.class::isInstance)
                .map(v -> toList((Iterable<?>) v))
                .orElseGet(Collections::emptyList);
    }

    public Map<String, List<String>> getPathsForAllReportTypes() {
        return getUnknownProperties().entrySet().stream()
                .filter(e -> e.getKey().startsWith("eo_"))
                .filter(e -> e.getValue() instanceof Iterable)
                .collect(Collectors.toMap(Map.Entry::getKey, e -> toList((Iterable<?>) e.getValue())));
    }
}
