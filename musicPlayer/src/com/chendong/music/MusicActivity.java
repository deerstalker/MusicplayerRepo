package com.chendong.music;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MusicActivity extends ListActivity implements
		OnAudioFocusChangeListener {
	private MediaPlayer myMediaPlayer;
	private MediaPlayer noiseMediaPlayer;
	private MediaPlayer asynchronousMediaPlayer;

	private static final String TAG = "MusicActivity";
	private List<String> myMusicList = new ArrayList<String>();
	private int currentListItem = 0;

	private String remoteMusicURI = "http://dc220.4shared.com/img/302542695/d0964170/" +
			"dlink__2Fdownload_2FxdlEtbxq_3Ftsid_3D20101119-54721-4807ddd0/preview.mp3";
	// some variables

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		myMediaPlayer = new MediaPlayer();
		findView();
		musicList();
		listener();
	}

	/*
	 * use Asynchronous preparation
	 */
	void prepareMusicAsynchronous() {
	
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int result = audioManager.requestAudioFocus(this,
				AudioManager.STREAM_MUSIC,
				AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

		if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			// could not get audio focus.
			Toast.makeText(this, "cannot get audio focus!", 2);
		}
		if(myMediaPlayer != null){
			myMediaPlayer.reset();
		}
		try {
			try {
				myMediaPlayer.setDataSource(remoteMusicURI);
				myMediaPlayer.prepareAsync();
			
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myMediaPlayer.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer asyncMediaPlayer) {
				// TODO Auto-generated method stub
				myMediaPlayer.start();
				myMediaPlayer
				.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
						nextMusic();
					}
				});
			}
		});

	}


	void musicList() {
		myMusicList.add("LocalMusic");
		myMusicList.add("RemoteMusic");
		ArrayAdapter<String> musicList = new ArrayAdapter<String>(
				MusicActivity.this, R.layout.musicitme, myMusicList);
		
		setListAdapter(musicList);
	}

	void findView() {
		ViewHolder.start = (Button) findViewById(R.id.start);
		ViewHolder.stop = (Button) findViewById(R.id.stop);
		ViewHolder.next = (Button) findViewById(R.id.next);
		ViewHolder.pause = (Button) findViewById(R.id.pause);
		ViewHolder.last = (Button) findViewById(R.id.last);
	}

	void listener() {
		// ֹͣ
		ViewHolder.stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (myMediaPlayer.isPlaying()) {
					myMediaPlayer.reset();
				}
			}
		});
		ViewHolder.start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				playMusic(currentListItem);
			}
		});
		ViewHolder.next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				nextMusic();
			}
		});
		ViewHolder.pause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (myMediaPlayer.isPlaying()) {
					myMediaPlayer.pause();
					// abandon audio focus
					AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
					am.abandonAudioFocus(MusicActivity.this);
				} else {
					myMediaPlayer.start();
				}
			}
		});
		ViewHolder.last.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				lastMusic();
			}
		});

	}

	/*
	 * currentListItem = 0:local
	 * currentListItem = 1:remote
	 */
	void playMusic(int currentListItem) {
		if (myMusicList.size() == 0) {
			return;
		}
		if (currentListItem == 1) {
			prepareMusicAsynchronous();
		}
		else if(currentListItem == 0){
			if(myMediaPlayer != null){
				myMediaPlayer.release();
			}
			myMediaPlayer = MediaPlayer.create(this, R.raw.courage);
			AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			int result = audioManager.requestAudioFocus(this,
					AudioManager.STREAM_MUSIC,
					AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

			if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
				// could not get audio focus.
				Toast.makeText(this, "cannot get audio focus!", 2);
			}
			myMediaPlayer.start();
			myMediaPlayer
			.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					nextMusic();
				}
			});
		}
	}

	void nextMusic() {
		if (++currentListItem >= myMusicList.size()) {
			currentListItem = 0;
		}

		playMusic(currentListItem);

	}

	void lastMusic() {

		if (--currentListItem < 0) {
			currentListItem = myMusicList.size() - 1;
		}
		playMusic(currentListItem);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			myMediaPlayer.stop();
			myMediaPlayer.release();
			if (noiseMediaPlayer != null) {
				noiseMediaPlayer.stop();
				noiseMediaPlayer.release();
			}
			if (asynchronousMediaPlayer != null) {
				asynchronousMediaPlayer.stop();
				asynchronousMediaPlayer.release();
			}
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		currentListItem = position;
		Log.d(TAG, ""+currentListItem);
		playMusic(position);
	}
	

	@Override
	public void onAudioFocusChange(int focusChange) {
		// TODO Auto-generated method stub

	}

}