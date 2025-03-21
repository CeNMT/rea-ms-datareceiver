/**
 * Copyright (c) 2025 CNMT
 * All rights reserved.
 */

package com.cnmt.util;

public final class Const {
    public static final String CONTENT_FHIR_JSON = "application/fhir+json";

    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";

    private Const() throws IllegalAccessException {
        throw new IllegalAccessException("Wrong usage");
    }

}
