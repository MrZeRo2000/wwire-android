package com.romanpulov.wwire.view;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.romanpulov.wwire.R;
import com.romanpulov.wwire.gles.ElementsDrawer;
import com.romanpulov.wwire.gles.GLES20DrawerFactory;
import com.romanpulov.wwire.helper.AssetsHelper;
import com.romanpulov.wwire.helper.StorageHelper;
import com.romanpulov.wwire.model.WWireData;

public class MainActivity extends Activity {
	
	public static final String TAG = "MainActivity";
	
	private ModelGLSurfaceView mModelSurfaceView;
	
	private Spinner mFileSelector;
	private Spinner mViewSelector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final String tag = "OnCreate";
		//Log.d(tag, null == savedInstanceState ? "Bundle is null" : "Bundle is not null");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// check opengles support
		final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
		final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
		//Log.d(TAG, "SupportsES2 = " + String.valueOf(supportsEs2));

		// get DisplayMetrics and density
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);		
		
		// set up default drawer		
		ModelLayout modelLayout = (ModelLayout)findViewById(R.id.modellayout);
		mModelSurfaceView = modelLayout.getModelGLSurfaceView(); 
		mModelSurfaceView.getModelRenderer().setModelDrawer(GLES20DrawerFactory.getInstance().getModelDrawer(ElementsDrawer.class));
		
		// set up density 
		mModelSurfaceView.setDensity(displayMetrics.density);

		// copy over default models
		AssetsHelper.listAssets(this, "pre_inst_models/");
		
		// setup controls
		mFileSelector = setupFileSelector();
		mViewSelector = setupViewSelector();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("FileSelector", mFileSelector.getSelectedItemPosition());
		outState.putInt("ViewSelector", mViewSelector.getSelectedItemPosition());
		mModelSurfaceView.getModelRenderer().saveHandlerState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mFileSelector.setSelection(savedInstanceState.getInt("FileSelector"));
		mViewSelector.setSelection(savedInstanceState.getInt("ViewSelector"));
		mModelSurfaceView.getModelRenderer().loadHandlerState(savedInstanceState);
	}
	
	private Spinner setupFileSelector() {
		Spinner mFileSelector = findViewById(R.id.fileselector);
		File fileList = StorageHelper.getDataFileFolder(getApplicationContext());
		
		String[] files = null;
		if (fileList.exists()) {
				files = fileList.list(new FilenameFilter()  {
				
				@Override
				public boolean accept(File dir, String filename) {
					return filename.toUpperCase(Locale.US).endsWith("WW1");
				}
			});
		}
		
		if (null != files) {
			ArrayAdapter<String> fileSelectorAdapter = new ArrayAdapter<>(
					this, android.R.layout.simple_spinner_item, files);
			fileSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mFileSelector.setAdapter(fileSelectorAdapter);
			mFileSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					// load new file
					WWireData.getInstance().loadFromFile(StorageHelper.getDataFile(getApplicationContext(), (String)parent.getItemAtPosition(position)));
					mModelSurfaceView.getModelRenderer().getModelDrawer().invalidate();
					mModelSurfaceView.requestRender();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});
		}
		
		return mFileSelector;
	}
	
	private Spinner setupViewSelector() {
		final Spinner mViewSelector = (Spinner)findViewById(R.id.viewselector);
		ArrayAdapter<String> fileSelectorAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, GLES20DrawerFactory.DrawerListTitle);
		fileSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mViewSelector.setAdapter(fileSelectorAdapter);
		mViewSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				//Log.d(MainActivity.TAG, "setupViewSelector.onItemSelected position = " + String.valueOf(position));
				
				if ((1 == position) && (! WWireData.getInstance().gaintAvailable())) {
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
						GLES20DrawerFactory.getInstance().getModelDrawer(GLES20DrawerFactory.DrawerListClass[position])
							);
					mModelSurfaceView.getModelRenderer().getModelDrawer().invalidate();
					mModelSurfaceView.requestRender();
				}				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
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
