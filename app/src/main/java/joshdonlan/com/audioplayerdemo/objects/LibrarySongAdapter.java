package joshdonlan.com.audioplayerdemo.objects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import java.util.ArrayList;

import joshdonlan.com.audioplayerdemo.R;
import joshdonlan.com.audioplayerdemo.fragments.LibraryFragment;

/**
 * Created by jdonlan on 7/30/15.
 */
public class LibrarySongAdapter extends SongAdapter {

    public LibrarySongAdapter(Context _context, ArrayList<Song> _songs) {
        super(_context, _songs);
    }

    @Override
    protected View.OnClickListener buildClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mContext, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.item_list_song_menu, popup.getMenu());
                popup.getMenu().findItem(R.id.item_play_next).setActionView(v);
                popup.getMenu().findItem(R.id.item_add_queue).setActionView(v);
                popup.show();
                popup.setOnMenuItemClickListener(popupListener);
            }
        };
    }

    protected PopupMenu.OnMenuItemClickListener popupListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int position = (int) item.getActionView().getTag();
            switch (item.getItemId()){
                case R.id.item_play_next :
                    if(mContext instanceof LibraryFragment.LibraryListener)
                        ((LibraryFragment.LibraryListener) mContext).addSong(mSongs.get(position),true);
                    return true;
                case R.id.item_add_queue :
                    if(mContext instanceof LibraryFragment.LibraryListener)
                        ((LibraryFragment.LibraryListener) mContext).addSong(mSongs.get(position),false);
                    return true;
            }
            return false;
        }
    };

}
