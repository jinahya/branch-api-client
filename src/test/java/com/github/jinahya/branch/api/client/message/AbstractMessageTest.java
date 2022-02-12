package com.github.jinahya.branch.api.client.message;

import java.util.Objects;

public class AbstractMessageTest<T extends AbstractMessage> {

    protected AbstractMessageTest(final Class<T> messageClass) {
        super();
        this.messageClass = Objects.requireNonNull(messageClass, "messageClass is null");
    }

    protected final Class<T> messageClass;
}