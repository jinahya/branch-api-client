package com.github.jinahya.branch.api.client;

import com.github.jinahya.branch.api.client.BranchApiClientUtilities.Jackson;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.IntPredicate;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public abstract class AbstractClient {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(16L);

    private static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(4L);

    protected static <T> Function<HttpResponse<T>, T> checkStatusCode(
            final IntPredicate predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        return r -> {
            final int statusCode = r.statusCode();
            if (!predicate.test(statusCode)) {
                throw new RuntimeException("unexpected status code: " + statusCode);
            }
            return r.body();
        };
    }

    protected static <T> Function<HttpResponse<T>, T> check200() {
        return checkStatusCode(v -> v == 200);
    }

    protected static <U, T> CompletableFuture<T> sendAsync(
            final HttpClient httpClient, final HttpRequest httpRequest, final HttpResponse.BodyHandler<U> bodyHandler,
            final Class<U> bodyClass, final Class<T> valueClass) {
        Objects.requireNonNull(httpClient, "httpClient is null");
        Objects.requireNonNull(httpRequest, "httpRequest is null");
        Objects.requireNonNull(bodyClass, "bodyClass is null");
        Objects.requireNonNull(bodyClass, "bodyClass is null");
        Objects.requireNonNull(valueClass, "valueClass is null");
        return httpClient.sendAsync(httpRequest, bodyHandler)
                .thenApply(check200())
                .thenApply(b -> Jackson.readValue(valueClass, new Class<?>[]{bodyClass}, b));
    }

    protected static <T> CompletableFuture<T> sendAsync(
            final HttpClient httpClient, final HttpRequest httpRequest, final Class<T> valueType) {
        return sendAsync(httpClient, httpRequest, BodyHandlers.ofString(), String.class, valueType);
    }

    @Override
    public String toString() {
        return super.toString() + '{'
               + "timeout=" + timeout
               + ",connectTimeout=" + connectTimeout
               + '}';
    }

    @AssertTrue
    private boolean isTimeoutPositive() {
        if (timeout == null) {
            return true;
        }
        return !timeout.isNegative() && !timeout.isZero();
    }

    @AssertTrue
    private boolean isConnectTimeoutPositive() {
        if (connectTimeout == null) {
            return true;
        }
        return !connectTimeout.isNegative() && !connectTimeout.isZero();
    }

    /**
     * Returns a value for {@link java.net.http.HttpRequest.Builder#timeout(Duration)}.
     *
     * @return a value for {@link java.net.http.HttpRequest.Builder#timeout(Duration)}.
     */
    protected Duration timeout() {
        return Optional.ofNullable(timeout).orElse(DEFAULT_TIMEOUT);
    }

    /**
     * Returns a value for {@link HttpClient.Builder#connectTimeout(Duration)}.
     *
     * @return a value for {@link HttpClient.Builder#connectTimeout(Duration)}.
     */
    protected Duration connectTimeout() {
        return Optional.ofNullable(connectTimeout).orElse(DEFAULT_CONNECT_TIMEOUT);
    }

    @javax.validation.constraints.NotNull
    @NotNull
    @Setter
    @Getter
    @Builder.Default
    private Duration timeout = DEFAULT_TIMEOUT;

    @javax.validation.constraints.NotNull
    @NotNull
    @Setter
    @Getter
    @Builder.Default
    private Duration connectTimeout = DEFAULT_CONNECT_TIMEOUT;
}
