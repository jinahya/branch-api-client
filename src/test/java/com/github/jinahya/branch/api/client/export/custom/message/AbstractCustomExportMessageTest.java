package com.github.jinahya.branch.api.client.export.custom.message;

import com.github.jinahya.branch.api.client.message.AbstractMessageTest;

abstract class AbstractCustomExportMessageTest<T extends AbstractCustomExportMessage>
        extends AbstractMessageTest<T> {

    protected AbstractCustomExportMessageTest(final Class<T> messageClass) {
        super(messageClass);
    }
}