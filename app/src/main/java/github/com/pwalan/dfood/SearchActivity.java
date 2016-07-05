package github.com.pwalan.dfood;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import github.com.pwalan.dfood.utils.QCloud;


public class SearchActivity extends Activity {
    //获取数据
    protected static final int GET_DATA = 1;

    //本地广播
    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;

    private App app;

    private ImageView iv_down;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        app=(App)getApplication();

        //腾讯云上传下载初始化
        QCloud.init(this);

        //本地广播接收初始化
        localBroadcastManager=LocalBroadcastManager.getInstance(this);
        intentFilter=new IntentFilter();
        intentFilter.addAction("github.com.pwalan.dfood.LOCAL_BROADCAST");
        localReceiver=new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);

        iv_down=(ImageView)findViewById(R.id.iv_down);
        iv_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url="http://pwalan-10035979.image.myqcloud.com/test_fileId_2c01ef1f-a261-4377-9226-f2a24762e28b";
                QCloud.downloadPic(url,SearchActivity.this);
            }
        });

    }

    //接受广播
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            iv_down.setImageBitmap(QCloud.bmp);
        }
    }

}
