package io.sichacvah.radioplayer;

import android.content.Context;
import android.net.Uri; 
import android.util.Log;
import android.view.View;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.goolge.android.exoplayer.FrameworkSampleSource;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;

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
    public static void start(String URL) {
        if (exoPlayer != null) {
            exoPlayer.stop();
        }

        Uri URI = Uri.parse(URL);
        FrameworkSampleSource sampleSource = new FrameworkSampleSource(this.mContext, URI, null);
        audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource, null, true);
        exoPlayer = ExoPlayer.Factory.newInstance(1);
        exoPlayer.prepare(audioRenderer);
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.addListener(new ExoPlayer.Listener() {
            @Override
            public void onPlayerStateChange(boolean playWhenReady, int playbackState) {
                if (playbackState == 4) {
                    WritableMap params = Arguments.createMap();
                    sendEvent("start", params);
                }
            }

            @Override
            public void onPlayerWhenReadyCommited() {}

            @Override
            public void onPlayerError(ExoPlaybackException error) {}
        });

    }

    @ReactMethod
    public static void stop() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            WritableMap params = Arguments.createMap();
            sendEvent("stop", params);
        }
    }

    @ReactMethod
    public static void setVolume(float volume) {
        if (exoPlayer != null) {
            exoPlayer.sendMessage(audioRenderer, MediaCodecAudioTrackRenderer.MSG_SET_VOLUME, volume);
            WritableMap params = Arguments.createMap();
            params.putFloat("volume", volume);
            sendEvent("volume_changed", params);
        }
    }
}