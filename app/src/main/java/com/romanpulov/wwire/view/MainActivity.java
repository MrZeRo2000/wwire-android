package com.romanpulov.wwire.view;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.romanpulov.wwire.R;
import com.romanpulov.wwire.gles.ElementsDrawer;
import com.romanpulov.wwire.gles.DrawerFactory;
import com.romanpulov.wwire.helper.AssetsHelper;
import com.romanpulov.wwire.helper.StorageHelper;
import com.romanpulov.wwire.model.WWireData;

public class MainActivity extends Activity {

	private WWireData mData = WWireData.createEmpty();
	
	private ModelGLSurfaceView mModelSurfaceView;

	//want to keep this one for consistency and possible future use
	@SuppressWarnings("unused,FieldCanBeLocal")
	private Spinner mFileSelector;

	private Spinner mViewSelector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//restore saved data
		if (savedInstanceState != null) {
            mData = savedInstanceState.getParcelable(WWireData.class.getName());
        }

		// get DisplayMetrics and density
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);		
		
		// set up default drawer		
		ModelLayout modelLayout = findViewById(R.id.modellayout);
		mModelSurfaceView = modelLayout.getModelGLSurfaceView(); 
		mModelSurfaceView.getModelRenderer().setModelDrawer(DrawerFactory.getInstance().getModelDrawer(mData, ElementsDrawer.class));
		
		// set up density 
		mModelSurfaceView.setDensity(displayMetrics.density);

		//restore saved handler state
		if (savedInstanceState != null) {
            mModelSurfaceView.getModelRenderer().loadHandlerState(savedInstanceState);
        }

		// copy over default models
		AssetsHelper.listAssets(this, "pre_inst_models/");
		
		// setup controls
		mFileSelector = setupFileSelector();
		mViewSelector = setupViewSelector();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
        outState.putParcelable(WWireData.class.getName(), mData);
		mModelSurfaceView.getModelRenderer().saveHandlerState(outState);
	}
	
	private Spinner setupFileSelector() {
		Spinner mFileSelector = findViewById(R.id.fileselector);

        String[] files = StorageHelper.getDataFileNameList(getApplicationContext());

		if (null != files) {
			ArrayAdapter<String> fileSelectorAdapter = new ArrayAdapter<>(
					this, android.R.layout.simple_spinner_item, files);
			fileSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mFileSelector.setAdapter(fileSelectorAdapter);
			mFileSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					// load new file
                    File dataFile = StorageHelper.getDataFile(getApplicationContext(), (String)parent.getItemAtPosition(position));
                    if ((mData == null) || (!mData.compareFile(dataFile))) {
                        mData = WWireData.fromFile(dataFile);
                        DrawerFactory.getInstance().invalidateModelDrawers();
                        mModelSurfaceView.getModelRenderer().setModelDrawer(DrawerFactory.getInstance().getModelDrawer(mData, ElementsDrawer.class));
                        mModelSurfaceView.getModelRenderer().performRevert();
                        mViewSelector.setSelection(0);
                    }
					mModelSurfaceView.requestRender();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {}
			});
		}
		
		return mFileSelector;
	}
	
	private Spinner setupViewSelector() {
		final Spinner mViewSelector = findViewById(R.id.viewselector);
		ArrayAdapter<String> fileSelectorAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, DrawerFactory.DrawerListTitle);
		fileSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mViewSelector.setAdapter(fileSelectorAdapter);
		mViewSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if ((1 == position) && (mData != null) && (!mData.gaintAvailable())) {
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setTitle(android.R.string.dialog_alert_title);
					builder.setIcon(android.R.drawable.ic_dialog_alert);
					builder.setMessage(R.string.warning_calc_results_not_available);
					builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							mViewSelector.setSelection(0);
						}
					});
					builder.create().show();					
				} else {				
					mModelSurfaceView.getModelRenderer().setModelDrawer(
						DrawerFactory.getInstance().getModelDrawer(mData, DrawerFactory.DrawerListClass[position])
							);
					mModelSurfaceView.getModelRenderer().getModelDrawer().invalidate();
					mModelSurfaceView.requestRender();
				}				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		return mViewSelector;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mModelSurfaceView.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mModelSurfaceView.onResume();
	}	
}
