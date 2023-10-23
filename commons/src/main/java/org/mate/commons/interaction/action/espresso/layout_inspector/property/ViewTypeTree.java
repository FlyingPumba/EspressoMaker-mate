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

import android.os.Build;
import android.view.View;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanionProvider;
import android.view.inspector.StaticInspectionCompanionProvider;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds a tree of {@link ViewType}s.
 *
 * <p>A caller can add a ViewType to the tree by calling {@link #typeOf}. All super classes of this
 * View up to android.view.View will be included.
 */
@RequiresApi(api = Build.VERSION_CODES.Q)
public class ViewTypeTree {
    private final InspectionCompanionProvider inspectionCompanionProvider =
            new StaticInspectionCompanionProvider();
    private final Map<Class<? extends View>, ViewType<? extends View>> typeMap = new HashMap<>();

    @NonNull
    @SuppressWarnings("unchecked")
    public <V extends View> ViewType<V> typeOf(@NonNull V view) {
        return typeOf((Class<V>) view.getClass());
    }

    @NonNull
    private <V extends View> ViewType<V> typeOf(@NonNull Class<V> viewClass) {
        return innerTypeOf(viewClass);
    }

    @NonNull
    private <V extends View> ViewType<V> innerTypeOf(@NonNull Class<V> viewClass) {
        @SuppressWarnings("unchecked")
        ViewType<V> type = (ViewType<V>) typeMap.get(viewClass);
        if (type != null) {
            return type;
        }

        InspectionCompanion<View> inspectionCompanion = loadInspectionCompanion(viewClass);
        @SuppressWarnings("unchecked")
        ViewType<? extends View> superType =
                !"android.view.View".equals(viewClass.getCanonicalName())
                        ? innerTypeOf((Class<? extends View>) viewClass.getSuperclass())
                        : null;
        List<InspectionCompanion<View>> companions = new ArrayList<>();
        if (superType != null) {
            companions.addAll(superType.getInspectionCompanions());
        }
        if (inspectionCompanion != null) {
            companions.add(inspectionCompanion);
        }

        List<PropertyType> properties = new ArrayList<>();
        String nodeName = viewClass.getSimpleName();
        if (superType != null) {
            properties.addAll(superType.getProperties());
        }
        if (inspectionCompanion != null) {
            PropertyTypeMapper mapper = new PropertyTypeMapper(properties);
            inspectionCompanion.mapProperties(mapper);
            properties = mapper.getProperties();
        }

        type = new ViewType<>(nodeName, viewClass.getCanonicalName(), properties, companions);
        typeMap.put(viewClass, type);
        return type;
    }

    private <V extends View> InspectionCompanion<View> loadInspectionCompanion(
            @NonNull Class<V> javaClass) {
        //noinspection unchecked
        return (InspectionCompanion<View>) inspectionCompanionProvider.provide(javaClass);
    }
}
