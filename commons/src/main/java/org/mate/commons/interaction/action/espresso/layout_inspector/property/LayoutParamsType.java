package org.mate.commons.interaction.action.espresso.layout_inspector.property;

import android.os.Build;
import android.view.ViewGroup.LayoutParams;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.PropertyReader;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.List;

public class LayoutParamsType {
    private final String mNodeName;
    private final String mJavaName;
    private final List<PropertyType> mProperties;
    private final List<InspectionCompanion<LayoutParams>> mInspectionCompanions;

    LayoutParamsType(
            @NonNull String nodeName,
            @NonNull String javaName,
            @NonNull List<PropertyType> properties,
            @NonNull List<InspectionCompanion<LayoutParams>> inspectionCompanions) {
        mNodeName = nodeName;
        mJavaName = javaName;
        mProperties = properties;
        mInspectionCompanions = inspectionCompanions;
    }

    @NonNull
    @SuppressWarnings("unused")
    public String getNodeName() {
        return mNodeName;
    }

    @NonNull
    @SuppressWarnings("unused")
    public String getJavaName() {
        return mJavaName;
    }

    @NonNull
    List<InspectionCompanion<LayoutParams>> getInspectionCompanions() {
        return mInspectionCompanions;
    }

    @NonNull
    List<PropertyType> getProperties() {
        return mProperties;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    void readProperties(
            @NonNull LayoutParams layoutParams, @NonNull PropertyReader propertyReader) {
        for (InspectionCompanion<LayoutParams> companion : mInspectionCompanions) {
            companion.readProperties(layoutParams, propertyReader);
        }
    }
}
