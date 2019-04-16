package com.romanpulov.wwire.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.romanpulov.wwire.R;

public class ModelLayout extends LinearLayout {
    private Context mContext;
    private ModelGLSurfaceView mModelGLSurfaceView;

    public ModelGLSurfaceView getModelGLSurfaceView() {
        return mModelGLSurfaceView;
    }

    public ModelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupView(context);
    }

    public ModelLayout(Context context) {
        super(context);
        setupView(context);
    }

    private void setupView(Context context) {
        mContext = context;
        inflate();
        mModelGLSurfaceView = findViewById(R.id.glsurfaceview);

        //setting up mode selector
        Spinner modeSelector = findViewById(R.id.modeselector);
        final String[] modeSelectorItems = {"NONE", "REVERT", "PAN", "ROTATE", "SCALE"};
        ArrayAdapter<String> modeSelectorAdapter = new ArrayAdapter<>(
                mContext, android.R.layout.simple_spinner_item, modeSelectorItems);
        modeSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSelector.setAdapter(modeSelectorAdapter);
        modeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                mModelGLSurfaceView.getModelRenderer().setHandlerMode(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    private void inflate() {
        LayoutInflater layoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.model_bg, this, true);
    }
}
