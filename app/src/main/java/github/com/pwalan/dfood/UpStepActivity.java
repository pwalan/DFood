package github.com.pwalan.dfood;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import github.com.pwalan.dfood.utils.QCloud;
import github.com.pwalan.dfood.utils.SelectPicActivity;


public class UpStepActivity extends Activity {
    //本地广播
    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;

    //startActivityForResult需要的intent
    private Intent lastIntent ;

    private TextView tv_num;
    private EditText et_step;
    private ImageView iv_step;
    private Button btn_finish;

    private String picPath;
    private Bitmap bitmap;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_step);

        lastIntent = getIntent();
        Bundle data=lastIntent.getExtras();

        //本地广播接收初始化
        localBroadcastManager=LocalBroadcastManager.getInstance(this);
        intentFilter=new IntentFilter();
        intentFilter.addAction("github.com.pwalan.dfood.LOCAL_BROADCAST");
        localReceiver=new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);

        //腾讯云上传初始化
        QCloud.init(this);
        QCloud.resultUrl=null;

        position=data.getInt("position");
        tv_num=(TextView)findViewById(R.id.tv_num);
        tv_num.setText("第 "+(position+1)+" 步");
        et_step=(EditText)findViewById(R.id.et_step);
        et_step.setText(data.getString("content"));
        iv_step=(ImageView)findViewById(R.id.iv_step);
        if(!data.getString("picPath").isEmpty()){
            bitmap= BitmapFactory.decodeFile(data.getString("picPath"));
            iv_step.setImageBitmap(bitmap);
        }
        iv_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpStepActivity.this,SelectPicActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        btn_finish=(Button)findViewById(R.id.btn_finish);
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putInt("position",position);
                bundle.putString("content",et_step.getText().toString().trim());
                bundle.putString("picPath",picPath);
                bundle.putString("url",QCloud.resultUrl);
                lastIntent.putExtras(bundle);
                //设置结果
                setResult(Activity.RESULT_OK, lastIntent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==Activity.RESULT_OK && requestCode == 0)
        {
            picPath = data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH);
            Bitmap bm = BitmapFactory.decodeFile(picPath);
            iv_step.setImageBitmap(bm);
            QCloud.UploadPic(picPath, UpStepActivity.this);
            //更新图库
            Uri localUri = Uri.fromFile(new File(picPath));
            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            sendBroadcast(localIntent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //接受广播
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            btn_finish.setClickable(true);
            btn_finish.setBackgroundColor(getResources().getColor(R.color.tianyi));
        }
    }
}
