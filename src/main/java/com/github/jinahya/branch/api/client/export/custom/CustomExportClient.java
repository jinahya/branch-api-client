package com.github.jinahya.branch.api.client.export.custom;

import com.github.jinahya.branch.api.client.AbstractClient;
import com.github.jinahya.branch.api.client.BranchApiClientUtilities.Jackson;
import com.github.jinahya.branch.api.client.export.custom.message.ExportRequest;
import com.github.jinahya.branch.api.client.export.custom.message.ExportResponse;
import com.github.jinahya.branch.api.client.export.custom.message.ExportStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
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
        extends AbstractClient
        implements ICustomExportClient {

    private static final System.Logger log = System.getLogger(CustomExportClient.class.getName());

    private static final String EXPORT_REQUEST_URI = "https://api2.branch.io/v2/logs";

    @Override
    public String toString() {
        return super.toString() + '{'
               + '}';
    }

    @Override
    public CompletableFuture<ExportResponse> requestExport(final ExportRequest exportRequest) {
        Objects.requireNonNull(exportRequest, "exportCreationRequest is null");
        final var httpRequest = HttpRequest.newBuilder()
                .POST(BodyPublishers.ofString(Jackson.writeValueAsString(exportRequest)))
                .uri(URI.create(EXPORT_REQUEST_URI + "?app_id=" + URLEncoder.encode(appId, StandardCharsets.UTF_8)))
                .header(HEADER_CONTENT_TYPE, MEDIA_TYPE_APPLICATION_JSON)
                .header(HEADER_ACCEPT, MEDIA_TYPE_APPLICATION_JSON)
                .header(HEADER_ACCESS_TOKEN, accessToken)
                .timeout(timeout())
                .build();
        final var httpClient = HttpClient.newBuilder()
                .connectTimeout(connectTimeout())
                .build();
        return sendAsyncAndReadJsonValueFromStringBody(httpClient, httpRequest, ExportResponse.class);
    }

    @Override
    public CompletableFuture<ExportStatus> checkStatus(final ExportResponse exportResponse) {
        Objects.requireNonNull(exportResponse, "exportJobResponse is null");
        if (exportResponse.hasErrors()) {
            throw new IllegalArgumentException("has errors: " + exportResponse);
        }
        final var httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(Objects.requireNonNull(exportResponse.getExportJobStatusUrl())))
                .header(HEADER_ACCEPT, MEDIA_TYPE_APPLICATION_JSON)
                .header(HEADER_ACCESS_TOKEN, accessToken)
                .timeout(timeout())
                .build();
        final var httpClient = HttpClient.newBuilder()
                .connectTimeout(connectTimeout())
                .build();
        return sendAsyncAndReadJsonValueFromStringBody(httpClient, httpRequest, ExportStatus.class);
    }

    @Override
    public CompletableFuture<InputStream> readExported(final ExportStatus exportStatus) {
        Objects.requireNonNull(exportStatus, "exportStatus is null");
        if (!exportStatus.isCompleted()) {
            throw new IllegalArgumentException("not completed: " + exportStatus);
        }
        final var httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(Objects.requireNonNull(exportStatus.getResponseUrl())))
                .timeout(timeout())
                .build();
        final var httpClient = HttpClient.newBuilder()
                .connectTimeout(connectTimeout())
                .build();
        return httpClient.sendAsync(httpRequest, BodyHandlers.ofInputStream())
                .thenApply(checkStatusCode200());
    }

    @Override
    public CompletableFuture<Path> downloadExported(final ExportStatus exportStatus,
                                                    final Supplier<? extends Path> pathSupplier) {
        return readExported(exportStatus)
                .thenApply(s -> {
                    final var path = pathSupplier.get();
                    try {
                        final var bytes = Files.copy(s, path, StandardCopyOption.REPLACE_EXISTING);
                    } catch (final IOException ioe) {
                        throw new UncheckedIOException("failed to download to " + path, ioe);
                    }
                    return path;
                });
    }

    /**
     * Downloads an exported log to a temporary file and applies to specified function.
     *
     * @param exportStatus a {@link ExportStatus#isCompleted() completed} exportation job status.
     * @param fileFunction a function to which the file is applied.
     * @return a future of desired result.
     */
    protected <R> CompletableFuture<R> downloadExportedAndApply(
            final ExportStatus exportStatus, final Function<? super Path, ? extends R> fileFunction) {
        Objects.requireNonNull(fileFunction, "fileFunction is null");
        return downloadExported(
                exportStatus,
                () -> {
                    try {
                        return Files.createTempFile("prefix", "suffix");
                    } catch (final IOException ioe) {
                        throw new UncheckedIOException("failed to create a temp file", ioe);
                    }
                })
                .thenApply(p -> {
                    try {
                        return fileFunction.apply(p);
                    } finally {
                        try {
                            if (!Files.deleteIfExists(p) && Files.exists(p)) {
                                log.log(System.Logger.Level.ERROR, () -> String.format("unable to delete %1$s", p));
                            }
                        } catch (final IOException ioe) {
                            throw new UncheckedIOException("failed to delete " + p, ioe);
                        }
                    }
                });
    }

    @Override
    public <R> CompletableFuture<R> downloadExportedAndRead(
            final ExportStatus exportStatus,
            final Function<? super ReadableByteChannel, ? extends R> channelFunction) {
        Objects.requireNonNull(channelFunction, "channelFunction is null");
        return downloadExportedAndApply(exportStatus, p -> {
            try (var channel = FileChannel.open(p, StandardOpenOption.READ)) {
                return channelFunction.apply(channel);
            } catch (final IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        });
    }

    @Override
    public <R> CompletableFuture<R> downloadExportedAndReadLines(
            final ExportStatus exportStatus, final Function<? super Stream<String>, ? extends R> streamFunction) {
        Objects.requireNonNull(streamFunction, "streamFunction is null");
        return downloadExportedAndApply(exportStatus, p -> {
            try (var lines = Files.lines(p)) {
                return streamFunction.apply(lines);
            } catch (final IOException ioe) {
                throw new UncheckedIOException("failed to close the line stream", ioe);
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
