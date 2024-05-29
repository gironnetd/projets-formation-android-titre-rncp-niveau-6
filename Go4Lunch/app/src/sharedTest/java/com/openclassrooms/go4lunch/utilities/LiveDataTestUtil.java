package com.openclassrooms.go4lunch.utilities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LiveDataTestUtil {

    public static final Object getValue(@NotNull final LiveData liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);
        Observer observer = new Observer() {
            public void onChanged(@Nullable Object o) {
                data[0] = o;
                latch.countDown();
                liveData.removeObserver((Observer) this);
            }
        };
        liveData.observeForever((Observer) observer);
        latch.await(2L, TimeUnit.SECONDS);
        return data[0];
    }
}
