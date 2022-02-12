package com.github.jinahya.branch.api.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse;
import java.util.function.Supplier;

// https://stackoverflow.com/q/57629401/330457
class JsonBodyHandler<T>
        implements HttpResponse.BodyHandler<Supplier<? extends T>> {

    @Override
    public HttpResponse.BodySubscriber<Supplier<? extends T>> apply(final HttpResponse.ResponseInfo responseInfo) {
        return HttpResponse.BodySubscribers.mapping(
                HttpResponse.BodySubscribers.ofInputStream(),
                s -> () -> {
                    try (s) {
                        return new ObjectMapper().readValue(s, new TypeReference<>() {
                        });
                    } catch (final IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }
}
