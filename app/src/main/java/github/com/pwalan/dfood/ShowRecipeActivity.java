package github.com.pwalan.dfood;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Handler;

import java.util.Map;
import java.util.Objects;
import java.util.logging.LogRecord;

import github.com.pwalan.dfood.myview.RoundImageView;
import github.com.pwalan.dfood.utils.C;
import github.com.pwalan.dfood.utils.ListViewBinder;
import github.com.pwalan.dfood.utils.ListViewUtils;
import github.com.pwalan.dfood.utils.QCloud;

/**
 * 菜单详情页
 */
public class ShowRecipeActivity extends Activity {
    //获取数据
    protected static final int GET_DATA = 1;
    //菜谱图片
    protected static final int RPIC = 2;
    //用户头像
    protected static final int HEAD = 3;
    //菜谱步骤图片
    protected static final int STEP = 4;
    //收藏
    protected static final int ADDFAVORITE=5;
    //获取评论用户的头像
    protected static final int HEADS=6;
    //获取新评论
    protected static final int GET_COMMENT=7;
    //点赞
    protected static final int ADDZAN=8;

    //本地广播
    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;

    private App app;
    private String rname, rpic;
    private int rid,ruid, uid;
    private int count = 0;
    private int status;
    private JSONObject response, data;
    private JSONArray steps,comments;

    //startActivityForResult需要的intent
    private Intent lastIntent ;

    //标题
    private ImageView titleLeftImv;
    private TextView titleTv;
    private ImageView img_up;
    PopupMenu popup = null;

    //主体
    private ImageView iv_recipe;
    private TextView tv_rname;
    private RoundImageView iv_head;
    private TextView tv_username;
    private TextView tv_rcontent;

    //步骤列表
    private ListView step_list;
    private SimpleAdapter step_adapter;
    List<Map<String, Object>> step_listItems;


