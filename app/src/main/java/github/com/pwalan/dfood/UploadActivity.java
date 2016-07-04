package github.com.pwalan.dfood;

import android.app.Activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
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
import github.com.pwalan.dfood.utils.ListViewBinder;
import github.com.pwalan.dfood.utils.ListViewUtils;
import github.com.pwalan.dfood.utils.QCloud;
import github.com.pwalan.dfood.utils.SelectPicActivity;


public class UploadActivity extends Activity{
    //去上传文件
    protected static final int TO_UPLOAD_FILE = 1;
    //选择文件
    public static final int TO_SELECT_PHOTO = 2;
    //编辑步骤
    public static final int EDIT_STEP=3;

    //本地广播
    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;

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
    private ImageView imv_add,imv_delete;

    private App app;
    private Bitmap bitmap;
    private String recipeurl=null; //菜谱的介绍图片
    private int step_num=1;  //步骤数
    private String season;  //选择的季节
    List<Map<String, Object>> listItems;
    Map<String, Object> listItem;
    List<String> picUrls;  //步骤图片的url
    List<String> picPaths;  //步骤图片在手机上的位置
    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        progressDialog = new ProgressDialog(this);

        app=(App)getApplication();

        //本地广播接收初始化
        localBroadcastManager=LocalBroadcastManager.getInstance(this);
        intentFilter=new IntentFilter();
        intentFilter.addAction("github.com.pwalan.dfood.LOCAL_BROADCAST");
        localReceiver=new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);

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
                String rname=et_rname.getText().toString().trim();
                String rcontent=et_rcontent.getText().toString().trim();
                season=(String)sp_season.getSelectedItem();
                String stepCon="",stepUrl="";
                for(int i=0;i<listItems.size();i++){
                    stepCon+=(listItems.get(i).get("content")+" ");
                    stepUrl+=(picUrls.get(i)+" ");
                }
                Log.i("dfood","菜名："+rname+"\n介绍："+rcontent+"\n介绍的图片："+recipeurl+"\n步骤："+stepCon+"\n图片："+stepUrl);
                if(rname.isEmpty()){
                    Toast.makeText(UploadActivity.this,"请添加菜名！",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(UploadActivity.this,"上传中...",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //顶部标签
        titleTv = (TextView) findViewById(R.id.title_text_tv);
        titleTv.setText("菜谱发布");

        /**
         * 初始化食谱部分
         */
        iv_recipe=(ImageView)findViewById(R.id.iv_recipe);
        iv_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadActivity.this,SelectPicActivity.class);
                startActivityForResult(intent, TO_SELECT_PHOTO);
            }
        });
        et_rname=(EditText)findViewById(R.id.et_rname);
        et_rcontent=(EditText)findViewById(R.id.et_rcontent);
        sp_season=(Spinner)findViewById(R.id.sp_season);

        /**
         * 初始化步骤列表
         */
        step_list=(ListView)findViewById(R.id.step_list);
        listItems = new ArrayList<Map<String, Object>>();
        listItem = new HashMap<String, Object>();
        picUrls=new ArrayList<String>();
        picPaths=new ArrayList<String>();

        listItem.put("num", step_num + ".");
        listItem.put("content", "请添加");
        listItems.add(listItem);
        picUrls.add("");
        picPaths.add("");

        adapter= new SimpleAdapter(UploadActivity.this, listItems, R.layout.step_item,
                new String[]{"num", "content", "pic"},
                new int[]{R.id.tv_num, R.id.tv_step, R.id.iv_step});
        adapter.setViewBinder(new ListViewBinder());

        step_list.setAdapter(adapter);
        step_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(UploadActivity.this, "第" + (position + 1) + "步", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(UploadActivity.this,UpStepActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("position",position);
                bundle.putString("content",(String)listItems.get(position).get("content"));
                bundle.putString("picPath",picPaths.get(position));
                intent.putExtras(bundle);
                startActivityForResult(intent, EDIT_STEP);
            }
        });

        //添加步骤的按钮
        imv_add=(ImageView)findViewById(R.id.imv_add);
        imv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                step_num++;
                listItem = new HashMap<String, Object>();
                listItem.put("num", step_num + ".");
                listItem.put("content","请添加");
                listItems.add(listItem);
                picPaths.add("");
                picUrls.add("");
                adapter.notifyDataSetChanged();
                ListViewUtils.setListViewHeightBasedOnChildren(step_list);
            }
        });
        imv_delete=(ImageView)findViewById(R.id.imv_delete);
        imv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size=listItems.size();
                if(size>0){
                    step_num--;
                    listItems.remove(size-1);
                    picUrls.remove(size-1);
                    picPaths.remove(size-1);
                    adapter.notifyDataSetChanged();
                    ListViewUtils.setListViewHeightBasedOnChildren(step_list);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * 从选择图片得到结果
         */
        if(resultCode==Activity.RESULT_OK && requestCode == TO_SELECT_PHOTO)
        {
            picPath = data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH);
            Log.i("dfood", "最终选择的图片=" + picPath);
            Bitmap bm = BitmapFactory.decodeFile(picPath);
            iv_recipe.setImageBitmap(bm);
            QCloud.UploadPic(picPath, UploadActivity.this);
            //更新图库
            Uri localUri = Uri.fromFile(new File(picPath));
            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            sendBroadcast(localIntent);
        }
        /**
         * 从上传步骤得到结果
         */
        if(resultCode==Activity.RESULT_OK&&requestCode==EDIT_STEP){
            Bundle returndata=data.getExtras();
            int position=returndata.getInt("position");
            picPaths.set(position,returndata.getString("picPath"));
            bitmap=BitmapFactory.decodeFile(returndata.getString("picPath"));
            picUrls.set(position,returndata.getString("url"));
            Log.i("dfood","步骤"+position+returndata.getString("url"));
            listItems.get(position).put("content",returndata.getString("content"));
            listItems.get(position).put("pic", bitmap);
            adapter.notifyDataSetChanged();
            ListViewUtils.setListViewHeightBasedOnChildren(step_list);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //接受广播
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            recipeurl=QCloud.resultUrl;
        }
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
