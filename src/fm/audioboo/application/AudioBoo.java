/**
 * This file is part of AudioBoo, an android program for audio blogging.
 * Copyright (C) 2009 BestBefore Media Ltd. All rights reserved.
 *
 * Author: Jens Finkhaeuser <jens@finkhaeuser.de>
 *
 * $Id$
 **/

package fm.audioboo.application;

import android.app.TabActivity;

import android.os.Bundle;

import android.content.Intent;

import android.content.res.Configuration;
import android.content.res.TypedArray;

import android.widget.TabHost;

import android.app.Dialog;

import android.util.Log;

/**
 * Main Activity. Does little more than set up the Tabs that contain the
 * other Activities.
 **/
public class AudioBoo extends TabActivity
{
  /***************************************************************************
   * Private constants
   **/
  // Log ID
  private static final String LTAG  = "AudioBoo";

  // Classes for the Intents used to launch Tab contents.
  // XXX Indices need to correspond with the "main_tab_labels" array in
  //     localized.xml and the "main_tab_drawables" array in arrays.xml
  private static final Class TAB_CONTENT_CLASSES[] = {
    RecentBoosActivity.class,
    RecordActivity.class,
    AccountActivity.class,
  };

  // Dialog IDs.
  private static final int DIALOG_GPS_SETTINGS    = Globals.DIALOG_GPS_SETTINGS;


  /***************************************************************************
   * Implementation
   **/
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    // Load resources describing the tabs.
    String[] labels = getResources().getStringArray(R.array.main_tab_labels);
    TypedArray drawables = getResources().obtainTypedArray(
        R.array.main_tab_drawables);

    if (labels.length != drawables.length()
        || labels.length != TAB_CONTENT_CLASSES.length)
    {
      Log.e(LTAG, "Programming error: differing numbers of tab labels, drawables "
          + "and classes found.");
      return;
    }

    // Create tabs.
    TabHost host = getTabHost();
    for (int i = 0 ; i < labels.length ; ++i) {
      host.addTab(host.newTabSpec("tab" + i)
          .setIndicator(labels[i], drawables.getDrawable(i))
          .setContent(
            new Intent(this, TAB_CONTENT_CLASSES[i])
          )
        );
    }
    drawables.recycle();

    setDefaultTab(0);
  }



  @Override
  public void onStart()
  {
    super.onStart();

    // Start listening to location updates, if that's required.
    if (!Globals.get().startLocationUpdates()) {
      showDialog(DIALOG_GPS_SETTINGS);
    }
  }



  @Override
  public void onConfigurationChanged(Configuration config)
  {
    // Ignore when the keyboard opens to the extent that we don't fetch boos
    // again. XXX We need to ignore this in the parent activity so the child
    // actvities don't get restarted. Ignoring in the child activities is also
    // required.
    super.onConfigurationChanged(config);
  }



  @Override
  public void onStop()
  {
    super.onStop();

    Globals.get().mPlayer.stopPlaying();
    Globals.get().stopLocationUpdates();
  }



  protected Dialog onCreateDialog(int id)
  {
    Dialog dialog = null;

    switch (id) {
      case DIALOG_GPS_SETTINGS:
        dialog = Globals.get().createDialog(this, id);
        break;
    }

    return dialog;
  }
}