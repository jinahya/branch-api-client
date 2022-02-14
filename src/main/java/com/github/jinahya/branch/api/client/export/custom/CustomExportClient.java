package com.github.jinahya.branch.api.client.export.custom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.jinahya.branch.api.client.AbstractClient;
import com.github.jinahya.branch.api.client.export.custom.message.ExportCreationRequest;
import com.github.jinahya.branch.api.client.export.custom.message.ExportCreationResponse;
import com.github.jinahya.branch.api.client.export.custom.message.ExportJobStatus;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static com.github.jinahya.branch.api.client.BranchApiClientConstants.Http.HEADER_ACCEPT;
import static com.github.jinahya.branch.api.client.BranchApiClientConstants.Http.HEADER_ACCESS_TOKEN;
import static com.github.jinahya.branch.api.client.BranchApiClientConstants.Http.HEADER_CONTENT_TYPE;
import static com.github.jinahya.branch.api.client.BranchApiClientConstants.Http.MEDIA_TYPE_APPLICATION_JSON;
import static com.github.jinahya.branch.api.client.BranchApiClientUtilities.Jackson.applyObjectReader;
import static com.github.jinahya.branch.api.client.BranchApiClientUtilities.Jackson.applyObjectWriter;

/**
 * A client class for accessing <a href="https://help.branch.io/developers-hub/docs/custom-exports">Custom Exports &
 * API</a>.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://help.branch.io/developers-hub/docs/custom-exports">Custom Exports & API (branch.io)</a>
 */
@SuperBuilder
public class CustomExportClient
        extends AbstractClient {

    private static final String EXPORT_REQUEST_URI = "https://api2.branch.io/v2/logs";

    @Override
    public String toString() {
        return super.toString() + '{'
               + '}';
    }

    /**
     * Request a new log exportation job.
     *
     * @param exportCreationRequest a request.
     * @return a future of response.
     */
    @javax.validation.constraints.NotNull
    @NotNull
    public CompletableFuture<ExportCreationResponse> requestExport(
            @javax.validation.Valid @javax.validation.constraints.NotNull
            @Valid @NotNull final ExportCreationRequest exportCreationRequest) {
        Objects.requireNonNull(exportCreationRequest, "exportCreationRequest is null");
        final var uri = URI.create(EXPORT_REQUEST_URI + "?app_id=" + appId);
        final var request = HttpRequest.newBuilder()
                .POST(BodyPublishers.ofString(applyObjectWriter(w -> {
                    try {
                        return w.writeValueAsString(exportCreationRequest);
                    } catch (final JsonProcessingException jpe) {
                        throw new UncheckedIOException(jpe);
                    }
                })))
                .uri(uri)
                .header(HEADER_CONTENT_TYPE, MEDIA_TYPE_APPLICATION_JSON)
                .header(HEADER_ACCEPT, MEDIA_TYPE_APPLICATION_JSON)
                .header(HEADER_ACCESS_TOKEN, accessToken)
                .timeout(timeout())
                .build();
        final var client = HttpClient.newBuilder()
                .connectTimeout(connectTimeout())
                .build();
        return client.sendAsync(request, BodyHandlers.ofString())
                .thenApply(r -> {
                    final int statusCode = r.statusCode();
                    if (statusCode != 200) {
                        throw new RuntimeException("unsuccessful status code: " + statusCode);
                    }
                    return r.body();
                })
                .thenApply(b -> applyObjectReader(r -> {
                    try {
                        return r.readValue(b, ExportCreationResponse.class);
                    } catch (final IOException ioe) {
                        throw new UncheckedIOException(ioe);
                    }
                }));
    }

    /**
     * Reads the status of a log export job.
     *
     * @param exportCreationResponse a log export response.
     * @return a future of response.
     */
    @javax.validation.constraints.NotNull
    @NotNull
    public CompletableFuture<ExportJobStatus> checkStatus(
            @javax.validation.Valid @javax.validation.constraints.NotNull
            @Valid @NotNull final ExportCreationResponse exportCreationResponse) {
        Objects.requireNonNull(exportCreationResponse, "exportJobResponse is null");
        if (exportCreationResponse.hasErrors()) {
            throw new IllegalArgumentException("has errors: " + exportCreationResponse);
        }
        final var uri = URI.create(exportCreationResponse.getExportJobStatusUrl());
        final var request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header(HEADER_ACCEPT, MEDIA_TYPE_APPLICATION_JSON)
                .header(HEADER_ACCESS_TOKEN, accessToken)
                .timeout(timeout())
                .build();
        final var client = HttpClient.newBuilder()
                .connectTimeout(connectTimeout())
                .build();
        return client.sendAsync(request, BodyHandlers.ofString())
                .thenApply(r -> {
                    final int statusCode = r.statusCode();
                    if (statusCode != 200) {
                        throw new RuntimeException("unsuccessful status code: " + statusCode);
                    }
                    return r.body();
                })
                .thenApply(b -> applyObjectReader(r -> {
                    try {
                        return r.readValue(b, ExportJobStatus.class);
                    } catch (final IOException ioe) {
                        throw new UncheckedIOException(ioe);
                    }
                }));
    }

    /**
     * Reads an exported log.
     *
     * @param exportJobStatus a {@link ExportJobStatus#isCompleted() completed} job status.
     * @return a future of log stream.
     */
    @javax.validation.constraints.NotNull
    @NotNull
    public CompletableFuture<InputStream> readExported(
            @javax.validation.Valid @javax.validation.constraints.NotNull
            @Valid @NotNull final ExportJobStatus exportJobStatus) {
        Objects.requireNonNull(exportJobStatus, "exportJobStatus is null");
        if (!exportJobStatus.isCompleted()) {
            throw new IllegalArgumentException("not completed: " + exportJobStatus);
        }
        final var uri = URI.create(exportJobStatus.getResponseUrl());
        final var request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .timeout(timeout())
                .build();
        final var client = HttpClient.newBuilder()
                .connectTimeout(connectTimeout())
                .build();
        return client.sendAsync(request, BodyHandlers.ofInputStream())
                .thenApply(r -> {
                    final int statusCode = r.statusCode();
                    if (statusCode != 200) {
                        throw new RuntimeException("unsuccessful status code: " + statusCode);
                    }
                    return r.body();
                });
    }

    /**
     * Downloads an exported log to a file and applies to specified function.
     *
     * @param exportJobStatus a {@link ExportJobStatus#isCompleted() completed} exportation job status.
     * @param logFileFunction a function to which the file is applied.
     * @return a future of desired result.
     */
    @javax.validation.constraints.NotNull
    @NotNull
    public <R> CompletableFuture<R> downloadExportedAndRead(
            @javax.validation.Valid @javax.validation.constraints.NotNull
            @Valid @NotNull final ExportJobStatus exportJobStatus,
            final Function<? super ReadableByteChannel, ? extends R> logFileFunction) {
        return readExported(exportJobStatus)
                .thenApply(s -> {
                    try {
                        final var path = Files.createTempFile("prefix", "suffix");
                        final var bytes = Files.copy(s, path);
                        try {
                            try (var channel = FileChannel.open(path, StandardOpenOption.READ)) {
                                return logFileFunction.apply(channel);
                            }
                        } finally {
                            Files.delete(path);
                        }
                    } catch (final IOException ioe) {
                        throw new UncheckedIOException(ioe);
                    }
                });
    }

    @javax.validation.constraints.NotBlank
    @NotBlank
    @ToString.Exclude
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private final String appId;

    @javax.validation.constraints.NotBlank
    @NotBlank
    @ToString.Exclude
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private final String accessToken;
}
