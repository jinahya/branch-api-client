package com.github.jinahya.branch.api.client;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.AssertTrue;
import java.time.Duration;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public abstract class AbstractClient {

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

    @Builder.Default
    protected final Duration timeout = Duration.ofSeconds(16L);

    @Builder.Default
    protected final Duration connectTimeout = Duration.ofSeconds(8L);
}
