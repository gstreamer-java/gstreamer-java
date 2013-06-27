package org.gstreamer.example;

import org.gstreamer.Gst;
import org.gstreamer.Pipeline;
import org.gstreamer.android.GstAndroidSurfaceAttach;
import org.gstreamer.lowlevel.GNative;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.gstreamer.GStreamer;

public class AndroidSurfaceActivity extends Activity {
 
    private boolean is_playing_desired;   // Whether the user asked to go to PLAYING

    private Pipeline pipe;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Initialize GStreamer and warn if it fails
        try {
        	GNative.setGlobalLibName("gstreamer_android");
            GStreamer.init(this);
            Gst.init();
           	pipe = Pipeline.launch("videotestsrc ! warptv ! ffmpegcolorspace ! eglglessink name=videosink");
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish(); 
            return;
        }

        setContentView(R.layout.main);

        ImageButton play = (ImageButton) this.findViewById(R.id.button_play);
        play.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                is_playing_desired = true;
                pipe.play();
            }
        });

        ImageButton pause = (ImageButton) this.findViewById(R.id.button_stop);
        pause.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                is_playing_desired = false;
                pipe.pause();
            }
        });

        SurfaceView sv = (SurfaceView) this.findViewById(R.id.surface_video);
        
        new GstAndroidSurfaceAttach(pipe.getElementByName("videosink"), sv) {

			@Override
			protected void onCreated() {
				if (is_playing_desired) {
					pipe.play();
				} else {
					pipe.pause();
				}
			}

			@Override
			protected void onDestroyed() {
				pipe.ready();
			}};
        

        if (savedInstanceState != null) {
            is_playing_desired = savedInstanceState.getBoolean("playing");
            Log.i ("GStreamer", "Activity created. Saved state is playing:" + is_playing_desired);
        } else {
            is_playing_desired = false;
            Log.i ("GStreamer", "Activity created. There is no saved state, playing: false");
        }
        
        if (is_playing_desired) {
        	pipe.play();
        }
    }

    protected void onSaveInstanceState (Bundle outState) {
        Log.d ("GStreamer", "Saving state, playing:" + is_playing_desired);
        outState.putBoolean("playing", is_playing_desired);
    }

    protected void onDestroy() {
    	
    	if (pipe != null) {
    		pipe.stop();
//    		Gst.deinit();
    	}
    	
        super.onDestroy();
    }


    static {
        System.loadLibrary("gstreamer_android");
        System.loadLibrary("android_jgst");
    }
}
