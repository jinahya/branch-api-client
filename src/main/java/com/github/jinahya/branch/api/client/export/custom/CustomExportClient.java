package com.github.jinahya.branch.api.client.export.custom;

import com.github.jinahya.branch.api.client.AbstractClient;
import com.github.jinahya.branch.api.client.BranchApiClientUtilities.Jackson;
import com.github.jinahya.branch.api.client.export.custom.message.ExportRequest;
import com.github.jinahya.branch.api.client.export.custom.message.ExportResponse;
import com.github.jinahya.branch.api.client.export.custom.message.ExportStatus;
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
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.jinahya.branch.api.client.BranchApiClientConstants.Http.HEADER_ACCEPT;
import static com.github.jinahya.branch.api.client.BranchApiClientConstants.Http.HEADER_ACCESS_TOKEN;
import static com.github.jinahya.branch.api.client.BranchApiClientConstants.Http.HEADER_CONTENT_TYPE;
import static com.github.jinahya.branch.api.client.BranchApiClientConstants.Http.MEDIA_TYPE_APPLICATION_JSON;

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
     * @param exportRequest a request.
     * @return a future of response.
     */
    @javax.validation.constraints.NotNull
    @NotNull
    public CompletableFuture<ExportResponse> requestExport(
            @javax.validation.Valid @javax.validation.constraints.NotNull
            @Valid @NotNull final ExportRequest exportRequest) {
        Objects.requireNonNull(exportRequest, "exportCreationRequest is null");
        final var uri = URI.create(EXPORT_REQUEST_URI + "?app_id=" + appId);
        final var request = HttpRequest.newBuilder()
                .POST(BodyPublishers.ofString(Jackson.writeValueAsString(exportRequest)))
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
                .thenApply(check200())
                .thenApply(b -> Jackson.readValue(ExportResponse.class, b))
                ;
    }

    /**
     * Reads the status of a log export job.
     *
     * @param exportResponse a log export response.
     * @return a future of response.
     */
    @javax.validation.constraints.NotNull
    @NotNull
    public CompletableFuture<ExportStatus> checkStatus(
            @javax.validation.Valid @javax.validation.constraints.NotNull
            @Valid @NotNull final ExportResponse exportResponse) {
        Objects.requireNonNull(exportResponse, "exportJobResponse is null");
        if (exportResponse.hasErrors()) {
            throw new IllegalArgumentException("has errors: " + exportResponse);
        }
        final var uri = URI.create(exportResponse.getExportJobStatusUrl());
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
                .thenApply(check200())
                .thenApply(b -> Jackson.readValue(ExportStatus.class, b))
                ;
    }

    /**
     * Reads an exported log.
     *
     * @param exportStatus a {@link ExportStatus#isCompleted() completed} job status.
     * @return a future of log stream.
     */
    @javax.validation.constraints.NotNull
    @NotNull
    public CompletableFuture<InputStream> readExported(
            @javax.validation.Valid @javax.validation.constraints.NotNull
            @Valid @NotNull final ExportStatus exportStatus) {
        Objects.requireNonNull(exportStatus, "exportJobStatus is null");
        if (!exportStatus.isCompleted()) {
            throw new IllegalArgumentException("not completed: " + exportStatus);
        }
        final var uri = URI.create(exportStatus.getResponseUrl());
        final var request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .timeout(timeout())
                .build();
        final var client = HttpClient.newBuilder()
                .connectTimeout(connectTimeout())
                .build();
        return client.sendAsync(request, BodyHandlers.ofInputStream())
                .thenApply(check200());
    }

    /**
     * Downloads an exported log to a file and applies to specified function.
     *
     * @param exportStatus a {@link ExportStatus#isCompleted() completed} exportation job status.
     * @param path         a path to which the exported log is downloaded.
     * @return a future of desired result.
     */
    @javax.validation.constraints.NotNull
    @NotNull
    public CompletableFuture<Path> downloadExported(
            @javax.validation.Valid @javax.validation.constraints.NotNull
            @Valid @NotNull final ExportStatus exportStatus,
            @javax.validation.constraints.NotNull
            @NotNull final Path path) {
        return readExported(exportStatus)
                .thenApply(s -> {
                    try {
                        final var bytes = Files.copy(s, path);
                        return path;
                    } catch (final IOException ioe) {
                        throw new UncheckedIOException("failed to copy stream to " + path, ioe);
                    }
                });
    }

    /**
     * Downloads an exported log to a file and applies to specified function.
     *
     * @param exportStatus a {@link ExportStatus#isCompleted() completed} exportation job status.
     * @param fileFunction a function to which the file is applied.
     * @return a future of desired result.
     */
    @javax.validation.constraints.NotNull
    @NotNull
    protected <R> CompletableFuture<R> downloadExportedAndApply(
            @javax.validation.Valid @javax.validation.constraints.NotNull
            @Valid @NotNull final ExportStatus exportStatus,
            final Function<? super Path, ? extends R> fileFunction) {
        final Path path;
        try {
            path = Files.createTempFile("prefix", "suffix");
        } catch (final IOException ioe) {
            throw new UncheckedIOException("failed to create a temp file", ioe);
        }
        return downloadExported(exportStatus, path)
                .thenApply(p -> {
                    try {
                        return fileFunction.apply(p);
                    } finally {
                        try {
                            Files.deleteIfExists(p);
                        } catch (final IOException ioe) {
                            throw new UncheckedIOException("failed to delete " + path, ioe);
                        }
                    }
                });
    }

    /**
     * Downloads an exported log to a file and applies a readable byte channel to specified function.
     *
     * @param exportStatus    a {@link ExportStatus#isCompleted() completed} exportation job status.
     * @param channelFunction a function to which the file is applied.
     * @return a future of desired result.
     */
    @javax.validation.constraints.NotNull
    @NotNull
    public <R> CompletableFuture<R> downloadExportedAndRead(
            @javax.validation.Valid @javax.validation.constraints.NotNull
            @Valid @NotNull final ExportStatus exportStatus,
            final Function<? super ReadableByteChannel, ? extends R> channelFunction) {
        return downloadExportedAndApply(exportStatus, p -> {
            try (var channel = FileChannel.open(p, StandardOpenOption.READ)) {
                return channelFunction.apply(channel);
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        });
    }

    /**
     * Downloads an exported log to a file and applies a stream of lines to specified function.
     *
     * @param exportStatus   a {@link ExportStatus#isCompleted() completed} exportation job status.
     * @param streamFunction a function to which the line stream is applied.
     * @return a future of desired result.
     */
    @javax.validation.constraints.NotNull
    @NotNull
    public <R> CompletableFuture<R> downloadExportedAndStream(
            @javax.validation.Valid @javax.validation.constraints.NotNull
            @Valid @NotNull final ExportStatus exportStatus,
            final Function<? super Stream<? super String>, ? extends R> streamFunction) {
        return downloadExportedAndApply(exportStatus, p -> {
            try (var lines = Files.lines(p)) {
                return streamFunction.apply(lines);
            } catch (IOException ioe) {
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
