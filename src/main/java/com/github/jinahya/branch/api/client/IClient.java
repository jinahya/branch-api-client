package com.github.jinahya.branch.api.client;

import java.time.Duration;

public interface IClient {

    Duration getTimeout();

    void setTimeout(Duration timeout);

    Duration getConnectTimeout();

    void setConnectTimeout(Duration connectTimeout);
}
