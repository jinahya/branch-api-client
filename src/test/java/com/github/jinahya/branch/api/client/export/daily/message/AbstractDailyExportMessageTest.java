package com.github.jinahya.branch.api.client.export.daily.message;

import java.util.Objects;

abstract class AbstractDailyExportMessageTest<T extends AbstractDailyExportMessage> {

    protected AbstractDailyExportMessageTest(final Class<T> messageClass) {
        super();
        this.messageClass = Objects.requireNonNull(messageClass, "messageClass is null");
    }

    protected final Class<T> messageClass;
}