package com.github.jinahya.branch.api.client;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class BranchApiClientConstants {

    public static final class Http {

        public static final String HEADER_CONTENT_TYPE = "content-type";

        public static final String HEADER_ACCEPT = "accept";

        public static final String HEADER_ACCESS_TOKEN = "access-token";

        public static final String MEDIA_TYPE_APPLICATION_JSON = "application/json";

        private Http() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

    public static final class BeanValidation {

        /**
         * A regular expression for matching non-empty strings.
         */
        // https://stackoverflow.com/a/4451730/330457
        public static final String PATTERN_REGEXP_NOT_BLANK = "^(?=\\s*\\S).*$";

        private BeanValidation() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

    public static final class Jackson {

        public static final JsonFactory JSON_FACTORY = JsonFactory.builder()
                .build();

        public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(JSON_FACTORY);

        static {
            OBJECT_MAPPER.registerModule(new JavaTimeModule());
            OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }

        public static final TypeFactory TYPE_FACTORY = TypeFactory.defaultInstance();

        private Jackson() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

    private BranchApiClientConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
