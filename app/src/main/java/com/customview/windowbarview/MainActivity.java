package com.customview.windowbarview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.customview.windowbarview.view.WindowBarView;

public class MainActivity extends AppCompatActivity {

    TextView tv;
    WindowBarView wbv;
    EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv);
        wbv = (WindowBarView) findViewById(R.id.wbv);
        et = (EditText) findViewById(R.id.et);
        tv.setText(wbv.getProgress()+"");
        wbv.getonBarProgressListener(new WindowBarView.setonBarTouthnListener() {
            @Override
            public void GetProgress(int progress) {
                tv.setText(progress+"");
            }
        });
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().isEmpty()){
                    wbv.setProgress(0);
                }else{
                    wbv.setProgress(Integer.parseInt(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                tv.setText(wbv.getProgress()+"");
            }
        });
    }
}
