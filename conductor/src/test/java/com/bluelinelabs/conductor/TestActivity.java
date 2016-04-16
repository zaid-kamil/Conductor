package com.bluelinelabs.conductor;

import android.app.Activity;

public class TestActivity extends Activity {

    public boolean isChangingConfigurations = false;

    @Override
    public boolean isChangingConfigurations() {
        return isChangingConfigurations;
    }

}
