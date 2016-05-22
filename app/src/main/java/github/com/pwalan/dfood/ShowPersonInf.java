package github.com.pwalan.dfood;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import github.com.pwalan.dfood.utils.C;
import github.com.pwalan.dfood.utils.ListViewBinder;


public class ShowPersonInf extends Activity {
    //获取数据
    protected static final int GET_DATA = 1;
    //获取头像
    protected static final int HEAD=2;
    //获取图片
    protected static final int GET_PICS=3;
    //添加关注
    protected static final int ADDCONCERN=4;

    private App app;
    private int uid;
    private JSONObject response,data;
    private JSONArray ups;
    private int count=0;
    private Bitmap bitmap;
    List<Map<String, Object>> listItems;

    // 初始化顶部栏显示
    private ImageView titleLeftImv;
    private TextView titleTv;
    private ImageView img_up;

    private TextView tv_conNum,tv_recNum;
    private ListView uplist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_person_inf);

        uid=getIntent().getIntExtra("uid",0);

        app=(App)getApplication();
        listItems=new ArrayList<Map<String, Object>>();
        uplist=(ListView)findViewById(R.id.uplist);
        tv_conNum=(TextView)findViewById(R.id.tv_conNum);
        tv_recNum=(TextView)findViewById(R.id.tv_recNum);
        //初始化标题栏
        titleLeftImv = (ImageView) findViewById(R.id.title_imv);
        titleLeftImv.setImageResource(R.drawable.user);
        titleLeftImv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleTv = (TextView) findViewById(R.id.title_text_tv);

        img_up = (ImageView) findViewById(R.id.img_up);
        img_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addConcern();
            }
        });

        getData();
    }

    /**
     * 获取用户发布及关注数据
     */
    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("uid",uid);
                response = C.asyncPost(app.getServer() + "getUserUp", map);
                handler.sendEmptyMessage(GET_DATA);
            }
        }).start();
    }

    /**
     * 添加关注
     */
    private void addConcern(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map=new HashMap();
                map.put("uid",app.getUid());
                map.put("cid",uid);
                response=C.asyncPost(app.getServer()+"addConcern",map);
                handler.sendEmptyMessage(ADDCONCERN);
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
                        titleTv.setText(data.getString("username"));
                        tv_conNum.setText(data.getString("conNum"));
                        tv_recNum.setText(data.getString("recNum"));
                        getHttpBitmap(data.getString("head"),HEAD);
                        //处理用户发布
                        ups=new JSONArray(data.getString("ups"));
                        JSONObject jo = ups.getJSONObject(count);
                        getHttpBitmap(jo.getString("pic"),GET_PICS);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case HEAD:
                    titleLeftImv.setImageBitmap(bitmap);
                    break;
                case GET_PICS:
                    Map<String, Object> listItem = new HashMap<String, Object>();
                    try {
                        JSONObject jo = ups.getJSONObject(count);
                        listItem.put("rname",jo.getString("rname"));
                        listItem.put("time",jo.getString("time"));
                        listItem.put("pic",bitmap);
                        listItems.add(listItem);
                        count++;
                        if(count==ups.length()){
                            SimpleAdapter adapter = new SimpleAdapter(ShowPersonInf.this, listItems, R.layout.simple_recipe_item,
                                    new String[]{"rname", "time", "pic"},
                                    new int[]{R.id.tv_rname, R.id.tv_time, R.id.iv_pic});
                            adapter.setViewBinder(new ListViewBinder());
                            uplist.setAdapter(adapter);
                            uplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Toast.makeText(ShowPersonInf.this, "你点击了 " + listItems.get(position).get("rname").toString(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ShowPersonInf.this, ShowRecipeActivity.class);
                                    intent.putExtra("rname", listItems.get(position).get("rname").toString());
                                    startActivity(intent);
                                }
                            });
                        }else{
                            jo=ups.getJSONObject(count);
                            getHttpBitmap(jo.getString("pic"),GET_PICS);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case ADDCONCERN:
                    try {
                        String result=response.getString("data");
                        if(result.equals("add")){
                            Toast.makeText(ShowPersonInf.this,"已关注",Toast.LENGTH_SHORT).show();
                        }else if(result.equals("cancle")){
                            Toast.makeText(ShowPersonInf.this,"已取消关注",Toast.LENGTH_SHORT).show();
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
                    conn.connect();
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
