package com.github.jinahya.branch.api.client.export.daily;

import com.github.jinahya.branch.api.client.AbstractClient;
import com.github.jinahya.branch.api.client.BranchApiClientUtilities.Jackson;
import com.github.jinahya.branch.api.client.export.daily.message.ExportRequest;
import com.github.jinahya.branch.api.client.export.daily.message.ExportResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

// https://help.branch.io/developers-hub/docs/daily-exports
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class DailyExportClient
        extends AbstractClient {

    public static final String EXPORT_REQUEST_URI = "https://api2.branch.io/v3/export";

    @Override
    public String toString() {
        return super.toString() + '{'
               + '}';
    }

    /**
     * Request to {@value #EXPORT_REQUEST_URI} with specified request and returns the result.
     *
     * @param exportRequest the request object.
     * @return a future of a http response.
     */
    @javax.validation.constraints.NotNull
    @NotNull
    public CompletableFuture<ExportResponse> requestExport(
            @javax.validation.Valid @javax.validation.constraints.NotNull
            @Valid @NotNull final ExportRequest exportRequest) {
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
//        return httpClient.sendAsync(httpRequest, BodyHandlers.ofString())
//                .thenApply(checkStatusCode200AndBody())
//                .thenApply(b -> Jackson.readValue(ExportResponse.class, b));
        return sendAsync(httpClient, httpRequest, ExportResponse.class);
    }
}
