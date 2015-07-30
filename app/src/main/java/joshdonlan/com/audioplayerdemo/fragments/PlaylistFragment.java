package joshdonlan.com.audioplayerdemo.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import joshdonlan.com.audioplayerdemo.R;
import joshdonlan.com.audioplayerdemo.objects.PlaylistSongAdapter;
import joshdonlan.com.audioplayerdemo.objects.Song;
import joshdonlan.com.audioplayerdemo.objects.SongListener;

/**
 * Created by jdonlan on 7/28/15.
 */
public class PlaylistFragment extends Fragment implements SongListener {

    private static final String TAG = "PlaylistFragment";
    private PlaylistSongAdapter mSongAdapter;

    public static PlaylistFragment newInstance(){
        PlaylistFragment frag = new PlaylistFragment();
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        mSongAdapter = new PlaylistSongAdapter(getActivity(), new ArrayList<Song>());
        ListView playlist = (ListView) view.findViewById(R.id.lv_playlist);
        playlist.setAdapter(mSongAdapter);

        return view;

    }

    @Override
    public void receiveData(ArrayList<Song> _data) {
        Log.i(TAG, "Updating Playlist: " + _data.toString());
        mSongAdapter.update(_data);
    }
}
