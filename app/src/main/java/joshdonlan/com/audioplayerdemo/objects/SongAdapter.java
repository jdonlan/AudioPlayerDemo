package joshdonlan.com.audioplayerdemo.objects;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import joshdonlan.com.audioplayerdemo.R;

/**
 * Created by jdonlan on 7/28/15.
 */
public class SongAdapter extends BaseAdapter {

    private static final String TAG = "SongAdapter";
    protected Context mContext;
    protected ArrayList<Song> mSongs;

    public interface SongPopupListener{
        public void showPopup(View _view, int _position);
    }

    public SongAdapter(Context _context, ArrayList<Song> _songs){
        mContext = _context;
        mSongs = _songs;
    }

    @Override
    public int getCount() {
        return mSongs.size();
    }

    @Override
    public Object getItem(int _position) {
        return mSongs.get(_position);
    }

    @Override
    public long getItemId(int _position) {
        return _position;
    }

    @Override
    public View getView(int _position, View _convertView, ViewGroup _parent) {
        ViewHolder holder;

        if(_convertView == null){
            _convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_song,_parent,false);
            holder = new ViewHolder(_convertView);
            _convertView.setTag(holder);
        } else {
            holder = (ViewHolder) _convertView.getTag();
        }

        holder.getArtist().setText(mSongs.get(_position).getmArtist());
        holder.getSong().setText(mSongs.get(_position).getmTitle());
        holder.getCover().setImageBitmap(mSongs.get(_position).getmCover());
        holder.getMenu().setTag(_position);


        holder.getMenu().setOnClickListener(buildClickListener());
        return _convertView;
    }

    public void update(ArrayList<Song> _songs){
        mSongs.clear();
        mSongs = new ArrayList<Song>(_songs);
        Log.i(TAG, "Updating List Content with Songs: " + mSongs);
        this.notifyDataSetChanged();
    }

    protected View.OnClickListener buildClickListener(){
        return  null;
    }

    static class ViewHolder{

        private TextView mArtist;
        private TextView mSong;
        private ImageView mCover;
        private ImageView mMenu;

        ViewHolder(View v){
            mArtist = (TextView) v.findViewById(R.id.tv_artist);
            mSong = (TextView) v.findViewById(R.id.tv_song);
            mCover = (ImageView) v.findViewById(R.id.iv_cover);
            mMenu = (ImageView) v.findViewById(R.id.iv_more);
        }

        public TextView getArtist() {
            return mArtist;
        }

        public TextView getSong() {
            return mSong;
        }

        public ImageView getCover() {
            return mCover;
        }

        public ImageView getMenu() { return mMenu; }

    }


}
