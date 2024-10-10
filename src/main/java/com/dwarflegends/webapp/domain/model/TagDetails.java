package com.dwarflegends.webapp.domain.model;

import java.util.Objects;

public record TagDetails(String pageTag, String path) {
    public TagDetails(String pageTag, String path) {
        this.pageTag = Objects.requireNonNull(pageTag);
        this.path = Objects.requireNonNull(path);
    }
}
