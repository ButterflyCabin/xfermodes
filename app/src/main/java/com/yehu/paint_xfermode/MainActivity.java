package com.yehu.paint_xfermode;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yehu.paint_xfermode.view.XferdemoView;

public class MainActivity extends AppCompatActivity {

    private TextView mTvTips;
    private XferdemoView mXfermode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvTips = findViewById(R.id.tv_tips);
        mXfermode = findViewById(R.id.xfv_fermode);

    }

}
