package github.com.pwalan.dfood.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import github.com.pwalan.dfood.App;
import github.com.pwalan.dfood.R;
import github.com.pwalan.dfood.ShowRecipeActivity;
import github.com.pwalan.dfood.myview.RefreshableView;
import github.com.pwalan.dfood.myview.RoundImageView;
import github.com.pwalan.dfood.utils.C;
import github.com.pwalan.dfood.utils.ListViewBinder;
import github.com.pwalan.dfood.utils.ListViewUtils;
import github.com.pwalan.dfood.utils.QCloud;

/**
 * 美食圈
 */
public class FoodCircleFragment extends Fragment {
    //获取数据
    protected static final int FOOODCIRCLE_GET_DATA=1;
    //获取头像
    protected static final int FOOODCIRCLE_HEAD=2;
    //获取菜谱图片
    protected static final int FOOODCIRCLE_PIC=3;

    //本地广播
    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;

    private App app;
    private JSONArray data;
    private JSONObject response;
    private int count=0;
    private int status;

    //下拉刷新
    RefreshableView refreshableView;

    private ListView list;
    private SimpleAdapter adapter;
    private List<Map<String, Object>> listItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_foodcircle, container, false);

        app=(App)getActivity().getApplication();
        //腾讯云下载初始化
        QCloud.init(view.getContext());

        //本地广播接收初始化
        localBroadcastManager=LocalBroadcastManager.getInstance(view.getContext());
        intentFilter=new IntentFilter();
        intentFilter.addAction("github.com.pwalan.dfood.LOCAL_BROADCAST");
        localReceiver=new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);

        list=(ListView)view.findViewById(R.id.list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ShowRecipeActivity.class);
                intent.putExtra("rname", listItems.get(position).get("rname").toString());
                intent.putExtra("uid",Integer.parseInt(listItems.get(position).get("uid").toString()));
                startActivity(intent);
            }
        });

        //下拉刷新初始化
        refreshableView = (RefreshableView) view.findViewById(R.id.refreshable_view);
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                refreshableView.finishRefreshing();
            }
        }, 0);

        getData();

        return view;
    }

    /**
     * 获取数据
     */
    public void getData(){
        count=0;
        listItems = new ArrayList<Map<String, Object>>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                response = C.asyncPost(app.getServer() + "getFoodCircle", map);
                handler.sendEmptyMessage(FOOODCIRCLE_GET_DATA);
            }
        }).start();
    }

    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FOOODCIRCLE_GET_DATA:
                    try {
                        adapter = new SimpleAdapter(getActivity(), listItems, R.layout.quanzi_item,
                                new String[]{"head", "name", "time","rname","rpic","num"},
                                new int[]{R.id.head, R.id.name, R.id.time,R.id.rname,R.id.rpic,R.id.num});
                        adapter.setViewBinder(new ListViewBinder());
                        list.setAdapter(adapter);

                        data = new JSONArray(response.getString("data"));
                        JSONObject jo = data.getJSONObject(count);
                        QCloud.downloadPic(jo.getString("head"));
                        status=FOOODCIRCLE_HEAD;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //接受广播
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (status){
                case FOOODCIRCLE_HEAD:
                    try {
                        JSONObject jo = data.getJSONObject(count);
                        Map<String, Object> listItem = new HashMap<String, Object>();
                        listItem.put("head",QCloud.bmp);
                        listItem.put("name",jo.getString("username"));
                        listItem.put("uid",jo.getInt("uid"));
                        listItem.put("time",jo.getString("time"));
                        listItem.put("rname",jo.getString("rname"));
                        listItem.put("num", jo.getInt("cnum") + "评论" + " " + jo.getInt("fnum") + "收藏" +
                                " " + jo.getInt("znum") + "赞");
                        listItems.add(listItem);
                        adapter.notifyDataSetChanged();
                        QCloud.downloadPic(jo.getString("pic"));
                        status=FOOODCIRCLE_PIC;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case FOOODCIRCLE_PIC:
                    try {
                        listItems.get(count).put("rpic", QCloud.bmp);
                        adapter.notifyDataSetChanged();
                        count++;
                        if(count<data.length()&&count<20){
                            JSONObject jo = data.getJSONObject(count);
                            QCloud.downloadPic(jo.getString("head"));
                            status=FOOODCIRCLE_HEAD;
                        }else{
                            status=0;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}