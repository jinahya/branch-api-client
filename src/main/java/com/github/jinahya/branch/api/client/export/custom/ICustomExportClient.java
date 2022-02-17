package com.github.jinahya.branch.api.client.export.custom;

import com.github.jinahya.branch.api.client.IClient;
import com.github.jinahya.branch.api.client.export.custom.message.ExportRequest;
import com.github.jinahya.branch.api.client.export.custom.message.ExportResponse;
import com.github.jinahya.branch.api.client.export.custom.message.ExportStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A client interface for accessing <a href="https://help.branch.io/developers-hub/docs/custom-exports">Custom Exports &
 * API</a>.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://help.branch.io/developers-hub/docs/custom-exports">Custom Exports & API (branch.io)</a>
 */
public interface ICustomExportClient
        extends IClient {

    /**
     * Request a new export for specified request.
     *
     * @param exportRequest the request to send.
     * @return a completable future of export response.
     */
    @javax.validation.constraints.NotNull
    @NotNull
    CompletableFuture<ExportResponse> requestExport(
            @javax.validation.Valid @javax.validation.constraints.NotNull
            @Valid @NotNull final ExportRequest exportRequest);

    /**
     * Reads current status of a log export for specified response.
     *
     * @param exportResponse the log export response whose {@code $.export_job_status_url} is requested.
     * @return a future of response.
     */
    @javax.validation.constraints.NotNull
    @NotNull
    CompletableFuture<ExportStatus> checkStatus(
            @javax.validation.Valid @javax.validation.constraints.NotNull
            @Valid @NotNull final ExportResponse exportResponse);

    /**
     * Reads an exported log.
     *
     * @param exportStatus a {@link ExportStatus#isCompleted() completed} status.
     * @return a future of log stream.
     */
    @javax.validation.constraints.NotNull
    @NotNull
    CompletableFuture<InputStream> readExported(
            @javax.validation.Valid @javax.validation.constraints.NotNull
            @Valid @NotNull final ExportStatus exportStatus);

    /**
     * Downloads a completely exported log to a file.
     *
     * @param exportStatus a {@link ExportStatus#isCompleted() completed} log export status.
     * @param pathSupplier a supplier for the path to which the exported log is downloaded.
     * @return a future of a path supplied by the {@code pathSupplier}.
     */
    @javax.validation.constraints.NotNull
    @NotNull
    CompletableFuture<Path> downloadExported(
            @javax.validation.Valid @javax.validation.constraints.NotNull
            @Valid @NotNull final ExportStatus exportStatus,
            @javax.validation.constraints.NotNull
            @NotNull final Supplier<? extends Path> pathSupplier);

    /**
     * Downloads a completely exported log to a file and applies a readable byte channel to specified function.
     *
     * @param exportStatus    a {@link ExportStatus#isCompleted() completed} log export status.
     * @param channelFunction a function to which the file is applied.
     * @return a future of desired result.
     */
    @javax.validation.constraints.NotNull
    @NotNull <R> CompletableFuture<R> downloadExportedAndRead(
            @javax.validation.Valid @javax.validation.constraints.NotNull
            @Valid @NotNull final ExportStatus exportStatus,
            @javax.validation.constraints.NotNull
            @NotNull final Function<? super ReadableByteChannel, ? extends R> channelFunction);

    /**
     * Downloads a completely exported log to a file and applies a stream of lines to specified function.
     *
     * @param exportStatus   a {@link ExportStatus#isCompleted() completed} log export status.
     * @param streamFunction a function to which the line stream is applied.
     * @return a future of the result of the {@code function}.
     */
    @javax.validation.constraints.NotNull
    @NotNull <R> CompletableFuture<R> downloadExportedAndReadLines(
            @javax.validation.Valid @javax.validation.constraints.NotNull
            @Valid @NotNull final ExportStatus exportStatus,
            @NotNull final Function<? super Stream<String>, ? extends R> streamFunction);
}
