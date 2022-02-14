package com.github.jinahya.branch.api.client;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public abstract class AbstractClient {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(16L);

    private static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(4L);

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
