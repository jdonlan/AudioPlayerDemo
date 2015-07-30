package joshdonlan.com.audioplayerdemo;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;

import joshdonlan.com.audioplayerdemo.fragments.LibraryFragment;
import joshdonlan.com.audioplayerdemo.fragments.PlaylistFragment;
import joshdonlan.com.audioplayerdemo.objects.ScanMusicTask;
import joshdonlan.com.audioplayerdemo.objects.Song;
import joshdonlan.com.audioplayerdemo.objects.SongListener;


public class MainActivity extends Activity implements ActionBar.TabListener, ScanMusicTask.ScanMusicListener {

    private static final String TAG = "MainActivity";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

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
                if(position == 0){
                    updateLibrary();
                }
            }
        });

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLibrary();
    }

    public void updateLibrary(){
        ScanMusicTask scanMusicTask = new ScanMusicTask(this);
        scanMusicTask.execute();
    }

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

    //ViewPager Adapter

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

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

}