    //评论列表
    private ListView comment_list;
    private SimpleAdapter comment_adapter;
    List<Map<String, Object>> com_listItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recipe);

        rname = getIntent().getStringExtra("rname");
        ruid=getIntent().getIntExtra("uid",1);

        //腾讯云下载初始化
        QCloud.init(this);

        //本地广播接收初始化
        localBroadcastManager=LocalBroadcastManager.getInstance(this);
        intentFilter=new IntentFilter();
        intentFilter.addAction("github.com.pwalan.dfood.LOCAL_BROADCAST");
        localReceiver=new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);

        initView();

        getData();
    }

    /**
     * 初始化页面
     */
    private void initView() {
        app = (App) getApplication();
        lastIntent = getIntent();

        //初始化标题栏
        titleLeftImv = (ImageView) findViewById(R.id.title_imv);
        titleLeftImv.setImageResource(R.drawable.exit);
        titleLeftImv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleTv = (TextView) findViewById(R.id.title_text_tv);
        titleTv.setText("菜谱详情");

        img_up = (ImageView) findViewById(R.id.img_up);
        img_up.setImageResource(R.drawable.menu);
        img_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup = new PopupMenu(ShowRecipeActivity.this, v);
                getMenuInflater().inflate(R.menu.menu_show_recipe, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                                     @Override
                                                     public boolean onMenuItemClick(MenuItem item) {
                                                         switch (item.getItemId()) {
                                                             case R.id.comment:
                                                                 popup.dismiss();
                                                                 if (app.isLogin()) {
                                                                     Intent intent = new Intent(ShowRecipeActivity.this, MakeCommentActivity.class);
                                                                     intent.putExtra("rid", rid);
                                                                     startActivityForResult(intent, 0);
                                                                 } else {
                                                                     Toast.makeText(ShowRecipeActivity.this, "要评论请先登录", Toast.LENGTH_SHORT).show();
                                                                 }
                                                                 break;
                                                             case R.id.favorite:
                                                                 popup.dismiss();
                                                                 if (app.isLogin()) {
                                                                     addFavorite();
                                                                 } else {
                                                                     Toast.makeText(ShowRecipeActivity.this, "要收藏请先登录", Toast.LENGTH_SHORT).show();
                                                                 }
                                                                 break;
                                                             case R.id.zan:
                                                                 if (app.isLogin()) {
                                                                     addZan();
                                                                 } else {
                                                                     Toast.makeText(ShowRecipeActivity.this, "要点赞请先登录", Toast.LENGTH_SHORT).show();
                                                                 }
                                                                 break;
                                                             case R.id.share:
                                                                 popup.dismiss();
                                                                 Intent intent=new Intent(ShowRecipeActivity.this,ShareDialogActivity.class);
                                                                 intent.putExtra("rid",rid);
                                                                 intent.putExtra("rname",rname);
                                                                 intent.putExtra("content",tv_rcontent.getText().toString().trim());
                                                                 intent.putExtra("rpic",rpic);
                                                                 startActivity(intent);
                                                                 break;
                                                             default:
                                                                 break;
                                                         }
                                                         return true;
                                                     }
                                                 }
                );
                popup.show();
            }
        });

        //初始化主体
        iv_recipe = (ImageView) findViewById(R.id.iv_recipe);
        tv_rname = (TextView) findViewById(R.id.tv_rname);
        tv_rname.setText(rname);
        iv_head = (RoundImageView) findViewById(R.id.iv_head);
        iv_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowRecipeActivity.this, ShowPersonInf.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });
        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_rcontent = (TextView) findViewById(R.id.tv_rcontent);

        //初始化步骤
        step_list = (ListView) findViewById(R.id.step_list);
        step_listItems = new ArrayList<Map<String, Object>>();
        step_adapter = new SimpleAdapter(ShowRecipeActivity.this, step_listItems, R.layout.step_item,
                new String[]{"num", "content", "pic"},
                new int[]{R.id.tv_num, R.id.tv_step, R.id.iv_step});
        step_adapter.setViewBinder(new ListViewBinder());
        step_list.setAdapter(step_adapter);
        ListViewUtils.setListViewHeightBasedOnChildren(step_list);

        //初始化评论
        comment_list = (ListView) findViewById(R.id.comment_list);
        com_listItems=new ArrayList<Map<String, Object>>();
        comment_adapter = new SimpleAdapter(ShowRecipeActivity.this, com_listItems, R.layout.comment_item,
                new String[]{"cname", "time", "content","head"},
                new int[]{R.id.tv_cname, R.id.tv_time, R.id.tv_content, R.id.iv_chead});
        comment_adapter.setViewBinder(new ListViewBinder());
        comment_list.setAdapter(comment_adapter);
        ListViewUtils.setListViewHeightBasedOnChildren(comment_list);


    }

    /**
     * 获取菜谱的数据
     */
    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("rname", rname);
                map.put("uid",ruid);
                response = C.asyncPost(app.getServer() + "getSteps", map);
                handler.sendEmptyMessage(GET_DATA);
            }
        }).start();
    }

    /**
     * 获取新的评论数据
     */
    private void getComments() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("rname", rname);
                response = C.asyncPost(app.getServer() + "getSteps", map);
                handler.sendEmptyMessage(GET_COMMENT);
            }
        }).start();
    }

    /**
     * 添加收藏
     */
    private void addFavorite(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map=new HashMap();
                map.put("uid",app.getUid());
                map.put("rid",rid);
                response=C.asyncPost(app.getServer()+"addFavorite",map);
                handler.sendEmptyMessage(ADDFAVORITE);
            }
        }).start();
    }

    /**
     * 点赞
     */
    private void addZan(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map=new HashMap();
                map.put("uid",app.getUid());
                map.put("rid",rid);
                response=C.asyncPost(app.getServer()+"addZan",map);
                handler.sendEmptyMessage(ADDZAN);
            }
        }).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_DATA:
                    try {
                        data = new JSONObject(response.get("data").toString());
                        Log.i("steps", data.toString());
                        if (!data.toString().equals("{}")) {
                            //处理相关数据
                            rid = Integer.parseInt(data.get("rid").toString());
                            Log.i("step", "rpic:" + data.get("rpic").toString());
                            //下载菜谱介绍图片
                            rpic=data.get("rpic").toString();
                            QCloud.downloadPic(rpic);
                            status=RPIC;
                            //getHttpBitmap(data.get("rpic").toString(), RPIC);
                            uid = data.getInt("uid");
                            tv_username.setText(data.get("username").toString());
                            tv_rcontent.setText("        " + data.get("info").toString());
                            comments=new JSONArray(data.getString("comments"));
                            //处理菜谱步骤
                            steps = new JSONArray(data.get("steps").toString());

                            //getHttpBitmap(jo.get("pic").toString(), STEP);

                        } else {
                            Toast.makeText(ShowRecipeActivity.this, "未找到,请返回！", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case RPIC:
                    try {
                        iv_recipe.setImageBitmap(QCloud.bmp);
                        //下完菜谱介绍图片后开始下载上传用户的头像
                        QCloud.downloadPic(data.get("head").toString());
                        status=HEAD;
                        //getHttpBitmap(data.get("head").toString(), HEAD);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case HEAD:
                    iv_head.setImageBitmap(QCloud.bmp);
                    JSONObject jo = null;
                    try {
                        jo = steps.getJSONObject(count);
                        QCloud.downloadPic(jo.get("pic").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    status=STEP;
                    break;
                case ADDFAVORITE:
                    try {
                        String result = response.getString("data");
                        if(result.equals("add")){
                            Toast.makeText(ShowRecipeActivity.this,"已收藏",Toast.LENGTH_SHORT).show();
                        }else if(result.equals("cancle")){
                            Toast.makeText(ShowRecipeActivity.this,"已取消收藏",Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case ADDZAN:
                    try {
                        String result = response.getString("data");
                        if(result.equals("add")){
                            Toast.makeText(ShowRecipeActivity.this,"点赞成功！",Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case HEADS:
                    //设置结果
                    setResult(Activity.RESULT_OK, lastIntent);
                    status=0;
                    break;
                case GET_COMMENT:
                    try {
                        com_listItems=new ArrayList<Map<String, Object>>();
                        comment_adapter = new SimpleAdapter(ShowRecipeActivity.this, com_listItems, R.layout.comment_item,
                                new String[]{"cname", "time", "content","head"},
                                new int[]{R.id.tv_cname, R.id.tv_time, R.id.tv_content, R.id.iv_chead});
                        comment_adapter.setViewBinder(new ListViewBinder());
                        comment_list.setAdapter(comment_adapter);
                        ListViewUtils.setListViewHeightBasedOnChildren(comment_list);

                        data = new JSONObject(response.get("data").toString());
                        comments=new JSONArray(data.getString("comments"));
                        JSONObject jo_co = steps.getJSONObject(count);
                        jo_co=comments.getJSONObject(count);
                        if(jo_co!=null){
                            QCloud.downloadPic(jo_co.getString("head"));
                            status=HEADS;
                            //getHttpBitmap(jo.getString("head"),HEADS);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK && requestCode == 0)
        {
            count=0;
            com_listItems=new ArrayList<Map<String, Object>>();
            getComments();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //接受广播
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            switch (status){
                case RPIC:
                    handler.sendEmptyMessage(RPIC);
                    break;
                case HEAD:
                    handler.sendEmptyMessage(HEAD);
                    break;
                case STEP:
                    Log.i("step","现在是第"+count);
                    try {
                        JSONObject jo = steps.getJSONObject(count);
                        Map<String, Object> step_listItem = new HashMap<String, Object>();
                        step_listItem.put("num", jo.get("num").toString());
                        step_listItem.put("content", jo.get("content").toString());
                        step_listItem.put("pic", QCloud.bmp);
                        step_listItems.add(step_listItem);
                        step_adapter.notifyDataSetChanged();
                        ListViewUtils.setListViewHeightBasedOnChildren(step_list);
                        count++;
                        if (count == steps.length()) {
                            //获取完步骤后再获取评论
                            count=0;
                            jo=comments.getJSONObject(count);
                            if(jo!=null){
                                QCloud.downloadPic(jo.getString("head"));
                                status=HEADS;
                                //getHttpBitmap(jo.getString("head"),HEADS);
                            }
                        }else{
                            jo = steps.getJSONObject(count);
                            QCloud.downloadPic(jo.get("pic").toString());
                            //getHttpBitmap(jo.get("pic").toString(), STEP);
                            Log.i("step",jo.get("pic").toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case HEADS:
                    try {
                        JSONObject jo = comments.getJSONObject(count);
                        Map<String, Object> com_listItem = new HashMap<String, Object>();
                        com_listItem.put("cname", jo.get("cname").toString());
                        com_listItem.put("time",jo.getString("time"));
                        com_listItem.put("content", jo.get("content").toString());
                        com_listItem.put("head", QCloud.bmp);
                        com_listItems.add(com_listItem);
                        comment_adapter.notifyDataSetChanged();
                        ListViewUtils.setListViewHeightBasedOnChildren(comment_list);
                        count++;
                        if (count == comments.length()) {
                            handler.sendEmptyMessage(HEADS);
                        }else{
                            jo = comments.getJSONObject(count);
                            QCloud.downloadPic(jo.get("head").toString());
                            //getHttpBitmap(jo.get("head").toString(), HEADS);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}
