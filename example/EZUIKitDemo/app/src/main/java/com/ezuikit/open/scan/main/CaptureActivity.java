package com.ezuikit.open.scan.main;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.ezuikit.open.MainActivity;
import com.ezuikit.open.PlayActivity;
import com.ezuikit.open.R;
import com.ezuikit.open.scan.camera.CameraManager;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.videogo.util.LocalValidate;

import java.io.IOException;
import java.util.Vector;

/**
 * 二维码扫描页面
 */
public class CaptureActivity extends Activity implements SurfaceHolder.Callback {

    private final static String TAG = "CaptureFragment";
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private InactivityTimer mInactivityTimer;

    private CameraManager cameraManager;
    private LocalValidate mLocalValidate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        cameraManager = new CameraManager((getApplicationContext()));
        hasSurface = false;
        mInactivityTimer = new InactivityTimer(this);
        mLocalValidate = new LocalValidate();
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        surfaceHolder = surfaceView.getHolder();
        viewfinderView.setCameraManager(cameraManager);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasSurface) {
            initCamera();
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;
        playBeep = true;
        AudioManager audioService = (AudioManager)getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        cameraManager.closeDriver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mInactivityTimer.shutdown();
        super.onDestroy();
    }


    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }


    /**
     * A valid barcode has been found, so give an indication of success and show the results.
     *
     * @param resultString
     * @param barcode
     *            A greyscale bitmap of the camera data which was decoded.
     */
    public void handleDecode(String resultString, Bitmap barcode) {
        mInactivityTimer.onActivity();
        playBeepSoundAndVibrate();

        if (resultString == null) {
            Log.e(TAG, "handleDecode-> resultString is null");
            return;
        }
        Log.d(TAG, "handleDecode-> resultString = "+resultString);
        Gson gson = new Gson();
        PlayParams playParams = gson.fromJson(resultString,PlayParams.class);
        if (playParams != null
                && !TextUtils.isEmpty(playParams.AppKey)
                && !TextUtils.isEmpty(playParams.AccessToken)
                && !TextUtils.isEmpty(playParams.Url)){
            Intent intent = new Intent();
            intent.putExtra(PlayActivity.APPKEY,playParams.AppKey);
            intent.putExtra(PlayActivity.AccessToekn,playParams.AccessToken);
            intent.putExtra(PlayActivity.PLAY_URL,playParams.Url);
            setResult(MainActivity.RESULT_OK, intent);
            finish();
        }
    }

//    private static String getVerifyCode(String string){
//        if (TextUtils.isEmpty(string)){
//            return null;
//        }
//        String[] keys = string.split(":");
//        if (keys.length > 1){
//            if ("AES".equals(keys[0])){
//                try {
//                    return com.ezuikit.open.AESCipher.decrypt("2e244bd87e2a473e8a73c7b714b077b2",keys[1]);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return keys[1];
//            }
//        }
//        return string;
//    }
//
//    public static EZPlayURLParams getEZPlayURLParams(String url) {
//        EZPlayURLParams urlparams = null;
//        if (url.startsWith("ezopen://")) {
//            urlparams = new EZPlayURLParams();
//            String stringkey = url.replace("ezopen://", "");
//
//            //解析视频加密
//            String[] stringKeys = stringkey.split("@");
//            if (stringKeys.length > 1) {
//                String verifyCode = getVerifyCode(stringKeys[0]);
//                if (!TextUtils.isEmpty(verifyCode)) {
//                    urlparams.verifyCode = verifyCode;
//                }
//            }
//            //解析host
//            String hosts = "";
//            if (stringKeys.length > 1) {
//                hosts = stringKeys[1];
//            } else {
//                hosts = stringKeys[0];
//            }
//            String[] strings = hosts.split("/");
//            Log.d("EZPlayURLParams", strings[strings.length - 1]);
//            //ezopen://open.ys7.com/序列号/通道号.清晰度.live
//            //ezopen://ABCDEF@open.ys7.com/123456789/1.cloud.rec?begin=1457420564508&end=1457420864508
//            if (strings.length < 3) {
//                return null;
//            }
//            urlparams.host = strings[0];
//            urlparams.deviceSerial = strings[1];
//            String param = strings[2];
//            String[] s1 = param.split("\\?");
//            Log.d("EZPlayURLParams", s1[0]);
//            String[] s2 = s1[0].split("\\.");
//            if (s2.length < 2) {
//                return null;
//            }
//            urlparams.cameraNo = Integer.parseInt(s2[0]);
//            if (s2.length == 2){
//                if (s2[1].equalsIgnoreCase("live")) {
//                    urlparams.type = 1;
//                    urlparams.videoLevel = 1;
//                }else {
//                    return null;
//                }
//            }else{
//                //直播
//                if (s2[2].equalsIgnoreCase("live")) {
//                    urlparams.type = 1;
//                    urlparams.videoLevel = s2[1].equalsIgnoreCase("HD") ? 2 : 1;
//                } else if (s2[2].equalsIgnoreCase("rec")) {
//                    //录像回放
//                    urlparams.type = 2;
//                    if (s2[1].equalsIgnoreCase("cloud")) {
//                        // TODO: 2017/2/13 云存储录像
//                        urlparams.recodeType = 2;
//                    } else if (s2[1].equalsIgnoreCase("local")) {
//                        // TODO: 2017/2/13 本地存储录像回放
//                        urlparams.recodeType = 1;
//                    }
//                }else{
//                    return null;
//                }
//            }
//            if (s1.length >1){
//                // TODO: 2017/2/18 后面带参数处理，回放时间的获取
//            }
//        }
//        return urlparams;
//    }
//


    private void initCamera() {
        try {
            cameraManager.openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet, cameraManager);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);
            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    class PlayParams{
        public String AppKey;
        public String AccessToken;
        public String Url;
    }
}
