package joshdonlan.com.audioplayerdemo;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import joshdonlan.com.audioplayerdemo.fragments.LibraryFragment;
import joshdonlan.com.audioplayerdemo.fragments.PlaylistFragment;
import joshdonlan.com.audioplayerdemo.objects.ScanMusicTask;
import joshdonlan.com.audioplayerdemo.objects.Song;
import joshdonlan.com.audioplayerdemo.objects.SongListener;
import joshdonlan.com.audioplayerdemo.utils.Globals;


public class MainActivity extends Activity implements ActionBar.TabListener, ScanMusicTask.ScanMusicListener, LibraryFragment.LibraryListener, ServiceConnection {

    private static final String TAG = "MainActivity";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private boolean mBound = false;
    private PlaybackService.MusicBinder mBinder;
    private MusicReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
                switch (position) {
                    case 0:
                        updateLibrary();
                        break;
                    case 1:
                        updatePlaylist();
                        break;
                }
            }
        });

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        findViewById(R.id.b_rewind).setOnClickListener(skipBackListener);
        findViewById(R.id.b_forward).setOnClickListener(skipForwardListener);
        findViewById(R.id.b_pause).setOnClickListener(playPauseToggleListener);

    }

    @Override
    protected void onResume() {
        super.onResume();

        updateLibrary();

        if(!mBound){
            Intent startService = new Intent(this,PlaybackService.class);
            startService(startService);
            bindService(startService, this, Context.BIND_AUTO_CREATE);
        }

        mReceiver = new MusicReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Globals.ACTION_NEW_SONG);
        registerReceiver(mReceiver,filter);
    }

    @Override
    protected void onPause() {

        if(mBound){
            unbindService(this);
            mBound = false;
            mBinder = null;
        }

        unregisterReceiver(mReceiver);

        super.onPause();
    }

    private void updateLibrary() {
        ScanMusicTask scanMusicTask = new ScanMusicTask(this);
        scanMusicTask.execute();
    }


    private void updatePlaylist() {
        Fragment frag;
        int ci = mViewPager.getCurrentItem();
        frag = getFragmentManager().findFragmentByTag("android:switcher:" + mViewPager.getId() + ":" + mViewPager.getCurrentItem());
        if(frag != null && frag instanceof SongListener){
            ((SongListener) frag).receiveData(mBinder.getService().getPlayList());
        }
    }

    private View.OnClickListener skipBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mBound) mBinder.getService().skipBackward();
        }
    };

    private View.OnClickListener skipForwardListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBound) mBinder.getService().skipForward();
        }
    };

    private View.OnClickListener playPauseToggleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBound) mBinder.getService().togglePause();
            String currentText = ((Button) v).getText().toString();
            if(currentText.compareTo("P") == 0){
                ((Button) v).setText("PL");
            } else {
                ((Button) v).setText("P");
            }
        }
    };

    //ActionBar Interface Methods

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    //ScanMusic Interface Methods

    @Override
    public void updateSongData(ArrayList<Song> _songs) {
        Fragment frag;
        int ci = mViewPager.getCurrentItem();
        frag = getFragmentManager().findFragmentByTag("android:switcher:" + mViewPager.getId() + ":" + mViewPager.getCurrentItem());
        if(frag != null && frag instanceof SongListener){
            Log.i(TAG, "Updating Song Data");
            ((SongListener) frag).receiveData(_songs);
        }
    }

    //Library Listener Methods

    @Override
    public void addSong(Song _song, boolean _next) {
        if(mBound){
            mBinder.getService().addToPlaylist(_song, _next);
        } else {
            Toast.makeText(this,R.string.toast_cannot_connect_to_service,Toast.LENGTH_SHORT).show();
        }
    }

    //ServiceConnection Methods

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mBound = true;
        mBinder = (PlaybackService.MusicBinder) service;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mBound = false;
        mBinder = null;
    }

    //ViewPager Adapter

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int _position) {
            switch (_position){
                case 0:
                    return LibraryFragment.newInstance();
                case 1:
                    return PlaylistFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }

    private class MusicReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().compareTo(Globals.ACTION_NEW_SONG) == 0){
                Log.i(TAG,"Recieved New Song Broadcast");
                Song song = intent.getParcelableExtra(Globals.EXTRA_SONG);
                TextView songView = (TextView) findViewById(R.id.tv_currentSong);
                TextView artistView = (TextView) findViewById(R.id.tv_currentArtist);
                songView.setText(song.getmTitle());
                artistView.setText(song.getmArtist());
            }
        }
    }

}
