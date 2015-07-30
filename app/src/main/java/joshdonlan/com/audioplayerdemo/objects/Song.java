package joshdonlan.com.audioplayerdemo.objects;

import android.graphics.Bitmap;

/**
 * Created by jdonlan on 7/28/15.
 */
public class Song {

    private String mTitle;
    private String mArtist;
    private String mAlbum;
    private Bitmap mCover;

    public Song(String _artist, String _title, String _album, Bitmap _cover){
        mArtist = _artist;
        mTitle = _title;
        mAlbum = _album;
        mCover = _cover;
    }


    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmArtist() {
        return mArtist;
    }

    public void setmArtist(String mArtist) {
        this.mArtist = mArtist;
    }

    public String getmAlbum() {
        return mAlbum;
    }

    public void setmAlbum(String mAlbum) {
        this.mAlbum = mAlbum;
    }

    public Bitmap getmCover() {
        return mCover;
    }

    public void setmCover(Bitmap mCover) {
        this.mCover = mCover;
    }

}
