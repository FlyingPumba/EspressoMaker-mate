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

package org.mate.commons.interaction.action.espresso.layout_inspector.common;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class StringTable {
    private final Map<String, Integer> mStringMap = new HashMap<>();

    public int generateStringId(@Nullable String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        Integer id = mStringMap.get(str);
        if (id != null) {
            return id;
        }
        int newId = mStringMap.size() + 1;
        mStringMap.put(str, newId);
        return newId;
    }

    @NonNull
    public Iterable<Map.Entry<String, Integer>> entries() {
        return mStringMap.entrySet();
    }

    public void clear() {
        mStringMap.clear();
    }
}
