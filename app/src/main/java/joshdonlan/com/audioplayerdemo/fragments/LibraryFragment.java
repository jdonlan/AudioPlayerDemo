package joshdonlan.com.audioplayerdemo.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import joshdonlan.com.audioplayerdemo.R;
import joshdonlan.com.audioplayerdemo.objects.SongAdapter;
import joshdonlan.com.audioplayerdemo.objects.SongListener;
import joshdonlan.com.audioplayerdemo.objects.Song;

/**
 * Created by jdonlan on 7/28/15.
 */
public class LibraryFragment extends Fragment implements SongListener {

    private static final String TAG = "LibraryFragment";
    SongAdapter mSongAdapter;
    public static LibraryFragment newInstance(){
        LibraryFragment frag = new LibraryFragment();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library,container,false);

        mSongAdapter = new SongAdapter(getActivity(), new ArrayList<Song>());
        ListView songList = (ListView) view.findViewById(R.id.lv_library);
        songList.setAdapter(mSongAdapter);

        return view;
    }

    //SongListener Interface Methods

    @Override
    public void receiveData(ArrayList<Song> _data) {
        Log.i(TAG,"New Song Data Received");
        mSongAdapter.update(_data);
        mSongAdapter.notifyDataSetChanged();
    }

}
