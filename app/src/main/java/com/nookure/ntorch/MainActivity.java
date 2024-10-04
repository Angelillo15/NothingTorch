package com.nookure.ntorch;

import static androidx.constraintlayout.widget.StateSet.TAG;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.nookure.ntorch.databinding.ActivityMainBinding;
import com.nookure.ntorch.service.GlyphService;
import com.nothing.ketchum.GlyphException;
import com.nothing.ketchum.GlyphManager;

public class MainActivity extends AppCompatActivity {
  private ActivityMainBinding binding;
  private GlyphManager glyphManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    new GlyphService(GlyphManager.getInstance(this));
    glyphManager = GlyphService.getInstance().getGlyphManager();

    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    setSupportActionBar(binding.toolbar);
  }

  @Override
  public void onDestroy() {
    try {
      glyphManager.closeSession();
    } catch (GlyphException e) {
      if (e.getMessage() != null)
        Log.e(TAG, e.getMessage());
    }
    glyphManager.unInit();
    super.onDestroy();
  }
}