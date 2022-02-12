package com.github.jinahya.branch.api.client.message;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public abstract class AbstractMessage {

    @Override
    public String toString() {
        return super.toString() + '{'
               + "unknownProperties=" + unknownProperties
               + '}';
    }

    @JsonAnyGetter
    public Map<String, Object> getUnknownProperties() {
        if (unknownProperties == null) {
            unknownProperties = new HashMap<>();
        }
        return unknownProperties;
    }

    @JsonAnySetter
    public void setUnknownProperties(final String name, final Object value) {
        getUnknownProperties().put(name, value);
    }

    private Map<String, Object> unknownProperties;
}
