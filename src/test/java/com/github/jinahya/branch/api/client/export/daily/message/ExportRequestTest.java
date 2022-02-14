package com.github.jinahya.branch.api.client.export.daily.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.jinahya.branch.api.client.BranchApiClientUtilities;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

@Slf4j
class ExportRequestTest
        extends AbstractDailyExportMessageTest<ExportRequest> {

    ExportRequestTest() {
        super(ExportRequest.class);
    }

    @Test
    void build1__() {
        final var built = ExportRequest.builder()
                .branchKey("branchKey")
                .branchSecret("branchSecret")
                .exportDate(LocalDate.now())
                .build();
        final var string = BranchApiClientUtilities.Jackson.applyObjectWriter(w -> {
            try {
                return w.writeValueAsString(built);
            } catch (final JsonProcessingException jpe) {
                throw new RuntimeException(jpe);
            }
        });
        log.debug("string: \n{}", string);
    }
}