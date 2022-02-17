package com.github.jinahya.branch.api.client.export.daily.message;

import com.github.jinahya.branch.api.client.BranchApiClientUtilities.Jackson;
import io.vavr.CheckedFunction1;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Function;

@Slf4j
class ExportResponseTest
        extends AbstractDailyExportMessageTest<ExportResponse> {

    private static <R> R applyExample1Unchecked(final Function<? super InputStream, ? extends R> function)
            throws IOException {
        Objects.requireNonNull(function, "function is null");
        try (var resource = ExportResponseTest.class.getResourceAsStream("export_response_example1.json")) {
            return function.apply(resource);
        }
    }

    private static <R> R applyExample1(final CheckedFunction1<? super InputStream, ? extends R> function)
            throws IOException {
        Objects.requireNonNull(function, "function is null");
        return applyExample1Unchecked(function.unchecked());
    }

    ExportResponseTest() {
        super(ExportResponse.class);
    }

    @Test
    void getPaths__export_response_example1() throws IOException {
        final var value = applyExample1Unchecked(s -> Jackson.readValue(ExportResponse.class, s));
        value.getPaths("eo_branch_cta_view").forEach(p -> {
            log.debug("path: {}", p);
        });
    }

    @Test
    void getPathsForAllReportTypes__export_response_example1() throws IOException {
        final var value = applyExample1Unchecked(s -> Jackson.readValue(ExportResponse.class, s));
        value.getPathsForAllReportTypes().forEach((k, v) -> {
            v.forEach(p -> {
                log.debug("{}: {}", k, p);
            });
        });
    }
}
