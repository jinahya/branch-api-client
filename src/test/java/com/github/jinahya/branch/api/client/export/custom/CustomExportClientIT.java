package com.github.jinahya.branch.api.client.export.custom;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jinahya.branch.api.client.BranchApiClientUtilities;
import com.github.jinahya.branch.api.client.export.custom.message.ExportRequest;
import com.github.jinahya.branch.api.client.export.custom.message.ExportStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Slf4j
class CustomExportClientIT {

    static final String SYSTEM_PROPERTY_NAME_APP_ID = "customExportAppId";

    static final String SYSTEM_PROPERTY_NAME_ACCESS_TOKEN = "customExportAccessToken";

    private static Stream<String> reportTypes() {
        return Stream.of(
//                "eo_install",
                "eo_open"
        );
    }

    @MethodSource({"reportTypes"})
    @ParameterizedTest
    @EnabledIfSystemProperty(named = SYSTEM_PROPERTY_NAME_APP_ID, matches = ".+")
    @EnabledIfSystemProperty(named = SYSTEM_PROPERTY_NAME_ACCESS_TOKEN, matches = ".+")
    void test(final String reportType) throws Exception {
        final var appId = System.getProperty(SYSTEM_PROPERTY_NAME_APP_ID);
        final var accessCode = System.getProperty(SYSTEM_PROPERTY_NAME_ACCESS_TOKEN);
        final var client = CustomExportClient.builder()
                .appId(appId)
                .accessToken(accessCode)
                .connectTimeout(Duration.ofSeconds(1L))
                .timeout(Duration.ofSeconds(2L))
                .build();
        final var request = ExportRequest.builder()
                .reportType(reportType)
                .startDateLocal(LocalDateTime.now().minusDays(1L).minusHours(1L))
                .endDateLocal(LocalDateTime.now().minusDays(1L))
                .timezone(ZoneId.systemDefault())
                .fields(Set.of("timestamp", "name"))
                .responseFormat(ExportRequest.RESPONSE_FORMAT_JSON)
                .limit(4)
                .build();
        log.debug("request: {}", request);
        final var response = client.requestExport(request).get();
        log.debug("response: {}", response);
        if (response.hasErrors(v -> log.error("error: {}", v))) {
            return;
        }
        ExportStatus status = null;
        for (int i = 0; i < 128; i++) {
            Thread.sleep(TimeUnit.SECONDS.toMillis(16L));
            status = client.checkStatus(response).get();
            log.debug("status: {}", status);
            if (status.isCompleted()) {
                break;
            }
        }
        if (!status.isCompleted()) {
            log.error("not completed");
            return;
        }
        try (var input = client.readExported(status).get();
             var reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
             var lines = reader.lines()) {
            lines.forEach(l -> log.debug("direct line: {}", l));
        }
        client.downloadExportedAndRead(status, c -> {
            log.debug("channel: {}", c);
            try { // see it doesn't matter!
                c.close();
            } catch (final IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
            return null;
        }).get();
        client.downloadExportedAndReadLines(status, s -> {
            log.debug("stream: {}", s);
            s.forEach(l -> {
                log.debug("stream line: {}", l);
            });
            if (true) { // see it doesn't matter!
                s.close();
            }
            return null;
        }).get();
    }

    @Test
    @EnabledIfSystemProperty(named = SYSTEM_PROPERTY_NAME_APP_ID, matches = ".+")
    @EnabledIfSystemProperty(named = SYSTEM_PROPERTY_NAME_ACCESS_TOKEN, matches = ".+")
    void test() throws Exception {
        final var appId = System.getProperty(SYSTEM_PROPERTY_NAME_APP_ID);
        final var accessCode = System.getProperty(SYSTEM_PROPERTY_NAME_ACCESS_TOKEN);
        final var client = CustomExportClient.builder()
                .appId(appId)
                .accessToken(accessCode)
                .connectTimeout(Duration.ofSeconds(1L))
                .timeout(Duration.ofSeconds(2L))
                .build();
        final var request = ExportRequest.builder()
                .reportType("eo_open")
                .startDateLocal(LocalDateTime.now().minusDays(1L).minusHours(1L))
                .endDateLocal(LocalDateTime.now().minusDays(1L))
                .timezone(ZoneId.systemDefault())
                .fields(Set.of("timestamp", "name"))
                .responseFormat(ExportRequest.RESPONSE_FORMAT_JSON)
                .limit(4)
                .build();
        log.debug("request: {}", request);
        final var response = client.requestExport(request).get();
        log.debug("response: {}", response);
        if (response.hasErrors(v -> log.error("error: {}", v))) {
            return;
        }
        ExportStatus status = null;
        for (int i = 0; i < 128; i++) {
            Thread.sleep(TimeUnit.SECONDS.toMillis(16L));
            status = client.checkStatus(response).get();
            log.debug("status: {}", status);
            if (status.isCompleted()) {
                break;
            }
        }
        if (!status.isCompleted()) {
            log.error("not completed");
            return;
        }
        client.readExported(status)
                .thenAccept(s -> {
                    BranchApiClientUtilities.Jackson.applyObjectReader(r -> {
                        try {
                            try (var i = r.forType(ObjectNode.class).readValues(s)) {
                                while (i.hasNext()) {
                                    final var value = i.next();
                                    log.debug("value: {}", value);
                                }
                            }
                        } catch (final IOException ioe) {
                            throw new UncheckedIOException(ioe);
                        }
                        return null;
                    });
                })
                .get();
    }
}