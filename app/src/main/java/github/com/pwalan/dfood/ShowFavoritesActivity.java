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


public class ShowFavoritesActivity extends Activity {
    //获取数据
    protected static final int GET_DATA = 1;
    //获取图片
    protected static final int GET_PICS=2;

    private App app;
    private ListView flist;
    List<Map<String, Object>> listItems;
    private JSONObject response;
    private JSONArray data;
    private int count=0;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_favorites);
        app=(App)getApplication();
        flist=(ListView)findViewById(R.id.flist);
        listItems=new ArrayList<Map<String, Object>>();
        getData();
    }

    /**
     * 获取收藏数据
     */
    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("uid",app.getUid());
                response = C.asyncPost(app.getServer() + "getFavorites", map);
                handler.sendEmptyMessage(GET_DATA);
            }
        }).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case GET_DATA:
                    try {
                        data = new JSONArray(response.get("data").toString());
                        JSONObject jo = data.getJSONObject(count);
                        getHttpBitmap(jo.getString("pic"),GET_PICS);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case GET_PICS:
                    Map<String, Object> listItem = new HashMap<String, Object>();
                    try {
                        JSONObject jo = data.getJSONObject(count);
                        listItem.put("rname",jo.getString("rname"));
                        listItem.put("time",jo.getString("time"));
                        listItem.put("pic", bitmap);
                        listItem.put("uid",jo.getInt("uid"));
                        listItems.add(listItem);
                        count++;
                        if(count==data.length()){
                            SimpleAdapter adapter = new SimpleAdapter(ShowFavoritesActivity.this, listItems, R.layout.simple_recipe_item,
                                    new String[]{"rname", "time", "pic"},
                                    new int[]{R.id.tv_rname, R.id.tv_time, R.id.iv_pic});
                            adapter.setViewBinder(new ListViewBinder());
                            flist.setAdapter(adapter);
                            flist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(ShowFavoritesActivity.this, ShowRecipeActivity.class);
                                    intent.putExtra("rname", listItems.get(position).get("rname").toString());
                                    intent.putExtra("uid",Integer.parseInt(listItems.get(position).get("uid").toString()));
                                    startActivityForResult(intent, 0);
                                }
                            });
                        }else{
                            jo = data.getJSONObject(count);
                            getHttpBitmap(jo.getString("pic"),GET_PICS);
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
            Log.i("dfood","查看收藏后");
            count=0;
            listItems=new ArrayList<Map<String, Object>>();
            getData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

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
