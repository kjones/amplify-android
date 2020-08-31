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

import com.amplifyframework.core.model.temporal.Temporal;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Implementation of a Temporal JSON serializers for the variables map using Gson.
 */
public final class TemporalSerializers {
    /**
     * Serializer of {@link Temporal.Date}, an extended ISO-8601 Date string, with an optional timezone offset.
     * <p>
     * https://docs.aws.amazon.com/appsync/latest/devguide/scalars.html
     */
    public static final class TemporalDateSerializer implements JsonSerializer<Temporal.Date> {
        @Override
        public JsonElement serialize(Temporal.Date date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(date.format());
        }
    }

    /**
     * Serializer of {@link Temporal.DateTime}, an extended ISO-8601 DateTime string.
     * Time zone offset is required.
     * <p>
     * https://docs.aws.amazon.com/appsync/latest/devguide/scalars.html
     */
    public static final class TemporalDateTimeSerializer implements JsonSerializer<Temporal.DateTime> {
        @Override
        public JsonElement serialize(Temporal.DateTime dateTime, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(dateTime.format());
        }
    }

    /**
     * Serializer of {@link Temporal.Time}, an extended ISO-8601 Time string, with an optional timezone offset.
     * <p>
     * https://docs.aws.amazon.com/appsync/latest/devguide/scalars.html
     */
    public static final class TemporalTimeSerializer implements JsonSerializer<Temporal.Time> {
        @Override
        public JsonElement serialize(Temporal.Time time, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(time.format());
        }
    }

    /**
     * Serializer of {@link Temporal.Timestamp}, an AppSync scalar type that represents
     * the number of seconds elapsed since 1970-01-01T00:00Z. Timestamps are serialized as numbers.
     * Negative values are also accepted and these represent the number of seconds till 1970-01-01T00:00Z.
     * <p>
     * https://docs.aws.amazon.com/appsync/latest/devguide/scalars.html
     */
    public static final class TemporalTimestampSerializer implements JsonSerializer<Temporal.Timestamp> {
        @Override
        public JsonElement serialize(Temporal.Timestamp timestamp, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(timestamp.getSecondsSinceEpoch());
        }
    }
}
