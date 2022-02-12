package com.github.jinahya.branch.api.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.github.jinahya.branch.api.client.BranchApiClientConstants.Jackson.OBJECT_MAPPER;

public final class BranchApiClientUtilities {

    public static HttpRequest.BodyPublisher jsonBodyPublisher(final Object value) {
        Objects.requireNonNull(value, "value is null");
        try {
            return BodyPublishers.ofString(new ObjectMapper().writeValueAsString(value));
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    public static <T> HttpResponse.BodyHandler<Supplier<? extends T>> jsonBodyHandler() {
        return new JsonBodyHandler<>();
    }

    public static final class Jackson {

        public static <R> R applyObjectReader(final Function<? super ObjectReader, ? extends R> function) {
            Objects.requireNonNull(function, "function is null");
            return function.apply(OBJECT_MAPPER.reader());
        }

        public static <R> R applyObjectWriter(final Function<? super ObjectWriter, ? extends R> function) {
            Objects.requireNonNull(function, "function is null");
            return function.apply(OBJECT_MAPPER.writer());
        }

        private Jackson() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

    private BranchApiClientUtilities() {
        throw new AssertionError("instantiation is not allowed");
    }
}
