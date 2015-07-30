package joshdonlan.com.audioplayerdemo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import joshdonlan.com.audioplayerdemo.objects.Song;
import joshdonlan.com.audioplayerdemo.utils.Globals;

/**
 * Created by jdonlan on 7/30/15.
 */
public class PlaybackService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {


    private static final String previousIntentCode = "joshdonlan.com.audioplayerdemo.INTENT_PREVIOUS";
    private static final String nextIntentCode = "joshdonlan.com.audioplayerdemo.INTENT_NEXT";
    private static final String playpauseIntentCode = "joshdonlan.com.audioplayerdemo.INTENT_PLAYPAUSE";
    private static final String openPlayerIntentCode = "joshdonlan.com.audioplayerdemo.INTENT_OPENPLAYER";

    private static final String TAG = "PlaybackService";
    private MusicBinder mBinder;
    private MediaPlayer mPlayer;
    private ArrayList<Song> mPlaylist;
    private int mCurrentSong = 0;
    private boolean mIsPrepared = false;
    private boolean mPlaying = false;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mBinder = new MusicBinder();

        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);

        mPlaylist = new ArrayList<Song>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null && intent.getAction() != null){
            Log.i(TAG,"Intent: " + intent.getAction());
            switch (intent.getAction()){
                case previousIntentCode:
                    skipBackward();
                    break;
                case nextIntentCode:
                    skipForward();
                    break;
                case playpauseIntentCode:
                    togglePause();
                    break;
                case openPlayerIntentCode:
                    Intent openIntent = new Intent(getApplicationContext(), MainActivity.class);
                    openIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(openIntent);
                    break;
            }
        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if(mPlayer != null){
            mPlayer.release();
            mPlayer = null;
        }
        stopForeground(true);
        super.onDestroy();
    }

    public ArrayList<Song> getPlayList(){
        return mPlaylist;
    }

    public void skipForward(){
        if(mPlayer != null){
             mCurrentSong++;
            if(mCurrentSong >= mPlaylist.size()) mCurrentSong = mPlaylist.size()-1;
            skipTrack();
        }
    }

    public void skipBackward(){
        if(mPlayer != null) {
            if (mPlayer.getCurrentPosition() < 3000) {
                mCurrentSong--;
                if (mCurrentSong < 0) mCurrentSong = 0;
                skipTrack();
            } else {
                mPlayer.seekTo(0);
            }
        }
    }

    public void togglePause(){
        if(mPlayer != null){
            if(mPlayer.isPlaying()){
                mPlayer.pause();
            } else {
                if(mIsPrepared) mPlayer.start();
            }
        }

    }

    private void playSong(Song _song) {
        Log.i(TAG, "Playing: " + _song.getmTitle());
        Intent broadcastIntent = new Intent(Globals.ACTION_NEW_SONG);
        broadcastIntent.putExtra(Globals.EXTRA_SONG, _song);
        sendBroadcast(broadcastIntent);


        Intent previousIntent = new Intent(previousIntentCode);
        Intent nextIntent = new Intent(nextIntentCode);
        Intent playpauseIntent = new Intent(playpauseIntentCode);
        Intent openplayerIntent = new Intent(openPlayerIntentCode);

        PendingIntent previousPending = PendingIntent.getService(this,0,previousIntent,0);
        PendingIntent nextPending = PendingIntent.getService(this,0,nextIntent,0);
        PendingIntent playPending = PendingIntent.getService(this,0,playpauseIntent,0);
        PendingIntent openPending = PendingIntent.getService(this, 0, openplayerIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_headphone)
                .setContentTitle(_song.getmTitle())
                .setContentText(_song.getmArtist())
                .setLargeIcon(_song.getmCover())
                .setAutoCancel(false)
                .setContentIntent(openPending)
                .addAction(android.R.drawable.ic_media_previous, null, previousPending)
                .addAction(android.R.drawable.ic_media_play, null, playPending)
                .addAction(android.R.drawable.ic_media_next, null, nextPending);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(0x0101,builder.build());


        try {
            mPlayer.setDataSource(this, Uri.parse(_song.getPath()));
        } catch (IOException e) {
            Log.e(TAG, "Error reading file: " + _song.getPath());
            mPlayer.release();
            mPlayer = null;
        }
        if (mPlayer != null && !mIsPrepared) {
            mPlayer.prepareAsync();
        }
    }

    private void stopSong(){
        mPlayer.stop();
        mPlayer.reset();
        mIsPrepared = false;
        mPlaying = false;
    }

    private void skipTrack(){
        if(mPlayer != null){
            stopSong();
            playSong(mPlaylist.get(mCurrentSong));
        }
    }

    public void addToPlaylist(Song _song){
        Log.i(TAG,"adding " + _song.getmTitle() + " currently playing: " + mPlaying);
        addToPlaylist(_song, false);
    }

    public void addToPlaylist(Song _song, boolean _next){
        if(_next && mCurrentSong < mPlaylist.size()-1){
            mPlaylist.add(mCurrentSong+1, _song);
        } else {
            mPlaylist.add(_song);
        }
        if(!mPlaying){
            playSong(_song);
        }
    }

    //OnPreparedListener Methods

    @Override
    public void onPrepared(MediaPlayer mp) {
        mIsPrepared = true;
        mPlaying = true;
        mPlayer.start();
    }

    //OnCompletionListener Methods

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i(TAG,"Completing: " + mPlaylist.get(mCurrentSong).getmTitle());
        stopSong();
        mCurrentSong++;

        if(mCurrentSong < mPlaylist.size()){
            playSong(mPlaylist.get(mCurrentSong));
        }
    }


    public class MusicBinder extends Binder {
        public PlaybackService getService(){
            return PlaybackService.this;
        }
    }

}
