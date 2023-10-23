package org.mate.commons.mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.view.View;

import androidx.test.espresso.Root;

public class MockWindowRoot {

    Root root;

    public MockWindowRoot() {
        root = mock(Root.class);
    }

    public Root getRoot() {
        return root;
    }

    public MockWindowRoot withDecorView(View decorView) {
        when(root.getDecorView()).thenReturn(decorView);
        return this;
    }
}
