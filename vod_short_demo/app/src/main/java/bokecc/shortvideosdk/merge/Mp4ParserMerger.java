package bokecc.shortvideosdk.merge;

import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;

import bokecc.shortvideosdk.model.MediaObject;

/**
 * mp4合并类
 */

public class Mp4ParserMerger {

    public static interface MergeListener{
        void mergeFinish();

        void mergeFail(Exception e);
    }

    public static void merge(final LinkedList<MediaObject.MediaPart> meidaParts, final String outName, final MergeListener mergeListener) {
        retryCount = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                startMerge(meidaParts, outName, mergeListener);
            }
        }).start();
    }

    static String TAG = "Mp4ParserMerger";
    static int retryCount;
    private static void startMerge(LinkedList<MediaObject.MediaPart> meidaParts, String outName, MergeListener mergeListener) {
        int count = meidaParts.size();

        Movie[] inMovies = new Movie[count];
        FileOutputStream fos = null;
        FileChannel fco = null;

        try {
            for (int i=0; i<count; i++) {
                String path = meidaParts.get(i).mediaPath;
                inMovies[i] = MovieCreator.build(path);
            }

            List<Track> videoTracks = new LinkedList<>();
            List<Track> audioTracks = new LinkedList<>();

            for (Movie m: inMovies) {

                for (Track t: m.getTracks()) {
                    if (t.getHandler().equals("soun")) {
                        audioTracks.add(t);
                    } else if (t.getHandler().equals("vide")) {
                        videoTracks.add(t);
                    }
                }
            }

            Movie result = new Movie();

            if (audioTracks.size() > 0) {
                result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
            }

            if (videoTracks.size() > 0) {
                result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
            }

            Container mp4File = new DefaultMp4Builder().build(result);
            File storagePath = new File(outName);
            storagePath.getParentFile().mkdirs();

            fos = new FileOutputStream(storagePath);

            fco = fos.getChannel();

            mp4File.writeContainer(fco);

            mergeListener.mergeFinish();

        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());

            if (retryCount++ < 10) {

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                startMerge(meidaParts, outName, mergeListener); //有异常时，重新尝试合并
            } else {
                mergeListener.mergeFail(e);
            }


        } finally {
            try {
                if (fco != null) {
                    fco.close();
                }
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }

            try {
                if (fos != null) {
                    fos.close();
                }

            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
    }
}
