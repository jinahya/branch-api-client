package com.github.jinahya.branch.api.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
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

        /**
         * Applies an instance of {@link ObjectReader} to specified function and returns the result.
         *
         * @param function the function.
         * @param <R>      result type parameter
         * @return the result of the {@code function}.
         */
        public static <R> R applyObjectReader(final Function<? super ObjectReader, ? extends R> function) {
            Objects.requireNonNull(function, "function is null");
            return function.apply(OBJECT_MAPPER.reader());
        }

        public static <T> T readValue(final Class<T> rtype, final Class<?>[] ptypes, final Object... args) {
            Objects.requireNonNull(rtype, "rtype is null");
            Objects.requireNonNull(ptypes, "ptypes is null");
            Objects.requireNonNull(args, "args");
            return applyObjectReader(r -> {
                try {
                    final MethodType type = MethodType.methodType(Object.class, ptypes);
                    final var handle = MethodHandles.lookup().findVirtual(ObjectReader.class, "readValue", type);
                    final var result = handle.bindTo(r.forType(rtype)).invokeWithArguments(args);
                    return rtype.cast(result);
                } catch (final Throwable t) {
                    throw new RuntimeException(t);
                }
            });
        }

        public static <T> T readValue(final Class<T> rtype, final InputStream src) {
            Objects.requireNonNull(src, "src is null");
            return readValue(rtype, new Class<?>[]{InputStream.class}, src);
        }

        public static <T> T readValue(final Class<T> rtype, final String src) {
            Objects.requireNonNull(src, "src is null");
            return readValue(rtype, new Class<?>[]{String.class}, src);
        }

        /**
         * Applies an instance of {@link ObjectWriter} to specified function and returns the result.
         *
         * @param function the function.
         * @param <R>      result type parameter
         * @return the result of the {@code function}.
         */
        public static <R> R applyObjectWriter(final Function<? super ObjectWriter, ? extends R> function) {
            Objects.requireNonNull(function, "function is null");
            return function.apply(OBJECT_MAPPER.writer());
        }

        public static String writeValueAsString(final Object value) {
            return applyObjectWriter(w -> {
                try {
                    return w.writeValueAsString(value);
                } catch (final JsonProcessingException jpe) {
                    throw new UncheckedIOException(jpe);
                }
            });
        }

        private Jackson() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

    private BranchApiClientUtilities() {
        throw new AssertionError("instantiation is not allowed");
    }
}
