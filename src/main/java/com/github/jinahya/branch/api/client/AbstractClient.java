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
import java.lang.System.Logger.Level;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.IntPredicate;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public abstract class AbstractClient
        implements IClient {

    private static final System.Logger log = System.getLogger(AbstractClient.class.getName());

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(16L);

    private static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(4L);

    /**
     * Returns a function tests the {@link HttpResponse#statusCode() status code} of a {@link HttpResponse} against
     * specified predicate and throws a runtime exception when the test fails.
     *
     * @param statusCodePredicate a predicate tests a {@link HttpResponse#statusCode() status code}.
     * @param <T>                 response body type parameter
     * @return a function.
     */
    protected static <T> Function<HttpResponse<T>, T> checkStatusCode(final IntPredicate statusCodePredicate) {
        Objects.requireNonNull(statusCodePredicate, "statusCodePredicate is null");
        return r -> {
            final var statusCode = r.statusCode();
            if (!statusCodePredicate.test(statusCode)) {
                log.log(Level.ERROR,
                        () -> String.format("unsuccessful status code: %1$d, request: %2$s, body: %3$s",
                                               statusCode, r.request(), r.body()));
                throw new RuntimeException("unsuccessful status code: " + statusCode);
            }
            return r.body();
        };
    }

    protected static <T> Function<HttpResponse<T>, T> checkStatusCode200() {
        return checkStatusCode(v -> v == 200);
    }

    protected static <T, U> CompletableFuture<U> sendAsyncAndReadJsonValue(
            final HttpClient httpClient, final HttpRequest httpRequest, final BodyHandler<T> bodyHandler,
            final Class<T> bodyClass, final Class<U> valueClass) {
        Objects.requireNonNull(httpClient, "httpClient is null");
        Objects.requireNonNull(httpRequest, "httpRequest is null");
        Objects.requireNonNull(bodyClass, "bodyClass is null");
        Objects.requireNonNull(bodyClass, "bodyClass is null");
        Objects.requireNonNull(valueClass, "valueClass is null");
        return httpClient.sendAsync(httpRequest, bodyHandler)
                .thenApply(checkStatusCode200())
                .thenApply(b -> Jackson.readValue(valueClass, new Class<?>[]{bodyClass}, b));
    }

    protected static <T> CompletableFuture<T> sendAsyncAndReadJsonValueFromStringBody(
            final HttpClient httpClient, final HttpRequest httpRequest, final Class<T> valueType) {
        return sendAsyncAndReadJsonValue(httpClient, httpRequest, BodyHandlers.ofString(), String.class, valueType);
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
        if (timeout == null) {
            return DEFAULT_TIMEOUT;
        }
        return timeout;
    }

    /**
     * Returns a value for {@link HttpClient.Builder#connectTimeout(Duration)}.
     *
     * @return a value for {@link HttpClient.Builder#connectTimeout(Duration)}.
     */
    protected Duration connectTimeout() {
        if (connectTimeout == null) {
            return DEFAULT_CONNECT_TIMEOUT;
        }
        return connectTimeout;
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
