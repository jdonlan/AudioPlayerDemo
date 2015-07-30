package joshdonlan.com.audioplayerdemo.objects;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import joshdonlan.com.audioplayerdemo.R;

/**
 * Created by jdonlan on 7/28/15.
 */
public class ScanMusicTask extends AsyncTask<Void, Void, ArrayList<Song>> {

    private static final String TAG = "ScanMusicTask";
    private Context mContext;
    private ProgressDialog mProgressDialog;

    public interface ScanMusicListener{
        public void updateSongData(ArrayList<Song> _songs);
    }

    public ScanMusicTask(Context _context){
        mContext = _context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(mContext.getResources().getString(R.string.dialog_update_song));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
    }

    @Override
    protected ArrayList<Song> doInBackground(Void... params) {

        File musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        File[] files = musicDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(".mp3")) return true;
                return false;
            }
        });

        ArrayList<Song> songs = new ArrayList<Song>();

        MediaMetadataRetriever meta = new MediaMetadataRetriever();
        for(File file : files){
            meta.setDataSource(mContext, Uri.parse(file.getPath()));
            String artist = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String album = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String title = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            byte[] art = meta.getEmbeddedPicture();
            Bitmap cover = BitmapFactory.decodeByteArray(art, 0, art.length);
            Song song = new Song(artist, title, album, cover);
            songs.add(song);
        }

        return songs;
    }

    @Override
    protected void onPostExecute(ArrayList<Song> _songs) {
        super.onPostExecute(_songs);

        if(mContext instanceof ScanMusicListener) ((ScanMusicListener) mContext).updateSongData(_songs);
        mProgressDialog.dismiss();
    }
}
