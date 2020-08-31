/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amplifyframework.api.aws;

import com.amplifyframework.api.graphql.GraphQLRequest;
import com.amplifyframework.core.model.temporal.Temporal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Implementation of a GraphQL Request serializer for the variables map using Gson.
 */
public final class GsonVariablesSerializer implements GraphQLRequest.VariablesSerializer {
    private final Gson gson;

    /**
     * Creates new instance of GsonVariablesSerializer.
     */
    public GsonVariablesSerializer() {
        gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateSerializer())
            .registerTypeAdapter(Temporal.Timestamp.class, new TemporalSerializers.TemporalTimestampSerializer())
            .registerTypeAdapter(Temporal.Date.class, new TemporalSerializers.TemporalDateSerializer())
            .registerTypeAdapter(Temporal.DateTime.class, new TemporalSerializers.TemporalDateTimeSerializer())
            .registerTypeAdapter(Temporal.Time.class, new TemporalSerializers.TemporalTimeSerializer())
            .create();
    }

    @Override
    public String serialize(Map<String, Object> variables) {
        return gson.toJson(variables);
    }

    @Override
    public boolean equals(Object thatObject) {
        if (this == thatObject) {
            return true;
        }
        if (thatObject == null || getClass() != thatObject.getClass()) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    /**
     * Earlier versions of the model gen used to use Java's {@link Date} to represent all of the
     * temporal types. This led to challenges while trying to decode/encode the timezone,
     * among other things. The model gen will now spit out {@link Temporal.Date}, {@link Temporal.DateTime},
     * {@link Temporal.Time}, and {@link Temporal.Timestamp}, instead. This DateSerializer is left for
     * compat, until such a time as it can be safely removed (that is, when all models no longer
     * use a raw Date type.)
     */
    static class DateSerializer implements JsonSerializer<Date> {
        @Override
        public JsonElement serialize(Date date, Type typeOfSrc, JsonSerializationContext context) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return new JsonPrimitive(dateFormat.format(date));
        }
    }
}
