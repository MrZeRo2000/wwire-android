package com.romanpulov.wwire;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TabHost;

public class MainActivity extends Activity {
	
	private static final String TAB_MODEL = "Model";
	private static final String TAB_DIAGRAM = "Diagram";
	
	TabHost mTabHost;
	ModelGLSurfaceView mModelSurfaceView;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
		final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
		Log.d("MainActivity", String.valueOf(supportsEs2));
		WWireData.createInstance(this);		
		//WWireData.getInstance().loadFromFile("Frac5.ww1");
		
		ModelLayout modelLayout = (ModelLayout)findViewById(R.id.modellayout);
		mModelSurfaceView = modelLayout.getModelGLSurfaceView(); 
		mModelSurfaceView.getModelRenderer().setModelDrawer(GLES20DrawerFactory.getInstance().getModelDrawer(ElementsDrawer.class));

		setupFileSelector();
		setupViewSelector();
	}
	
	private void setupFileSelector() {
		Spinner mFileSelector = (Spinner)findViewById(R.id.fileselector);
		File fileList = new File(WWireData.getInstance().getDataFileFolder());
		
		String[] files = null;
		if (fileList.exists()) {
				files = fileList.list(new FilenameFilter()  {
				
				@Override
				public boolean accept(File dir, String filename) {
					// TODO Auto-generated method stub
					return filename.toUpperCase(Locale.US).endsWith("WW1");					
				}
			});
		}
		
		if (null != files) {
			ArrayAdapter<String> fileSelectorAdapter = new ArrayAdapter<String>(
					this, android.R.layout.simple_spinner_item, files);
			fileSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mFileSelector.setAdapter(fileSelectorAdapter);
			mFileSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					WWireData.getInstance().loadFromFile((String)parent.getItemAtPosition(position));
					mModelSurfaceView.getModelRenderer().getModelDrawer().invalidate();
					mModelSurfaceView.requestRender();	
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub				
				}
				
			});			
		}
	}
	
	private void setupViewSelector() {
		Spinner mViewSelector = (Spinner)findViewById(R.id.viewselector);
		ArrayAdapter<String> fileSelectorAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, GLES20DrawerFactory.DrawerListTitle);
		fileSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mViewSelector.setAdapter(fileSelectorAdapter);
		mViewSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mModelSurfaceView.getModelRenderer().setModelDrawer(
						GLES20DrawerFactory.getInstance().getModelDrawer(GLES20DrawerFactory.DrawerListClass[position])
				);
				mModelSurfaceView.requestRender();	
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub				
			}
			
		});
		
		mViewSelector.setSelection(1);
		
	}
	
	
	TabHost.TabContentFactory TabFactory = new TabHost.TabContentFactory() {	    
	    @Override
	    public View createTabContent(String tag) {	    		
	    	if (TAB_MODEL == tag) {
	    		ModelLayout modelLayout = new ModelLayout(mTabHost.getContext());	    		
	    		modelLayout.getModelGLSurfaceView().getModelRenderer().setModelDrawer(new ElementsDrawer());	    		
	    		Log.d("MainActivity", "returning ElementsDrawer");
	    		return modelLayout;
	    	} else if (TAB_DIAGRAM == tag) {
	    		ModelLayout modelLayout = new ModelLayout(mTabHost.getContext());
	    		//modelLayout.getModelGLSurfaceView().getModelRenderer().setModelDrawer(new DiagramDrawer());
	    		mModelSurfaceView.getModelRenderer().setModelDrawer(new DiagramDrawer());
	    		Log.d("MainActivity", "returning DiagramDrawer");
	    		return modelLayout;
	    		//return (new android.widget.AnalogClock(mTabHost.getContext())); //
	    	} else {
	    		Log.d("MainActivity", "returning null");
	    		return null;
	    	}	
	    }
	};
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mModelSurfaceView.onPause();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mModelSurfaceView.onResume();
	}	

}
