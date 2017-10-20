package com.ezuikit.open;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelecteAreaActivity extends Activity implements View.OnClickListener {

    /**
     * 中文国内版本
     */
    private Button mLocalBtn;

    /**
     * 英文海外版本
     */
    private Button mGlobalBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecte_area);
        mLocalBtn = (Button) findViewById(R.id.btn_local);
        mGlobalBtn = (Button) findViewById(R.id.btn_global);
        mLocalBtn.setOnClickListener(this);
        mGlobalBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent  intent = new Intent(this,MainActivity.class);
        if (v == mLocalBtn){
            intent.putExtra(PlayActivity.Global_AreanDomain,false);
        }else if(v == mGlobalBtn){
            intent.putExtra(PlayActivity.Global_AreanDomain,true);
        }
        startActivity(intent);
        finish();
    }
}
