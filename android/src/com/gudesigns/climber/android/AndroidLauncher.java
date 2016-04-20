package com.gudesigns.climber.android;

import AdsInterface.IActivityRequestHandler;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.Mesh;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.gudesigns.climber.GameLoader;

public class AndroidLauncher extends AndroidApplication implements IActivityRequestHandler {
	protected AdView adView;
	private final int SHOW_ADS = 1;
	private final int HIDE_ADS = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.numSamples = 0;
		config.hideStatusBar = true;
		config.touchSleepTime = 16;
		Mesh.clearAllMeshes(this);

		// Built Layout
		RelativeLayout layout = new RelativeLayout(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        
		// Build gameView
		View gameView=initializeForView(new GameLoader(this),config);

		// Build AdView
		AdViewUnit adViewUnit = initAdView();
		adView = adViewUnit.view;
		RelativeLayout.LayoutParams adParams = adViewUnit.params;

		// Add views to layout
		layout.addView(gameView);
		layout.addView(adView,adParams);
		
		// Hide buttons
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			hideVirtualButtons();
		}

		//initialize(new GameLoader(), config);
		setContentView(layout);
	}
	
	@Override
	public void showAds(boolean show) {
		handler.sendEmptyMessage(show ? SHOW_ADS : HIDE_ADS);
	}
	
	@Override
	public void Toast(String text) {
		android.widget.Toast.makeText(this, text, android.widget.Toast.LENGTH_SHORT).show();
	}

    protected Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case SHOW_ADS:
                {
                    adView.setVisibility(View.VISIBLE);
                    break;
                }
                case HIDE_ADS:
                {
                    adView.setVisibility(View.GONE);
                    break;
                }
            }
        }
    };
	
	private class AdViewUnit {
		AdView view;
		RelativeLayout.LayoutParams params;
	}
	
	private AdViewUnit initAdView (){
		AdView adView = new AdView(this);
		adView.setAdSize(AdSize.BANNER);
		adView.setAdUnitId("ca-app-pub-6076836148998107/3630881938");
		AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
		adView.loadAd(adRequestBuilder.build());

		RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		adParams.addRule(RelativeLayout.ALIGN_BOTTOM);
		
		AdViewUnit ret = new AdViewUnit();
		ret.view = adView;
		ret.params =adParams;
		
		return ret;
	}

	@TargetApi(19)
	private void hideVirtualButtons() {
		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
						| View.SYSTEM_UI_FLAG_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			// In KITKAT (4.4) and next releases, hide the virtual buttons
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				hideVirtualButtons();
			}
		}
	}

}
