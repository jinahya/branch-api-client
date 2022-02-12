package com.github.jinahya.branch.api.client.export.custom;

import org.junit.jupiter.api.Test;

class CustomExportClientTest {

    @Test
    void builder__() {
        final var built = CustomExportClient.builder()
                .appId("appId")
                .accessToken("accessToken")
                .build();
    }
}