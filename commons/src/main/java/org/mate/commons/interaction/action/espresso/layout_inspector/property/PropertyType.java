/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mate.commons.interaction.action.espresso.layout_inspector.property;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Set;
import java.util.function.IntFunction;

/**
 * The property type as determined from inspection.
 */
public class PropertyType {
    private final String mName;
    private final int mAttributeId;
    private final int mPropertyId;
    private final ValueType mType;
    private IntFunction<String> mEnumMapping;
    private IntFunction<Set<String>> mFlagMapping;

    public PropertyType(
            @NonNull String name, int attributeId, int propertyId, @NonNull ValueType type) {
        mName = name;
        mAttributeId = attributeId;
        mPropertyId = propertyId;
        mType = type;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    public int getAttributeId() {
        return mAttributeId;
    }

    @SuppressWarnings("unused")
    public int getPropertyId() {
        return mPropertyId;
    }

    @NonNull
    public ValueType getType() {
        return mType;
    }

    public void setEnumMapping(@NonNull IntFunction<String> enumMapping) {
        mEnumMapping = enumMapping;
    }

    @Nullable
    public IntFunction<String> getEnumMapping() {
        return mEnumMapping;
    }

    public void setFlagMapping(@NonNull IntFunction<Set<String>> flagMapping) {
        mFlagMapping = flagMapping;
    }

    @Nullable
    public IntFunction<Set<String>> getFlagMapping() {
        return mFlagMapping;
    }
}
