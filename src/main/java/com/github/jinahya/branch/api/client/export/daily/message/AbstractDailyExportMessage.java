package com.github.jinahya.branch.api.client.export.daily.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jinahya.branch.api.client.message.AbstractMessage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public abstract class AbstractDailyExportMessage
        extends AbstractMessage {

    private static final long serialVersionUID = 5488974481743583089L;

    @Override
    public String toString() {
        return super.toString() + '{'
               + '}';
    }
}
