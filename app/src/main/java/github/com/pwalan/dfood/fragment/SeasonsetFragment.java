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
import android.widget.Button;
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

import github.com.pwalan.dfood.App;
import github.com.pwalan.dfood.R;
import github.com.pwalan.dfood.ShowRecipeActivity;
import github.com.pwalan.dfood.utils.C;
import github.com.pwalan.dfood.utils.ListViewBinder;

/**
 * 季节套餐
 */
public class SeasonsetFragment extends Fragment {
    //获取数据
    protected static final int GET_DATA=1;
    //获取图片
    protected static final int GET_PICS=2;

    private App app;
    private JSONArray data;
    private JSONObject response;
    private int count;
    private Bitmap bitmap;
    private String season="春";

    private Button btn_spring, btn_summer, btn_autumn, btn_winter;
    SimpleAdapter simpleAdapter;
    ListView list;
    private List<Map<String, Object>> listItems;
    SimpleAdapter adapter;

    public String[] names = new String[]{
            "白切鸡", "夫妻肺片", "麻婆豆腐", "七星鱼丸", "石锅拌饭", "糖醋鲤鱼"
    };

    public int[] imgs = new int[]{
            R.drawable.picture0, R.drawable.picture1, R.drawable.picture2,
            R.drawable.picture3, R.drawable.picture4, R.drawable.picture5
    };

    //春
    public String[] spr_names = new String[]{
            "七星鱼丸", "石锅拌饭", "糖醋鲤鱼"
    };
    public int[] spr_imgs = new int[]{
            R.drawable.picture3, R.drawable.picture4, R.drawable.picture5
    };
    //夏
    public String[] sum_names = new String[]{
            "白切鸡", "夫妻肺片", "七星鱼丸", "石锅拌饭"
    };
    public int[] sum_imgs = new int[]{
            R.drawable.picture0, R.drawable.picture1,
            R.drawable.picture3, R.drawable.picture4
    };
    //秋
    public String[] aut_names = new String[]{
            "白切鸡", "七星鱼丸", "石锅拌饭"
    };
    public int[] aut_imgs = new int[]{
            R.drawable.picture0, R.drawable.picture3, R.drawable.picture4
    };
    //冬
    public String[] win_names = new String[]{
            "麻婆豆腐", "七星鱼丸", "石锅拌饭", "糖醋鲤鱼"
    };
    public int[] win_imgs = new int[]{
            R.drawable.picture2, R.drawable.picture3, R.drawable.picture4, R.drawable.picture5
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seasonset, container, false);

        app=(App)getActivity().getApplication();

        btn_spring=(Button)view.findViewById(R.id.btn_spring);
        btn_spring.setOnClickListener(new ClickEvent());
        btn_summer=(Button)view.findViewById(R.id.btn_summer);
        btn_summer.setOnClickListener(new ClickEvent());
        btn_autumn=(Button)view.findViewById(R.id.btn_autumn);
        btn_autumn.setOnClickListener(new ClickEvent());
        btn_winter=(Button)view.findViewById(R.id.btn_winter);
        btn_winter.setOnClickListener(new ClickEvent());

        list = (ListView) view.findViewById(R.id.season_list);

        return view;
    }

    class ClickEvent implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(btn_spring==v){
                /*names=spr_names;
                imgs=spr_imgs;*/
                listItems = new ArrayList<Map<String, Object>>();
                season="春";
                count=0;
                adapter = new SimpleAdapter(getActivity(), listItems, R.layout.simple_item,
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
                getData();
            }else if(btn_summer==v){
                /*names=sum_names;
                imgs=sum_imgs;*/
                listItems = new ArrayList<Map<String, Object>>();
                season="夏";
                count=0;
                adapter = new SimpleAdapter(getActivity(), listItems, R.layout.simple_item,
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
                getData();
            }else if(btn_autumn==v){
                /*names=aut_names;
                imgs=aut_imgs;*/
                listItems = new ArrayList<Map<String, Object>>();
                season="秋";
                count=0;
                adapter = new SimpleAdapter(getActivity(), listItems, R.layout.simple_item,
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
                getData();
            }else if(btn_winter==v){
                /*names=win_names;
                imgs=win_imgs;*/
                listItems = new ArrayList<Map<String, Object>>();
                season="冬";
                count=0;
                adapter = new SimpleAdapter(getActivity(), listItems, R.layout.simple_item,
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
                getData();
            }

            /*List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();

            for(int i = 0; i < names.length;i++) {
                Map<String, Object> listItem = new HashMap<String, Object>();
                listItem.put("img", imgs[i]);
                listItem.put("name", names[i]);
                listItems.add(listItem);
            }

            simpleAdapter = new SimpleAdapter(getActivity(), listItems, R.layout.simple_item,
                    new String[]{"img", "name"},
                    new int[]{R.id.shared_pictures, R.id.picture_name});

            list.setAdapter(simpleAdapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getActivity(), "你点击了 " + names[position], Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getActivity(),ShowRecipeActivity.class);
                    intent.putExtra("rname",names[position]);
                    startActivity(intent);
                }
            });*/
        }
    }

    /**
     * 获取数据
     */
    public void getData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("season",season);
                response = C.asyncPost(app.getServer() + "getSeasonRecipes", map);
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
                    break;
                case GET_PICS:
                    Map<String, Object> listItem = new HashMap<String, Object>();
                    try {
                        Log.i("dfood", "现在是第" + count + "个菜谱");
                        JSONObject jo = data.getJSONObject(count);
                        listItem.put("rname",jo.getString("rname"));
                        listItem.put("pic",bitmap);
                        listItems.add(listItem);
                        adapter.notifyDataSetChanged();
                        count++;
                        if(count==data.length()){

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
                    Log.i("dfood", "第" + count + "张图");
                    handler.sendEmptyMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
