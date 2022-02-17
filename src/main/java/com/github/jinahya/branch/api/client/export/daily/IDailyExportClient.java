package com.github.jinahya.branch.api.client.export.daily;

import com.github.jinahya.branch.api.client.IClient;
import com.github.jinahya.branch.api.client.export.daily.message.ExportRequest;
import com.github.jinahya.branch.api.client.export.daily.message.ExportResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * A client interface for accessing <a href="https://help.branch.io/developers-hub/docs/daily-exports">Daily Exports &
 * API</a>.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://help.branch.io/developers-hub/docs/daily-exports">Daily Exports & API</a>
 */
public interface IDailyExportClient
        extends IClient {

    /**
     * Requests an export with specified value.
     *
     * @param exportRequest the request object.
     * @return a future of a http response.
     */
    @javax.validation.constraints.NotNull
    @NotNull
    CompletableFuture<ExportResponse> requestExport(
            @javax.validation.Valid @javax.validation.constraints.NotNull
            @Valid @NotNull final ExportRequest exportRequest);
}
