package com.github.jinahya.branch.api.client.export.daily;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class DailyExportsClient {

    @javax.validation.constraints.NotBlank
    @NotBlank
    @ToString.Exclude
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private String branchKey;

    @javax.validation.constraints.NotBlank
    @NotBlank
    @ToString.Exclude
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private String branchSecret;
}
