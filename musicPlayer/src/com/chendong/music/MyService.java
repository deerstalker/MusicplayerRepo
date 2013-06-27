package com.chendong.music;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service implements AudioManager.OnAudioFocusChangeListener{
	private static final String TAG = "MyService";
	MediaPlayer player;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");

//		player = MediaPlayer.create(this, R.raw.mariahcareyohsanta);
//		player.setLooping(false);
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
		player.stop();
}

	@Override
	public void onStart(Intent intent, int startid) {
		Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");
		
//		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//		int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
//				AudioManager.AUDIOFOCUS_GAIN);
//
//		if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//		       // could not get audio focus.
//			
//		}
		player.start();
	}

	@Override
	public void onAudioFocusChange(int focusChange) {
		// TODO Auto-generated method stub
		switch(focusChange){
		case AudioManager.AUDIOFOCUS_GAIN:
            // resume playback
			if(player != null){
				player.start();
			}
            break;

        case AudioManager.AUDIOFOCUS_LOSS:
            // Lost focus for an unbounded amount of time: stop playback and release media player
            if (player.isPlaying()) player.stop();
            AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(this);
            player.release();
            player = null;
            Log.d(TAG, "AUDIOFOCUS_LOSS");
            
            break;

        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            // Lost focus for a short time, but we have to stop
            // playback. We don't release the media player because playback
            // is likely to resume
            if (player.isPlaying()) player.pause();
            Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
            break;

        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
            // Lost focus for a short time, but it's ok to keep playing
            // at an attenuated level
            if (player.isPlaying()) player.setVolume(0.1f, 0.1f);
            Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
            break;
		}
	}
	
}
