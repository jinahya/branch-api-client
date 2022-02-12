package com.github.jinahya.branch.api.client.export.custom.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
public class ExportJobStatus
        extends AbstractCustomExportMessage {

    @Override
    public String toString() {
        return super.toString() + '{'
               + "code=" + code
               + ",status=" + status
               + ",linesExported=" + linesExported
               + ",responseUrl=" + responseUrl
               + '}';
    }

    @javax.validation.constraints.AssertTrue
    @AssertTrue
    public boolean isCodeSuccessful() {
        return code == 200;
    }

    @javax.validation.constraints.AssertTrue
    @AssertTrue
    public boolean isCompleted() {
        return "complete".equals(status);
    }

    private int code;

    @javax.validation.constraints.NotBlank
    @NotBlank
    private String status;

    @javax.validation.constraints.PositiveOrZero
    @PositiveOrZero
    private int linesExported;

    @javax.validation.constraints.NotBlank
    @NotBlank
    private String responseUrl;
}
