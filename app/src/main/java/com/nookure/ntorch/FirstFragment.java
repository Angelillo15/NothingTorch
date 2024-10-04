package com.nookure.ntorch;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.nookure.ntorch.databinding.FragmentFirstBinding;
import com.nookure.ntorch.service.GlyphService;
import com.nothing.ketchum.GlyphManager;

import org.jetbrains.annotations.NotNull;

public class FirstFragment extends Fragment {
  private FragmentFirstBinding binding;
  private GlyphService glyphService;
  private Activity activity;
  private BroadcastReceiver receiver;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState
  ) {
    binding = FragmentFirstBinding.inflate(inflater, container, false);
    glyphService = new GlyphService(GlyphManager.getInstance(getActivity()));
    activity = getActivity();
    requireActivity();
    receiver = new FirstFragmentReceiver(this);
    activity.registerReceiver(receiver, new IntentFilter(GlyphService.INTENT_ID), Context.RECEIVER_NOT_EXPORTED);

    container.post(() -> {
      changeButtonStatus(activity.getSharedPreferences("state", Context.MODE_PRIVATE).getBoolean("enabled", false));
    });

    activity.getSharedPreferences("state", Context.MODE_PRIVATE).registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
      assert key != null;
      if (key.equals("enabled")) {
        changeButtonStatus(!sharedPreferences.getBoolean("enabled", false), false);
      }
    });

    return binding.getRoot();
  }

  @Override
  public void onDestroy() {
    activity.unregisterReceiver(receiver);
    super.onDestroy();
  }

  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    binding.button.setOnClickListener((click) -> {
      changeButtonStatus(activity.getSharedPreferences("state", Context.MODE_PRIVATE).getBoolean("enabled", false));
    });
  }

  private void changeButtonStatus(boolean enabled) {
    changeButtonStatus(enabled, true);
  }

  private void changeButtonStatus(boolean enabled, boolean glyphUpdate) {
    if (enabled) {
      if (glyphUpdate)
        glyphService.toggleFrame(false, glyphService.getGlyphFrame(), activity.getSharedPreferences("state", Context.MODE_PRIVATE), activity);
      binding.button.setBackgroundTintList(requireContext().getResources().getColorStateList(R.color.gray_black, null));
    } else {
      if (glyphUpdate)
        glyphService.toggleFrame(true, glyphService.getGlyphFrame(), activity.getSharedPreferences("state", Context.MODE_PRIVATE), activity);
      binding.button.setBackgroundTintList(requireContext().getResources().getColorStateList(R.color.red, null));
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

  public static class FirstFragmentReceiver extends BroadcastReceiver {
    private final FirstFragment fragment;

    public FirstFragmentReceiver(@NotNull final FirstFragment fragment) {
      this.fragment = fragment;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
      fragment.glyphService.toggleFrame(
          intent.getBooleanExtra("enabled", false),
          fragment.glyphService.getGlyphFrame()
      );
    }
  }
}