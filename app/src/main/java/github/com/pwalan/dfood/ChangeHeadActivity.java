package github.com.pwalan.dfood;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import github.com.pwalan.dfood.utils.C;
import github.com.pwalan.dfood.utils.QCloud;
import github.com.pwalan.dfood.utils.SelectPicActivity;


public class ChangeHeadActivity extends Activity {
    //获取上传
    protected static final int UPLOAD = 1;
    //获取数据
    protected static final int GET_DATA = 2;

    private ImageView img_head;
    private Button btn_change;

    private App app;
    private JSONObject response;
    private String picPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_head);

        app = (App) getApplication();

        //腾讯云上传初始化
        QCloud.init(this);
        QCloud.resultUrl=null;

        img_head = (ImageView) findViewById(R.id.img_head);
        img_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangeHeadActivity.this, SelectPicActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        btn_change=(Button)findViewById(R.id.btn_change);
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(QCloud.resultUrl!=null){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HashMap map = new HashMap();
                            map.put("uid",app.getUid());
                            map.put("head",QCloud.resultUrl);
                            response = C.asyncPost(app.getServer() + "changeHead", map);
                            handler.sendEmptyMessage(GET_DATA);
                        }
                    }).start();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 0) {
            picPath = data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH);
            Log.i("dfood", "最终选择的图片=" + picPath);
            Bitmap bm = BitmapFactory.decodeFile(picPath);
            img_head.setImageBitmap(bm);
            handler.sendEmptyMessage(UPLOAD);

            //更新图库
            Uri localUri = Uri.fromFile(new File(picPath));
            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            sendBroadcast(localIntent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPLOAD:
                    if (picPath != null) {
                        QCloud.UploadPic(picPath, ChangeHeadActivity.this);
                    } else {
                        Toast.makeText(ChangeHeadActivity.this, "上传的文件路径出错", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case GET_DATA:
                    try {
                        String data=response.getString("data");
                        if(data.equals("changed")){
                            Toast.makeText(ChangeHeadActivity.this,"更换成功！",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

}
