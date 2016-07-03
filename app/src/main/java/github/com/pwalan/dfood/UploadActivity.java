package github.com.pwalan.dfood;

import android.app.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import github.com.pwalan.dfood.myview.RoundImageView;
import github.com.pwalan.dfood.utils.ListViewUtils;
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

    // 顶部栏显示
    private ImageView titleLeftImv;
    private TextView titleTv;
    private ImageView img_up;

    //食谱信息
    private ImageView iv_recipe;
    private EditText et_rname,et_rcontent;
    private Spinner sp_season;
    private ListView step_list;
    private ImageView imv_add;

    private App app;
    private Bitmap bitmap;
    private String url=null;
    private int step_num=1;
    private String season;  //选择的季节
    List<Map<String, Object>> listItems;
    Map<String, Object> listItem;
    List<String> urls;
    SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        progressDialog = new ProgressDialog(this);

        app=(App)getApplication();

        /**
         * 初始化页面标题栏
         */
        titleLeftImv = (ImageView) findViewById(R.id.title_imv);
        titleLeftImv.setImageResource(R.drawable.exit);
        //顶部左侧的图标点击事件
        titleLeftImv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //顶部右侧的加号图标点击启动上传
        img_up=(ImageView)findViewById(R.id.img_up);
        img_up.setImageResource(R.drawable.myup);
        img_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                season=(String)sp_season.getSelectedItem();
                Toast.makeText(UploadActivity.this, season, Toast.LENGTH_SHORT).show();
            }
        });
        //顶部标签
        titleTv = (TextView) findViewById(R.id.title_text_tv);
        titleTv.setText("食谱发布");

        /**
         * 初始化食谱部分
         */
        iv_recipe=(ImageView)findViewById(R.id.iv_recipe);
        et_rname=(EditText)findViewById(R.id.et_rname);
        et_rcontent=(EditText)findViewById(R.id.et_rcontent);
        sp_season=(Spinner)findViewById(R.id.sp_season);
        /**
         * 初始化步骤列表
         */
        step_list=(ListView)findViewById(R.id.step_list);
        listItems = new ArrayList<Map<String, Object>>();
        listItem = new HashMap<String, Object>();
        listItem.put("num", step_num + ".");
        listItems.add(listItem);
        simpleAdapter= new SimpleAdapter(UploadActivity.this, listItems, R.layout.step_item,
                new String[]{"num", "content", "pic"},
                new int[]{R.id.tv_num, R.id.tv_step, R.id.iv_step});
        step_list.setAdapter(simpleAdapter);
        ListViewUtils.setListViewHeightBasedOnChildren(step_list);

        urls=new ArrayList<String>();
        //添加步骤的按钮
        imv_add=(ImageView)findViewById(R.id.imv_add);
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
