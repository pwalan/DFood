package github.com.pwalan.dfood;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
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


public class ShowPersonInf extends Activity {
    //获取数据
    protected static final int GET_DATA = 1;
    //获取图片
    protected static final int GET_PICS=2;
    //添加关注
    protected static final int CONCERN=3;

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
        titleLeftImv.setImageResource(R.drawable.exit);
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
                Toast.makeText(ShowPersonInf.this,"关注",Toast.LENGTH_SHORT).show();
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

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_DATA:
                    try {
                        data = new JSONObject(response.get("data").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case GET_PICS:
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
