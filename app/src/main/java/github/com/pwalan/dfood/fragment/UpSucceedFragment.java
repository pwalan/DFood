package github.com.pwalan.dfood.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
import github.com.pwalan.dfood.utils.ListViewBinder;
import github.com.pwalan.dfood.utils.QCloud;

public class UpSucceedFragment extends Fragment {
    //获取图片
    protected static final int GET_PICS=1;

    private App app;
    private int count=0;
    private Bitmap bitmap;
    private JSONArray data;
    private ListView list;
    private SimpleAdapter adapter;
    private List<Map<String, Object>> listItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_up_succeed, container, false);

        app=(App)getActivity().getApplication();
        list=(ListView)view.findViewById(R.id.list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ShowRecipeActivity.class);
                intent.putExtra("rname", listItems.get(position).get("rname").toString());
                intent.putExtra("uid",app.getUid());
                startActivity(intent);
            }
        });
        listItems = new ArrayList<Map<String, Object>>();
        adapter = new SimpleAdapter(view.getContext(), listItems, R.layout.simple_recipe_item,
                new String[]{"rname", "time", "pic"},
                new int[]{R.id.tv_rname, R.id.tv_time, R.id.iv_pic});
        adapter.setViewBinder(new ListViewBinder());
        list.setAdapter(adapter);
        data=new JSONArray();

        Bundle bundle=getArguments();
        if(bundle!=null){
            try {
                data=new JSONArray(bundle.getString("data"));
                if(data.length()>0){
                    getHttpBitmap(data.getJSONObject(0).getString("pic"),GET_PICS);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return view;
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_PICS:
                    Map<String, Object> listItem = new HashMap<String, Object>();
                    JSONObject jo = null;
                    try {
                        jo = data.getJSONObject(count);
                        listItem.put("rname",jo.getString("rname"));
                        listItem.put("time",jo.getString("time"));
                        listItem.put("pic",bitmap);
                        listItems.add(listItem);
                        adapter.notifyDataSetChanged();
                        count++;
                        if(count<data.length()){
                            getHttpBitmap(data.getJSONObject(count).getString("pic"),GET_PICS);
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
