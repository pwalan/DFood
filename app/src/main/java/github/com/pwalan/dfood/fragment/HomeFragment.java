package github.com.pwalan.dfood.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.logging.Handler;

import github.com.pwalan.dfood.App;
import github.com.pwalan.dfood.MainActivity;
import github.com.pwalan.dfood.R;
import github.com.pwalan.dfood.ShowRecipeActivity;
import github.com.pwalan.dfood.utils.C;
import github.com.pwalan.dfood.utils.ListViewBinder;

import static java.lang.Thread.sleep;

/**
 * 首页
 */
public class HomeFragment extends Fragment {
    //获取数据
    protected static final int GET_DATA=1;
    //获取图片
    protected static final int GET_PICS=2;

    private String[] names = new String[]{
            "白切鸡", "夫妻肺片","麻婆豆腐", "七星鱼丸", "石锅拌饭", "糖醋鲤鱼","糖醋排骨","宫保鸡丁"
    };

    private int[] imageIds = new int[]{
            R.drawable.picture0, R.drawable.picture1, R.drawable.picture2,
            R.drawable.picture3, R.drawable.picture4,R.drawable.picture5,
            R.drawable.picture6,R.drawable.picture7
    };

    private App app;
    private JSONArray data;
    private JSONObject response;
    private int count=0;
    private Bitmap bitmap;

    private ListView list;
    private List<Map<String, Object>> listItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

      /*  app=(App)getActivity().getApplication();
        listItems = new ArrayList<Map<String, Object>>();
        list = (ListView) view.findViewById(R.id.home_list);
        getData();*/

        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < names.length; i++) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("picture", imageIds[i]);
            listItem.put("name", names[i]);
            listItems.add(listItem);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), listItems, R.layout.simple_item,
                new String[]{"picture", "name"},
                new int[]{R.id.shared_pictures, R.id.picture_name});

        ListView list = (ListView) view.findViewById(R.id.home_list);
        list.setAdapter(simpleAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(),ShowRecipeActivity.class);
                intent.putExtra("rname",names[position]);
                startActivity(intent);
            }
        });

        return view;
    }

    /*
    public void getData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                response = C.asyncPost(app.getServer() + "getHomeRecipes", map);
                handler.sendEmptyMessage(GET_DATA);
            }
        }).start();
    }

    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_DATA:
                    try {
                        data = new JSONArray(response.get("data").toString());
                        JSONObject jo = data.getJSONObject(count);
                        getHttpBitmap(jo.getString("pic"),GET_PICS);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                case GET_PICS:
                    Map<String, Object> listItem = new HashMap<String, Object>();
                    try {
                        Log.i("dfood","现在是第"+count+"个菜谱");
                        JSONObject jo = data.getJSONObject(count);
                        listItem.put("rname",jo.getString("rname"));
                        listItem.put("pic",bitmap);
                        listItems.add(listItem);
                        count++;
                        if(count==data.length()){
                            SimpleAdapter adapter = new SimpleAdapter(getActivity(), listItems, R.layout.simple_item,
                                    new String[]{"rname", "pic"},
                                    new int[]{R.id.picture_name,R.id.shared_pictures});
                            adapter.setViewBinder(new ListViewBinder());
                            list.setAdapter(adapter);
                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Toast.makeText(getActivity(), "你点击了 " + listItems.get(position).get("rname").toString(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(), ShowRecipeActivity.class);
                                    intent.putExtra("rname", listItems.get(position).get("rname").toString());
                                    startActivity(intent);
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

    public void getHttpBitmap(final String url, final int msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("dfood","即将下载第"+count+"张图");
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
                    Log.i("dfood","第"+count+"张图");
                    handler.sendEmptyMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
     */
}
