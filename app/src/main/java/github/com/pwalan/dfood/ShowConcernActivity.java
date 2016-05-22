package github.com.pwalan.dfood;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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


public class ShowConcernActivity extends Activity {
    //获取数据
    protected static final int GET_DATA = 1;
    //获取图片
    protected static final int GET_HEADS=2;

    private App app;
    private ListView clist;
    List<Map<String, Object>> listItems;
    private JSONObject response;
    private JSONArray data;
    private int count=0;
    private ArrayList<Integer> cids;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_concern);
        app=(App)getApplication();
        clist=(ListView)findViewById(R.id.clist);
        listItems=new ArrayList<Map<String, Object>>();
        cids=new ArrayList<Integer>();
        getData();
    }

    /**
     * 获取关注数据
     */
    private void getData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("uid",app.getUid());
                response = C.asyncPost(app.getServer() + "getConcern", map);
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
                        data = new JSONArray(response.get("data").toString());
                        JSONObject jo = data.getJSONObject(count);
                        getHttpBitmap(jo.getString("head"),GET_HEADS);
                        Log.i("dfood","head:"+jo.getString("head"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case GET_HEADS:
                    Map<String, Object> listItem = new HashMap<String, Object>();
                    try {
                        JSONObject jo = data.getJSONObject(count);
                        listItem.put("cname",jo.getString("cname"));
                        listItem.put("time",jo.getString("time"));
                        listItem.put("head", bitmap);
                        listItems.add(listItem);
                        cids.add(jo.getInt("cid"));
                        Log.i("dfood","现在是"+count);
                        count++;
                        if(count==data.length()){
                            SimpleAdapter adapter = new SimpleAdapter(ShowConcernActivity.this, listItems, R.layout.concern_item,
                                    new String[]{"cname", "time", "head"},
                                    new int[]{R.id.tv_username, R.id.tv_time, R.id.iv_head});
                            adapter.setViewBinder(new ListViewBinder());
                            clist.setAdapter(adapter);
                            clist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(ShowConcernActivity.this, ShowPersonInf.class);
                                    intent.putExtra("uid", cids.get(position));
                                    startActivity(intent);
                                }
                            });
                        }else{
                            jo = data.getJSONObject(count);
                            getHttpBitmap(jo.getString("head"),GET_HEADS);
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
