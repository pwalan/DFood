package github.com.pwalan.dfood;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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

/**
 * 菜单详情页
 */
public class ShowRecipeActivity extends Activity {

    //获取数据后
    protected static final int GET_DATA = 1;
    //菜谱图片
    protected static final int RPIC = 2;
    //用户头像
    protected static final int HEAD = 3;
    //菜谱步骤图片
    protected static final int STEP = 4;

    private App app;
    private String rname;
    private int rid, uid;
    private int count = 0;
    private JSONObject response, data;
    private JSONArray steps;
    List<Map<String, Object>> listItems;
    private Bitmap bitmap;

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

    //评论列表
    private ListView comment_list;

    SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recipe);

        rname = getIntent().getStringExtra("rname");

        initView();

        getData();
    }

    /**
     * 初始化页面
     */
    private void initView() {
        app = (App) getApplication();

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
                                                         Intent intent;
                                                         switch (item.getItemId()) {
                                                             case R.id.comment:
                                                                 popup.dismiss();
                                                                 Toast.makeText(ShowRecipeActivity.this, "评论", Toast.LENGTH_SHORT).show();
                                                                 break;
                                                             case R.id.favorite:
                                                                 popup.dismiss();
                                                                 Toast.makeText(ShowRecipeActivity.this, "收藏", Toast.LENGTH_SHORT).show();
                                                                 break;
                                                             case R.id.share:
                                                                 popup.dismiss();
                                                                 Toast.makeText(ShowRecipeActivity.this, "分享", Toast.LENGTH_SHORT).show();
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
        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_rcontent = (TextView) findViewById(R.id.tv_rcontent);

        //初始化步骤
        step_list = (ListView) findViewById(R.id.step_list);

        //初始化评论
        comment_list = (ListView) findViewById(R.id.comment_list);

        listItems = new ArrayList<Map<String, Object>>();
    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("rname", rname);
                response = C.asyncPost(app.getServer() + "getSteps", map);
                handler.sendEmptyMessage(GET_DATA);
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
                            getHttpBitmap(data.get("rpic").toString(), RPIC);
                            uid = Integer.parseInt(data.get("uid").toString());
                            tv_username.setText(data.get("username").toString());
                            tv_rcontent.setText("        " + data.get("info").toString());
                            //处理菜谱步骤
                            steps = new JSONArray(data.get("steps").toString());
                            JSONObject jo = steps.getJSONObject(count);
                            getHttpBitmap(jo.get("pic").toString(), STEP);

                        } else {
                            Toast.makeText(ShowRecipeActivity.this, "未找到,请返回！", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case RPIC:
                    iv_recipe.setImageBitmap(bitmap);
                    try {
                        getHttpBitmap(data.get("head").toString(), HEAD);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case HEAD:
                    iv_head.setImageBitmap(bitmap);
                    break;

                case STEP:
                    Map<String, Object> listItem = new HashMap<String, Object>();
                    Log.i("step","现在是第"+count);
                    try {
                        JSONObject jo = steps.getJSONObject(count);
                        listItem.put("num", jo.get("num").toString());
                        listItem.put("content", jo.get("content").toString());
                        listItem.put("pic", bitmap);
                        listItems.add(listItem);
                        count++;
                        if (count == steps.length()) {
                            SimpleAdapter adapter = new SimpleAdapter(ShowRecipeActivity.this, listItems, R.layout.step_item,
                                    new String[]{"num", "content", "pic"},
                                    new int[]{R.id.tv_num, R.id.tv_step, R.id.iv_step});
                            step_list.setAdapter(adapter);
                        }else{
                            jo = steps.getJSONObject(count);
                            getHttpBitmap(jo.get("pic").toString(), STEP);
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

    public void getHttpBitmap(final String url, final int msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL myFileURL = new URL(url);
                    //获得连接
                    HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
                    //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
                    conn.setConnectTimeout(6000);
                    //连接设置获得数据流
                    conn.setDoInput(true);
                    //不使用缓存
                    conn.setUseCaches(false);
                    //这句可有可无，没有影响
                    //conn.connect();
                    //得到数据流
                    InputStream is = conn.getInputStream();
                    //解析得到图片
                    bitmap = BitmapFactory.decodeStream(is);
                    //关闭数据流
                    is.close();
                    handler.sendEmptyMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
