package com.romanpulov.wwire;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;


public class ModelLayout extends LinearLayout {
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private ModelGLSurfaceView mModelGLSurfaceView;
	private Spinner mModeSelector;
	
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
		mModelGLSurfaceView = (ModelGLSurfaceView)findViewById(R.id.glsurfaceview);
		
		//setting up mode selector
		mModeSelector = (Spinner)findViewById(R.id.modeselector);
		final String[] modeSelectorItems = {"NONE", "REVERT", "PAN", "ROTATE", "SCALE"};
		ArrayAdapter<String> modeSelectorAdapter = new ArrayAdapter<String>(
				mContext, android.R.layout.simple_spinner_item, modeSelectorItems);
		modeSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mModeSelector.setAdapter(modeSelectorAdapter);
		mModeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub				
				mModelGLSurfaceView.getModelRenderer().setHandlerMode(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub				
			}
			
		});
	}
	
	private void inflate() {
		mLayoutInflater = (LayoutInflater) mContext
	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
	    mLayoutInflater.inflate(R.layout.model_bg, this, true);	    
	}	

}
