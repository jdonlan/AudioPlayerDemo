package joshdonlan.com.audioplayerdemo.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by jdonlan on 7/28/15.
 */
public class Song implements Parcelable{

    private String mTitle;
    private String mArtist;
    private String mAlbum;
    private byte[] mCover;
    private String mPath;
    private String mDuration;

    public Song(String _artist, String _title, String _album, byte[] _cover, String _path, String _duration){
        mArtist = _artist;
        mTitle = _title;
        mAlbum = _album;
        mCover = _cover;
        mPath = _path;
        mDuration = _duration;
    }


    public String getmTitle() {
        return mTitle;
    }

    public String getmArtist() {
        return mArtist;
    }

    public String getmAlbum() {
        return mAlbum;
    }

    public Bitmap getmCover() {
        byte[] art = mCover;
        Bitmap cover = BitmapFactory.decodeByteArray(art, 0, art.length);
        return cover;
    }

    public String getPath() { return mPath; }

    public String getDuration() { return mDuration; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mArtist);
        dest.writeString(mAlbum);
        dest.writeString(mPath);
        dest.writeString(mDuration);
        dest.writeInt(mCover.length);
        dest.writeByteArray(mCover);
    }

    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>(){
        public Song createFromParcel(Parcel parcel) {
            return new Song(parcel);
        }
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public Song(Parcel parcel){
        mArtist = parcel.readString();
        mTitle = parcel.readString();
        mAlbum = parcel.readString();
        mPath = parcel.readString();
        mDuration = parcel.readString();
        mCover = new byte[parcel.readInt()];
        parcel.readByteArray(mCover);
    }

}
