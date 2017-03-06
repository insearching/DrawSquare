package com.example.serhiihrabas.drawsquare;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.github.danielnilsson9.colorpickerview.dialog.ColorPickerDialogFragment;
import com.ogaclejapan.arclayout.ArcLayout;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ColorPickerDialogFragment.ColorPickerDialogListener, View.OnClickListener {

    private static final int FILL_COLOR_DIALOG = 1000;
    private static final int STROKE_COLOR_DIALOG = 2000;

    DrawSurfaceView surfaceView;
    FloatingActionButton instrumentFab;
    FloatingActionButton propertiesFab;

    View instrumentsLayout;
    View propertiesLayout;
    ArcLayout instruments;
    ArcLayout properties;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.a_main);

        surfaceView = (DrawSurfaceView) findViewById(R.id.surface);
        instrumentsLayout = findViewById(R.id.instruments_menu_layout);
        propertiesLayout = findViewById(R.id.properties_menu_layout);
        instruments = (ArcLayout) findViewById(R.id.instruments_layout);
        properties = (ArcLayout) findViewById(R.id.properties_layout);
        instrumentFab = (FloatingActionButton) findViewById(R.id.instrument_fab);
        propertiesFab = (FloatingActionButton) findViewById(R.id.properties_fab);

        instrumentFab.setOnClickListener(this);
        propertiesFab.setOnClickListener(this);
        findViewById(R.id.circle).setOnClickListener(this);
        findViewById(R.id.rect).setOnClickListener(this);
        findViewById(R.id.line).setOnClickListener(this);
        findViewById(R.id.pencil).setOnClickListener(this);


        findViewById(R.id.stroke_color).setOnClickListener(this);
        findViewById(R.id.fill_color).setOnClickListener(this);
        findViewById(R.id.stroke_width).setOnClickListener(this);

        switch (surfaceView.getCurrentInstrument()) {
            case CIRCLE:
                instrumentFab.setImageResource(R.drawable.ic_action_circle);
                break;
            case RECTANGLE:
                instrumentFab.setImageResource(R.drawable.ic_action_square);
                break;
            case LINE:
                instrumentFab.setImageResource(R.drawable.ic_action_line);
                break;
            case PENCIL:
                instrumentFab.setImageResource(R.drawable.ic_action_pencil);
                break;
        }
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        if (dialogId == FILL_COLOR_DIALOG)
            surfaceView.setFillColor(color);
        else if (dialogId == STROKE_COLOR_DIALOG)
            surfaceView.setStrokeColor(color);
    }

    @Override
    public void onDialogDismissed(int dialogId) {
    }

    @SuppressWarnings("NewApi")
    private void showMenu(int viewId) {
        final ArcLayout arcLayout = (viewId == R.id.instrument_fab ? instruments : properties);
        final View layout = (viewId == R.id.instrument_fab ? instrumentsLayout : propertiesLayout);
        layout.setVisibility(View.VISIBLE);
        List<Animator> animList = new ArrayList<>();

        for (int i = 0, len = arcLayout.getChildCount(); i < len; i++) {
            animList.add(createShowItemAnimator(arcLayout.getChildAt(i), (viewId == R.id.instrument_fab ? instrumentFab : propertiesFab)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new OvershootInterpolator());
        animSet.playTogether(animList);
        animSet.start();
    }

    @SuppressWarnings("NewApi")
    private void hideMenu(int viewId) {
        final ArcLayout arcLayout = (viewId == R.id.instrument_fab ? instruments : properties);
        List<Animator> animList = new ArrayList<>();

        for (int i = arcLayout.getChildCount() - 1; i >= 0; i--) {
            animList.add(createHideItemAnimator(arcLayout.getChildAt(i), (viewId == R.id.instrument_fab ? instrumentFab : propertiesFab)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new AnticipateInterpolator());
        animSet.playTogether(animList);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                instrumentsLayout.setVisibility(View.INVISIBLE);
                propertiesLayout.setVisibility(View.INVISIBLE);
            }
        });
        animSet.start();

    }

    private Animator createShowItemAnimator(View item, FloatingActionButton parent) {

        float dx = parent.getX() - item.getX();
        float dy = parent.getY() - item.getY();

        item.setRotation(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(0f, 720f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f)
        );

        return anim;
    }

    private Animator createHideItemAnimator(final View item, FloatingActionButton parent) {
        float dx = parent.getX() - item.getX();
        float dy = parent.getY() - item.getY();

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(720f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy)
        );

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });

        return anim;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.instrument_fab:
                if (v.isSelected()) {
                    hideMenu(v.getId());
                } else {
                    showMenu(v.getId());
                }
                v.setSelected(!v.isSelected());
                break;

            case R.id.circle:
                instrumentFab.setImageResource(R.drawable.ic_action_circle);

                surfaceView.setCurrentInstrument(DrawSurfaceView.Instrument.CIRCLE);
                hideMenu(v.getId());
                break;
            case R.id.rect:
                instrumentFab.setImageResource(R.drawable.ic_action_square);

                surfaceView.setCurrentInstrument(DrawSurfaceView.Instrument.RECTANGLE);
                hideMenu(v.getId());
                break;
            case R.id.line:
                instrumentFab.setImageResource(R.drawable.ic_action_line);

                surfaceView.setCurrentInstrument(DrawSurfaceView.Instrument.LINE);
                hideMenu(v.getId());
                break;
            case R.id.pencil:
                instrumentFab.setImageResource(R.drawable.ic_action_pencil);

                surfaceView.setCurrentInstrument(DrawSurfaceView.Instrument.PENCIL);
                hideMenu(v.getId());
                break;


            case R.id.properties_fab:
                if (v.isSelected()) {
                    hideMenu(v.getId());
                } else {
                    showMenu(v.getId());
                }
                v.setSelected(!v.isSelected());
                break;
            case R.id.stroke_color:
                ColorPickerDialogFragment
                        .newInstance(STROKE_COLOR_DIALOG, "Stroke color", null, surfaceView.getStrokeColor(), true)
                        .show(getFragmentManager(), "colorPicker");
                break;
            case R.id.fill_color:
                ColorPickerDialogFragment
                        .newInstance(FILL_COLOR_DIALOG, "Fill color", null, surfaceView.getFillColor(), true)
                        .show(getFragmentManager(), "colorPicker");
                break;
            case R.id.stroke_width:

                LayoutInflater inflater = LayoutInflater.from(this);
                View dialogView = inflater.inflate(R.layout.dialog_stroke_width, null);
                final DiscreteSeekBar seekBar = (DiscreteSeekBar)dialogView.findViewById(R.id.seek_bar);
                seekBar.setProgress(surfaceView.getStrokeWidth());
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Stroke width")
                        .setView(dialogView)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                surfaceView.setStrokeWidth(seekBar.getProgress());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
                break;
        }

    }
}
