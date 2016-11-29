package io.sichacvah.radioplayer;

import android.content.Context;
import android.net.Uri; 
import android.util.Log;
import android.view.View;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.FrameworkSampleSource;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;
import android.content.Intent;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.ReactMethod;

public class PlayerModule extends ReactContextBaseJavaModule {
    static ExoPlayer exoPlayer;
    static TrackRenderer audioRenderer;
    private final static String TAG = "PLAYER_MODULE";
    public Context mContext = null;

    @Override
    public String getName() {
        return "PlayerModule";
    }

    public PlayerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.mContext = reactContext;
    }


    private void sendEvent(
        String eventName,
        Object params
    ) {
        getReactApplicationContext()
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
        Log.i("ReactNativeRadioPlayer", "PlayerModule: sendEvent (to JS): " + eventName);
    }

    @ReactMethod
    public void startPlayerService(String URL) {
        Intent serviceIntent = new Intent(mContext, NotificationService.class);
        serviceIntent.putExtra("RADIO_PATH", URL);
        serviceIntent.setAction("STARTFOREGROUND_ACTION");
        mContext.startService(serviceIntent);
    }

    @ReactMethod
    public void start(String URL) {
        if (exoPlayer != null) {
            exoPlayer.stop();
        }

        Uri URI = Uri.parse(URL);
        FrameworkSampleSource sampleSource = new FrameworkSampleSource(mContext, URI, null);
        audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource, null, true);
        exoPlayer = ExoPlayer.Factory.newInstance(1);
        exoPlayer.prepare(audioRenderer);
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.addListener(new ExoPlayer.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == 4) {
                    WritableMap params = Arguments.createMap();
                    sendEvent("start", params);
                }
            }

            @Override
            public void onPlayWhenReadyCommitted() {}

            @Override
            public void onPlayerError(ExoPlaybackException error) {}
        });

    }

    @ReactMethod
    public void stop() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            WritableMap params = Arguments.createMap();
            sendEvent("stop", params);
        }
    }

    @ReactMethod
    public void stopPlayerService() {
        stop();
        Intent serviceIntent = new Intent(mContext, NotificationService.class);
        serviceIntent.setAction("PLAY_ACTION");
        mContext.startService(serviceIntent);
    }

    @ReactMethod
    public void setVolume(float volume) {
        if (exoPlayer != null) {
            exoPlayer.sendMessage(audioRenderer, MediaCodecAudioTrackRenderer.MSG_SET_VOLUME, volume);
            WritableMap params = Arguments.createMap();
            double dVolume = (double) volume;
            params.putDouble("volume", dVolume);
            sendEvent("volume_changed", params);
        }
    }
}