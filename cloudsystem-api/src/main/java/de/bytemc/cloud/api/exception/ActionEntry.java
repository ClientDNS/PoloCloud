package de.bytemc.cloud.api.exception;

import de.bytemc.cloud.api.exception.matcher.Matcher;

public record ActionEntry(Matcher matcher,
                          ErrorAction action) {

}

