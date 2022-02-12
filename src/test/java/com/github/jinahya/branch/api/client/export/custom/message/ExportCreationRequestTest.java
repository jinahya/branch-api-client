package com.github.jinahya.branch.api.client.export.custom.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static com.github.jinahya.branch.api.client.BranchApiClientUtilities.Jackson.applyObjectWriter;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class ExportCreationRequestTest
        extends AbstractCustomExportMessageTest<ExportCreationRequest> {

    ExportCreationRequestTest() {
        super(ExportCreationRequest.class);
    }

    @Test
    void write__() throws JsonProcessingException {
        final var built = ExportCreationRequest.builder()
                .reportType("reportType")
                .startDateLocal(LocalDateTime.now().minusDays(1L).minusHours(1L))
                .endDateLocal(LocalDateTime.now().minusDays(1L))
                .fields(Set.of("f1", "f2"))
                .filter(List.of("gt", "last_attributed_touch_timestamp", 1604015756))
                .build();
        final String string = applyObjectWriter(Function.identity())
                .withDefaultPrettyPrinter()
                .writeValueAsString(built);
        assertThat(string).isNotBlank();
        log.debug("\n{}", string);
    }
}