package com.github.jinahya.branch.api.client.export.daily.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.jinahya.branch.api.client.BranchApiClientUtilities;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.function.Function;

@Slf4j
class ExportRequestTest
        extends AbstractDailyExportMessageTest<ExportRequest> {

    ExportRequestTest() {
        super(ExportRequest.class);
    }

    @Nested
    class BuilderTest {

        @Test
        void build1__() throws JsonProcessingException {
            final var built = ExportRequest.builder()
                    .branchKey("branchKey")
                    .branchSecret("branchSecret")
                    .exportDate(LocalDate.now())
                    .build();
            final String string = BranchApiClientUtilities.Jackson.applyObjectWriter(Function.identity())
                    .withDefaultPrettyPrinter()
                    .writeValueAsString(built);
            log.debug("string: \n{}", string);
        }
    }
}