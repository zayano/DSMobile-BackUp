package id.sentuh.digitalsignage;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.C.ContentType;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.util.List;

import id.sentuh.digitalsignage.R;

/** Manages the {@link ExoPlayer}, the IMA plugin and all video playback. */
public class PlayerManager implements AdsMediaSource.MediaSourceFactory {

    //  private final ImaAdsLoader adsLoader;
    private final DataSource.Factory manifestDataSourceFactory;
    private final DataSource.Factory mediaDataSourceFactory;

    private SimpleExoPlayer player;
    private long contentPosition;
    private File mp4File;

    public PlayerManager(Context context) {

        String adTag = context.getString(R.string.ad_tag_url);
//    adsLoader = new ImaAdsLoader(context, Uri.parse(adTag));
        manifestDataSourceFactory =
                new DefaultDataSourceFactory(
                        context, Util.getUserAgent(context, context.getString(R.string.application_name)));
        mediaDataSourceFactory =
                new DefaultDataSourceFactory(
                        context,
                        Util.getUserAgent(context, context.getString(R.string.application_name)),
                        new DefaultBandwidthMeter());
    }

    public void init(Context context, PlayerView playerView,String filePath) {
        // Create a default track selector.
        mp4File = new File(filePath);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        // Create a player instance.
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

        // Bind the player to the view.
        playerView.setPlayer(player);

        // This is the MediaSource representing the content media (i.e. not the ad).
//    String contentUrl = context.getString(R.string.content_url);
        MediaSource contentMediaSource = buildMediaSource(Uri.fromFile(mp4File));

        // Compose the content media source into a new AdsMediaSource with both ads and content.
//    MediaSource mediaSourceWithAds =
//        new AdsMediaSource(
//            contentMediaSource,
//            /* adMediaSourceFactory= */ this,
//            adsLoader,
//            playerView.getOverlayFrameLayout(),
//            /* eventHandler= */ null,
//            /* eventListener= */ null);

        // Prepare the player with the source.
        player.seekTo(contentPosition);
        player.prepare(contentMediaSource);
//    player.prepare(mediaSourceWithAds);
//    player.setPlayWhenReady(false);
    }
    public void start() {
        if(player!=null){
            // player.seekTo(0);
            player.setPlayWhenReady(true);
        }
    }
    public void reset() {
        if (player != null) {
            contentPosition = player.getContentPosition();
            player.release();
            player = null;
        }
    }
    public void stop() {
        player.stop();
    }
    public void release() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
//    adsLoader.release();
    }

    // AdsMediaSource.MediaSourceFactory implementation.

    @Override
    public MediaSource createMediaSource(Uri uri) {
        return buildMediaSource(uri);
    }

    @Override
    public int[] getSupportedTypes() {
        // IMA does not support Smooth Streaming ads.
        return new int[] {C.TYPE_DASH, C.TYPE_HLS, C.TYPE_OTHER};
    }

    // Internal methods.

    private MediaSource buildMediaSource(Uri uri) {
        @ContentType int type = Util.inferContentType(uri);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory),
                        manifestDataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory), manifestDataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
            default:
                throw new IllegalStateException("Unsupported type: " + type);
        }
    }

}

