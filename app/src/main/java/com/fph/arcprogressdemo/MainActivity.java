package com.fph.arcprogressdemo;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button mButton1;
    private Button mButton2;
    private ArcProgressView mArcProgressView;
    private int mRaiseQuota;
    private int mCurrentQuota = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        initListener();
    }

    private void initListener() {
        mButton1.setOnClickListener(new View.OnClickListener() {//获取额度
            @Override
            public void onClick(View view) {
                mRaiseQuota = (int) (Math.random() * 500);
                mArcProgressView.setProgressAndCurrentQuota(mRaiseQuota, mCurrentQuota);
            }
        });

        mButton2.setOnClickListener(new View.OnClickListener() {//领取额度
            @Override
            public void onClick(View view) {
                if (mRaiseQuota < 100) {
                    Toast.makeText(MainActivity.this, "只能提额整百额度", Toast.LENGTH_SHORT).show();
                    return;
                }
                final int quota = mRaiseQuota / 100 * 100;//只能提额整百
                mRaiseQuota = mRaiseQuota - quota;
                mCurrentQuota = mCurrentQuota + quota;
                mArcProgressView.setProgressAndCurrentQuota(mRaiseQuota, mCurrentQuota);
            }
        });

        mArcProgressView.setListener(new ArcProgressView.OnExplainClickListener() {
            @Override
            public void onClick() {//点击说明
                showExplainDialog();
            }
        });
    }

    //提额说明弹窗
    private void showExplainDialog() {
        final Dialog dialog = new Dialog(this, R.style.custom_dialog);
        View view = View.inflate(this, R.layout.dialog_explain, null);
        TextView tv_next = (TextView) view.findViewById(R.id.tv_next);
        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    private void initViews() {
        mButton1 = (Button) findViewById(R.id.btn1);
        mButton2 = (Button) findViewById(R.id.btn2);
        mArcProgressView = (ArcProgressView) findViewById(R.id.arc_progress_view);
        mArcProgressView.setCurrentQuota(mCurrentQuota);
    }
}
