package com.github.jinahya.branch.api.client.export.daily;

import com.github.jinahya.branch.api.client.AbstractClient;
import com.github.jinahya.branch.api.client.BranchApiClientUtilities.Jackson;
import com.github.jinahya.branch.api.client.export.daily.message.ExportRequest;
import com.github.jinahya.branch.api.client.export.daily.message.ExportResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.github.jinahya.branch.api.client.BranchApiClientConstants.Http.HEADER_ACCEPT;
import static com.github.jinahya.branch.api.client.BranchApiClientConstants.Http.HEADER_CONTENT_TYPE;
import static com.github.jinahya.branch.api.client.BranchApiClientConstants.Http.MEDIA_TYPE_APPLICATION_JSON;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class DailyExportClient
        extends AbstractClient
        implements IDailyExportClient {

    private static final System.Logger log = System.getLogger(DailyExportClient.class.getName());

    private static final String EXPORT_REQUEST_URI = "https://api2.branch.io/v3/export";

    @Override
    public String toString() {
        return super.toString() + '{'
               + '}';
    }

    @Override
    public CompletableFuture<ExportResponse> requestExport(final ExportRequest exportRequest) {
        Objects.requireNonNull(exportRequest, "exportRequest is null");
        final var httpRequest = HttpRequest.newBuilder()
                .POST(BodyPublishers.ofString(Jackson.writeValueAsString(exportRequest)))
                .uri(URI.create(EXPORT_REQUEST_URI))
                .header(HEADER_CONTENT_TYPE, MEDIA_TYPE_APPLICATION_JSON)
                .header(HEADER_ACCEPT, MEDIA_TYPE_APPLICATION_JSON)
                .timeout(timeout())
                .build();
        final var httpClient = HttpClient.newBuilder()
                .connectTimeout(connectTimeout())
                .build();
        return sendAsyncAndReadJsonValueFromStringBody(httpClient, httpRequest, ExportResponse.class);
    }
}
