package com.example.serhiihrabas.drawsquare;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.github.danielnilsson9.colorpickerview.dialog.ColorPickerDialogFragment;
import com.ogaclejapan.arclayout.ArcLayout;
import com.rustamg.filedialogs.FileDialog;
import com.rustamg.filedialogs.OpenFileDialog;
import com.rustamg.filedialogs.SaveFileDialog;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ColorPickerDialogFragment.ColorPickerDialogListener, View.OnClickListener, FileDialog.OnFileSelectedListener {

    private static final int FILL_COLOR_DIALOG = 1000;
    private static final int STROKE_COLOR_DIALOG = 2000;

    private static final int WRITE_EXTERNAL_STORAGE = 1000;
    private static final int READ_EXTERNAL_STORAGE = 2000;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    DrawSurfaceView surfaceView;
    FloatingActionButton instrumentFab;
    FloatingActionButton propertiesFab;
    FloatingActionButton mainFab;

    View instrumentsLayout;
    View propertiesLayout;
    View mainLayout;
    ArcLayout instruments;
    ArcLayout properties;
    ArcLayout main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.a_main);

        surfaceView = (DrawSurfaceView) findViewById(R.id.surface);
        instrumentsLayout = findViewById(R.id.instruments_menu_layout);
        propertiesLayout = findViewById(R.id.properties_menu_layout);
        mainLayout = findViewById(R.id.main_menu_layout);

        instruments = (ArcLayout) findViewById(R.id.instruments_layout);
        properties = (ArcLayout) findViewById(R.id.properties_layout);
        main = (ArcLayout) findViewById(R.id.main_layout);

        instrumentFab = (FloatingActionButton) findViewById(R.id.instrument_fab);
        propertiesFab = (FloatingActionButton) findViewById(R.id.properties_fab);
        mainFab = (FloatingActionButton) findViewById(R.id.main_fab);

        instrumentFab.setOnClickListener(this);
        propertiesFab.setOnClickListener(this);
        mainFab.setOnClickListener(this);
        findViewById(R.id.circle).setOnClickListener(this);
        findViewById(R.id.rect).setOnClickListener(this);
        findViewById(R.id.line).setOnClickListener(this);
        findViewById(R.id.pencil).setOnClickListener(this);

        findViewById(R.id.stroke_color).setOnClickListener(this);
        findViewById(R.id.fill_color).setOnClickListener(this);
        findViewById(R.id.stroke_width).setOnClickListener(this);


        findViewById(R.id.save_fab).setOnClickListener(this);
        findViewById(R.id.load_fab).setOnClickListener(this);
        findViewById(R.id.undo).setOnClickListener(this);
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
        ArcLayout arcLayout;
        View layout;
        switch (viewId){
            case R.id.instrument_fab:
                arcLayout = instruments;
                layout = instrumentsLayout;
                break;
            case R.id.properties_fab:
                arcLayout = properties;
                layout = propertiesLayout;
                break;
            default:
                arcLayout = main;
                layout = mainLayout;
                break;
        }

        layout.setVisibility(View.VISIBLE);
        List<Animator> animList = new ArrayList<>();

        for (int i = 0, len = arcLayout.getChildCount(); i < len; i++) {
            animList.add(createShowItemAnimator(arcLayout.getChildAt(i), findViewById(viewId)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(300);
        animSet.setInterpolator(new OvershootInterpolator());
        animSet.playTogether(animList);
        animSet.start();
    }

    @SuppressWarnings("NewApi")
    private void hideMenu(int viewId) {
        ArcLayout arcLayout;
        switch (viewId){
            case R.id.instrument_fab:
                arcLayout = instruments;
                break;
            case R.id.properties_fab:
                arcLayout = properties;
                break;
            default:
                arcLayout = main;
                break;
        }

        List<Animator> animList = new ArrayList<>();

        for (int i = arcLayout.getChildCount() - 1; i >= 0; i--) {
            animList.add(createHideItemAnimator(arcLayout.getChildAt(i), findViewById(viewId)));
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
                mainLayout.setVisibility(View.INVISIBLE);
            }
        });
        animSet.start();

    }

    private Animator createShowItemAnimator(View item, View parent) {

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

    private Animator createHideItemAnimator(final View item, View parent) {
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
            case R.id.properties_fab:
            case R.id.main_fab:
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

            case R.id.new_fab:
                surfaceView.clearCanvas();
                break;
            case R.id.save_fab:
                int permissionCheckWrite = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if(permissionCheckWrite == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    save();
                }
                else{
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_STORAGE);
                }
                break;

            case R.id.load_fab:
                int permissionCheckLoad = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE);
                if(permissionCheckLoad == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    load();
                }
                else{
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            READ_EXTERNAL_STORAGE);
                }
                break;

            case R.id.undo:

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    save();
                } else {
                    findViewById(R.id.save_fab).setVisibility(View.GONE);
                }
                return;
            }
            case READ_EXTERNAL_STORAGE:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    load();
                } else {
                    findViewById(R.id.load_fab).setVisibility(View.GONE);
                }
                return;
            }
        }
    }

    private void save(){
        FileDialog dialog = new SaveFileDialog();
        Bundle args = new Bundle();
        args.putString(FileDialog.EXTENSION, ".png");
        dialog.setArguments(args);
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme);
        dialog.show(getSupportFragmentManager(), SaveFileDialog.class.getName());
    }

    private void load(){
        FileDialog dialog = new OpenFileDialog();
        Bundle args = new Bundle();
        args.putString(FileDialog.EXTENSION, "png");
        dialog.setArguments(args);
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme);
        dialog.show(getSupportFragmentManager(), OpenFileDialog.class.getName());
    }


    @Override
    public void onFileSelected(FileDialog dialog, File file) {
        if(dialog.getTag().equals(SaveFileDialog.class.getName())) {
            surfaceView.saveToFile(file.getAbsolutePath());
        }
        else if(dialog.getTag().equals(OpenFileDialog.class.getName())){
            Log.d(LOG_TAG, "OPENING FILE " + file.getAbsolutePath());
            Bitmap bMap = BitmapFactory.decodeFile(file.getAbsolutePath());
            surfaceView.loadBitmap(bMap);
//            Bitmap bMap = BitmapFactory.decodeFile(file);
//            Drawable d = new BitmapDrawable(bMap);
//            d.setBounds(left, top, right, bottom);
//            d.draw(canvas);
        }
        hideMenu(R.id.main_fab);

    }
}
