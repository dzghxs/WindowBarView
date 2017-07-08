package com.customview.windowbarview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.customview.windowbarview.view.WindowBarView;

public class MainActivity extends AppCompatActivity {

    TextView tv;
    WindowBarView wbv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv);
        wbv = (WindowBarView) findViewById(R.id.wbv);
        tv.setText(wbv.getProgress()+"");
        wbv.getonBarProgressListener(new WindowBarView.setonBarTouthnListener() {
            @Override
            public void GetProgress(int progress) {
                tv.setText(progress+"");
            }
        });
    }
}
