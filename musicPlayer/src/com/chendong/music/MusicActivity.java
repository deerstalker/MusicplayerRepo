package com.chendong.music;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List; 

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MusicActivity extends ListActivity implements OnAudioFocusChangeListener {
	private MediaPlayer myMediaPlayer;
	private MediaPlayer noiseMediaPlayer;
	private MediaPlayer asynchronousMediaPlayer;
	
	private List<String> myMusicList=new ArrayList<String>();
	private int currentListItem=0;
	private static final String MUSIC_PATH=new String("/sdcard/");
	
	//some variables 
	private boolean canBreak = false;
	private EditText editText;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        myMediaPlayer=new MediaPlayer();
        
        findView();
        musicList();
        listener();
        editText.setText("ready to go!");
        playMusicUseCreate();
        //prepareMusicAsynchronous();
    }	
    
    /*
     * use Asynchronous preparation
     */
    void prepareMusicAsynchronous(){
    	int i = 0;
    	
    	String url = "http://cd04.static.jango.com/music/07/0563/0705635592.mp3";
    	asynchronousMediaPlayer = new MediaPlayer();
    	asynchronousMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    	try {
    		try {
    			asynchronousMediaPlayer.setDataSource(url);
				asynchronousMediaPlayer.prepareAsync();
				editText.setText("prepareAsync!");
				while(!canBreak){
					i++;
				}
				editText.setText(""+i);
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
    	asynchronousMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer asyncMediaPlayer) {
				// TODO Auto-generated method stub
				asynchronousMediaPlayer.start();
				canBreak = true;
			}
		});
    	
    }
    
    /*
     *use create method, no need to prepare
     */
    void playMusicUseCreate(){
    	//res/raw
    	//noiseMediaPlayer = MediaPlayer.create(this, R.raw.mariahcareyohsanta);;
    	/*
    	noiseMediaPlayer = MediaPlayer.create(this, Uri.parse(
    			"http://cd04.static.jango.com/music/07/0563/0705635592.mp3"));
    	*/
    	//noiseMediaPlayer.start();
    	//startService(new Intent(this, MyService.class));
    }
    void musicList(){
    	File home=new File(MUSIC_PATH);
    	if(home.listFiles(new MusicFilter()).length>0){
    		for(File file:home.listFiles(new MusicFilter())){
    			myMusicList.add(file.getName());
    		}
    		ArrayAdapter<String> musicList=new ArrayAdapter<String>
    		(MusicActivity.this,R.layout.musicitme, myMusicList);
    		setListAdapter(musicList);
    	}
    }
    
   void findView(){
	   ViewHolder.start=(Button)findViewById(R.id.start);
	   ViewHolder.stop=(Button)findViewById(R.id.stop);
	   ViewHolder.next=(Button)findViewById(R.id.next);
	   ViewHolder.pause=(Button)findViewById(R.id.pause);
	   ViewHolder.last=(Button)findViewById(R.id.last);
	   editText = (EditText)findViewById(R.id.statusText);
   }
   
   
   void listener(){
	   //ֹͣ
	   ViewHolder.stop.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(myMediaPlayer.isPlaying()){
				myMediaPlayer.reset();
				editText.setText("Now: Idle state!");
			}
		}
	});
	   ViewHolder.start.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			playMusic(MUSIC_PATH+myMusicList.get(currentListItem));
			editText.setText("Now: Started state!");
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
			if(myMediaPlayer.isPlaying()){
				myMediaPlayer.pause();
				editText.setText("Now: Paused state!");
				
				//abandon audio focus
				AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
				am.abandonAudioFocus(MusicActivity.this);
			}else{
				myMediaPlayer.start();
				editText.setText("Now: Started state!");
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
   
   void playMusic(String path){
	   if(myMusicList.size() == 0){
		   return; 
	   }                                                                                                                                                                                                                                                                                                                                                                                                                        
	   try { 
		   
		   AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		   int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
		       AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
		   
		   if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
		       // could not get audio focus.
			   editText.setText("donot get focus!");
		   }
		   
		myMediaPlayer.reset();
		//editText.setText("Now: Paused state!");
		myMediaPlayer.setDataSource(path);
		myMediaPlayer.prepare();
		myMediaPlayer.start();
		editText.setText("Now: Started state!");
		myMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				nextMusic();
			}
		});
	} catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	}
   }
   
   void nextMusic(){
	   if(++currentListItem>=myMusicList.size()){
		   currentListItem=0;
	   }
	  
	   playMusic(MUSIC_PATH+myMusicList.get(currentListItem));
	   
   }
   
   void lastMusic(){
	   
	   if(--currentListItem<0){
		   currentListItem=myMusicList.size()-1;
	   }
	   playMusic(MUSIC_PATH+myMusicList.get(currentListItem));
	  
   }
   
	   @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		   if(keyCode==KeyEvent.KEYCODE_BACK){
			   myMediaPlayer.stop();
			   myMediaPlayer.release();
			   if(noiseMediaPlayer != null){
				   noiseMediaPlayer.stop();
				   noiseMediaPlayer.release();
			   }
			   if(asynchronousMediaPlayer != null){
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
		currentListItem=position;
		playMusic(MUSIC_PATH+myMusicList.get(currentListItem));
	}

	@Override
	public void onAudioFocusChange(int focusChange) {
		// TODO Auto-generated method stub
		
	}
   
}