package com.nookure.ntorch.service;

import static androidx.constraintlayout.widget.StateSet.TAG;

import android.content.ComponentName;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.nothing.ketchum.Common;
import com.nothing.ketchum.GlyphException;
import com.nothing.ketchum.GlyphFrame;
import com.nothing.ketchum.GlyphManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GlyphService {
  public static final String INTENT_ID = "com.nookure.ntorch.TOGGLE";

  private static GlyphService INSTANCE = null;
  private final GlyphManager glyphManager;
  private GlyphManager.Callback callback;
  private GlyphFrame glyphFrame;

  public GlyphService(
      @NotNull final GlyphManager manager
  ) {
    glyphManager = manager;
    init();
    glyphManager.init(callback);

    INSTANCE = this;
  }

  public static GlyphService getInstance() {
    return INSTANCE;
  }

  private void init() {
    callback = new GlyphManager.Callback() {
      @Override
      public void onServiceConnected(ComponentName componentName) {
        if (Common.is20111()) glyphManager.register(Common.DEVICE_20111);
        if (Common.is22111()) glyphManager.register(Common.DEVICE_22111);
        if (Common.is23111()) glyphManager.register(Common.DEVICE_23111);
        try {
          glyphManager.openSession();
          Log.i(TAG, "onServiceConnected: " + glyphManager);
        } catch (GlyphException e) {
          Log.e(TAG, "onServiceConnected: " + e.getMessage());
        }
      }

      @Override
      public void onServiceDisconnected(ComponentName componentName) {
        try {
          glyphManager.closeSession();
        } catch (GlyphException e) {
          Log.e(TAG, "onServiceDisconnected: " + e.getMessage());
        }
      }
    };
  }

  public GlyphManager getGlyphManager() {
    return glyphManager;
  }

  public void toggleFrame(
      boolean state,
      @NotNull final GlyphFrame glyphFrame,
      @Nullable final SharedPreferences preferences,
      @Nullable final ContextWrapper contextWrapper
  ) {
    try {
      glyphManager.openSession();

      if (state) {
        glyphManager.toggle(glyphFrame);
      } else {
        glyphManager.turnOff();
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    if (preferences != null) {
      preferences.edit().putBoolean("enabled", state).apply();
    }

    if (contextWrapper != null) {
      contextWrapper.sendBroadcast(new Intent(INTENT_ID).putExtra("state", state));
    }
  }

  public void toggleFrame(boolean state, @NotNull final GlyphFrame glyphFrame) {
    toggleFrame(state, glyphFrame, null, null);
  }

  public GlyphFrame getGlyphFrame() {
    if (glyphFrame == null) {
      glyphFrame = glyphManager.getGlyphFrameBuilder()
          .buildChannelA()
          .buildChannelB()
          .buildChannelC()
          .buildChannelD()
          .buildChannelE()
          .build();
    }

    return glyphFrame;
  }
}
