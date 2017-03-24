package com.ezuikit.open;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ezuikit.open.scan.main.CaptureActivity;
import com.ezvizuikit.open.EZUIKit;
import com.videogo.openapi.EZOpenSDK;

import static com.ezuikit.open.PlayActivity.APPKEY;
import static com.ezuikit.open.PlayActivity.AccessToekn;
import static com.ezuikit.open.PlayActivity.PLAY_URL;

public class MainActivity extends Activity implements View.OnClickListener {

    /**
     * 二维码扫描按钮
     */
    private Button mButtonCode;
    /**
     * 预览播放按钮
     */
    private Button mButtonPlay;

    /**
     * 清除播放缓存参数按钮
     */
    private Button mButtonClear;

    /**
     * 开发者申请的Appkey
     */
    private String mAppKey;

    /**
     * 授权accesstoken
     */
    private String mAccessToken;

    /**
     * 播放url：ezopen协议
     */
    private String mUrl;

    private EditText mAppkeyEditText;

    private EditText mAccessTokenEditText;

    private EditText mUrlEditText;

    private TextView mTextViewVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtonCode = (Button) findViewById(R.id.btn_code);
        mButtonPlay = (Button) findViewById(R.id.btn_play);
        mButtonClear = (Button) findViewById(R.id.btn_clear_cache);
        mAppkeyEditText = (EditText) findViewById(R.id.edit_appkey);
        mAccessTokenEditText = (EditText) findViewById(R.id.edit_accesstoken);
        mUrlEditText = (EditText) findViewById(R.id.edit_url);
        mTextViewVersion = (TextView) findViewById(R.id.text_version);
        mButtonCode.setOnClickListener(this);
        mButtonPlay.setOnClickListener(this);
        mButtonClear.setOnClickListener(this);
        mTextViewVersion.setText(EZUIKit.EZUIKit_Version+" (SDK "+ EZOpenSDK.getVersion()+")");
        getDefaultParams();
    }

    @Override
    public void onClick(View view) {
        if (view == mButtonCode) {
            //跳转到二维码扫描页面扫描二维码获取预览所需参数appkey、accesstoken、url
            Intent intent = new Intent(this, CaptureActivity.class);
            startActivityForResult(intent,200);
        }else if(view  == mButtonClear){
            //弹出清除数据确认框
            showClearDialog();
        } else if(view == mButtonPlay){
            mAppKey = mAppkeyEditText.getText().toString().trim();
            mAccessToken = mAccessTokenEditText.getText().toString().trim();
            mUrl = mUrlEditText.getText().toString().trim();
            if (TextUtils.isEmpty(mAppKey)){
                Toast.makeText(this,"appkey can not be null",Toast.LENGTH_LONG).show();
                return;
            }
            if (TextUtils.isEmpty(mAccessToken)){
                Toast.makeText(this,"accesstoken can not be null",Toast.LENGTH_LONG).show();
                return;
            }
            if (TextUtils.isEmpty(mUrl)){
                Toast.makeText(this,"url can not be null",Toast.LENGTH_LONG).show();
                return;
            }
            saveDefaultParams();
            //启动播放页面
            PlayActivity.startPlayActivity(this,mAppKey,mAccessToken,mUrl);

            //启动播放页面
            PlayActivity.startPlayActivity(this,"76d8a02ae81a4260a02e470ebb48077d","at.ao8hv9xl265kopuj660q9igq00zqe2i0-3ursg79f60-1dr0iky-0saaemrff\n",mUrl);
        }
    }

    /**
     * 清除缓存弹框
     */
    private void showClearDialog() {
        AlertDialog.Builder exitDialog = new AlertDialog.Builder(this);
        exitDialog.setMessage(R.string.string_btn_clear_cache_sure);
        exitDialog.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearDefaultParams();
            }
        });
        exitDialog.setNegativeButton(android.R.string.cancel,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        exitDialog.show();
    }

    //二维码扫描返回值获取
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == 200){
                String appkey = data.getStringExtra(APPKEY);
                String accesstoken = data.getStringExtra(AccessToekn);
                String playUrl = data.getStringExtra(PLAY_URL);
                if (!TextUtils.isEmpty(appkey)){
                    mAppKey = appkey;
                    mAppkeyEditText.setText(appkey);
                }
                if (!TextUtils.isEmpty(accesstoken)){
                    mAccessToken = accesstoken;
                    mAccessTokenEditText.setText(accesstoken);
                }
                if (!TextUtils.isEmpty(playUrl)){
                    mUrl = playUrl;
                    mUrlEditText.setText(playUrl);
                }
                saveDefaultParams();
            }
        }
    }

    /**
     * 获取缓存播放参数
     */
    private void getDefaultParams(){
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name),0);
        mAppKey = sharedPreferences.getString(PlayActivity.APPKEY,"");
        mAccessToken = sharedPreferences.getString(PlayActivity.AccessToekn,"");
        mUrl = sharedPreferences.getString(PlayActivity.PLAY_URL,"");
        mAppkeyEditText.setText(mAppKey);
        mAccessTokenEditText.setText(mAccessToken);
        mUrlEditText.setText(mUrl);
    }

    /**
     * 缓存播放参数
     */
    private void saveDefaultParams(){
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name),0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PlayActivity.APPKEY,mAppKey);
        editor.putString(PlayActivity.AccessToekn,mAccessToken);
        editor.putString(PlayActivity.PLAY_URL,mUrl);
        editor.commit();
    }

    /**
     * 清除播放参数缓存
     */
    private void clearDefaultParams(){
        mAppKey = "";
        mAccessToken = "";
        mUrl = "";
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name),0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PlayActivity.APPKEY,mAppKey);
        editor.putString(PlayActivity.AccessToekn,mAccessToken);
        editor.putString(PlayActivity.PLAY_URL,mUrl);
        editor.commit();
        mAppkeyEditText.setText(mAppKey);
        mAccessTokenEditText.setText(mAccessToken);
        mUrlEditText.setText(mUrl);
    }
}
