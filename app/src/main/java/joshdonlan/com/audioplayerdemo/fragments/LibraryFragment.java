package joshdonlan.com.audioplayerdemo.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupMenu;

import java.util.ArrayList;

import joshdonlan.com.audioplayerdemo.R;
import joshdonlan.com.audioplayerdemo.objects.LibrarySongAdapter;
import joshdonlan.com.audioplayerdemo.objects.Song;
import joshdonlan.com.audioplayerdemo.objects.SongAdapter;
import joshdonlan.com.audioplayerdemo.objects.SongListener;

/**
 * Created by jdonlan on 7/28/15.
 */
public class LibraryFragment extends Fragment implements SongListener, SongAdapter.SongPopupListener {

    private static final String TAG = "LibraryFragment";
    LibrarySongAdapter mSongAdapter;

    public interface LibraryListener{
        public void addSong(Song _song, boolean _next);
    }

    public static LibraryFragment newInstance(){
        LibraryFragment frag = new LibraryFragment();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library,container,false);

        mSongAdapter = new LibrarySongAdapter(getActivity(), new ArrayList<Song>());
        ListView songList = (ListView) view.findViewById(R.id.lv_library);
        songList.setAdapter(mSongAdapter);

        return view;
    }

    //SongPopupListenr Interface Methods

    public void showPopup(View _view, int _position){
        PopupMenu popup = new PopupMenu(getActivity(), _view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.item_list_song_menu, popup.getMenu());
        popup.show();
    }

    //SongListener Interface Methods

    @Override
    public void receiveData(ArrayList<Song> _data) {
        mSongAdapter.update(_data);
    }

}
