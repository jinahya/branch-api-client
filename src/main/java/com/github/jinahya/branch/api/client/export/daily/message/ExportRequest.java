package com.github.jinahya.branch.api.client.export.daily.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class ExportRequest
        extends AbstractDailyExportMessage {

    private static final long serialVersionUID = 7100626022290791953L;

    @Override
    public String toString() {
        return super.toString() + '{'
               + "exportDate=" + exportDate
               + '}';
    }

    @javax.validation.constraints.NotBlank
    @NotBlank
    @ToString.Exclude
    private String branchKey;

    @javax.validation.constraints.NotBlank
    @NotBlank
    @ToString.Exclude
    private String branchSecret;

    @javax.validation.constraints.NotNull
    @NotNull
    private LocalDate exportDate;
}
