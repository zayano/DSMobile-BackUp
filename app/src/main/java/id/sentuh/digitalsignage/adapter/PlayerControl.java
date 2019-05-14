package id.sentuh.digitalsignage.adapter;

import android.widget.MediaController;

import com.google.android.exoplayer2.ExoPlayer;

/**
 * Created by sony on 2/18/2018.
 */

public class PlayerControl implements MediaController.MediaPlayerControl {
    private final ExoPlayer exoPlayer;

    public PlayerControl(ExoPlayer exoPlayer) {
        this.exoPlayer = exoPlayer;
    }
    @Override
    public void start() {
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void pause() {
        exoPlayer.setPlayWhenReady(false);
    }

    @Override
    public int getDuration() {
        return (int)exoPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return (int) exoPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int i) {

    }

    @Override
    public boolean isPlaying() {
        return exoPlayer.getPlayWhenReady();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
