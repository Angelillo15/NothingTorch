package com.nookure.ntorch.service;

import android.content.Intent;
import android.os.IBinder;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.nothing.ketchum.GlyphManager;

import org.jetbrains.annotations.NotNull;

public class TorchTileService extends TileService {
  private GlyphService glyphService;

  @Override
  public void onTileAdded() {
    if (getSharedPreferences("state", MODE_PRIVATE).getBoolean("enabled", false)) {
      getQsTile().setState(Tile.STATE_ACTIVE);
    } else {
      getQsTile().setState(Tile.STATE_INACTIVE);
    }

    getQsTile().updateTile();
    loadState(getQsTile());
  }

  @Override
  public void onStartListening() {
    if (getSharedPreferences("state", MODE_PRIVATE).getBoolean("enabled", false)) {
      getQsTile().setState(Tile.STATE_ACTIVE);
    } else {
      getQsTile().setState(Tile.STATE_INACTIVE);
    }

    getQsTile().updateTile();
    loadState(getQsTile());
  }

  @Override
  public IBinder onBind(Intent intent) {
    glyphService = new GlyphService(GlyphManager.getInstance(this));
    return super.onBind(intent);
  }

  @Override
  public void onClick() {
    Tile tile = getQsTile();
    tile.setState(tile.getState() == Tile.STATE_ACTIVE ? Tile.STATE_INACTIVE : Tile.STATE_ACTIVE);
    tile.updateTile();

    loadState(tile);
  }

  public void loadState(@NotNull final Tile tile) {
    glyphService.toggleFrame(+
            tile.getState() == Tile.STATE_ACTIVE, glyphService.getGlyphFrame(),
        getSharedPreferences("state", MODE_PRIVATE),
        this
    );
  }
}
