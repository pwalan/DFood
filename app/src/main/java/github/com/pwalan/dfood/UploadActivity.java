package github.com.pwalan.dfood;

import android.app.Activity;

import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import github.com.pwalan.dfood.myview.RoundImageView;
import github.com.pwalan.dfood.utils.QCloud;
import github.com.pwalan.dfood.utils.SelectPicActivity;


public class UploadActivity extends Activity{
    /**
     * 去上传文件
     */
    protected static final int TO_UPLOAD_FILE = 1;
    /**
     * 选择文件
     */
    public static final int TO_SELECT_PHOTO = 2;

    /**
     * 要上传图片的本地地址
     */
    private String picPath = null;


    private ProgressDialog progressDialog;
    private App app;

    private Bitmap bitmap;
    private String url=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        progressDialog = new ProgressDialog(this);

        app=(App)getApplication();

        //腾讯云上传初始化
        QCloud.init(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==Activity.RESULT_OK && requestCode == TO_SELECT_PHOTO)
        {
            picPath = data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH);
            Log.i("dfood", "最终选择的图片=" + picPath);
            Bitmap bm = BitmapFactory.decodeFile(picPath);

            //更新图库

            Uri localUri = Uri.fromFile(new File(picPath));
            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            sendBroadcast(localIntent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TO_UPLOAD_FILE:
                    QCloud.UploadPic(picPath, UploadActivity.this);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

}
