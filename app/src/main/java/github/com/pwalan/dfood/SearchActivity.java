package github.com.pwalan.dfood;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import github.com.pwalan.dfood.utils.C;
import github.com.pwalan.dfood.utils.ListViewBinder;
import github.com.pwalan.dfood.utils.QCloud;


public class SearchActivity extends Activity {
    //获取数据
    protected static final int GET_DATA = 1;

    //本地广播
    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;

    private App app;
    private String rname;
    private int count=0;
    private JSONObject response;
    private JSONArray data;

    private EditText et_search;
    private ImageView iv_search;
    private ListView list;
    List<Map<String, Object>> listItems;
    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        app=(App)getApplication();

        //腾讯云上传下载初始化
        QCloud.init(this);

        //本地广播接收初始化
        localBroadcastManager=LocalBroadcastManager.getInstance(this);
        intentFilter=new IntentFilter();
        intentFilter.addAction("github.com.pwalan.dfood.LOCAL_BROADCAST");
        localReceiver=new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);

        list=(ListView)findViewById(R.id.list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchActivity.this, ShowRecipeActivity.class);
                intent.putExtra("rname", listItems.get(position).get("rname").toString());
                intent.putExtra("uid",Integer.parseInt(listItems.get(position).get("uid").toString()));
                startActivity(intent);
            }
        });

        et_search=(EditText)findViewById(R.id.et_search);
        iv_search=(ImageView)findViewById(R.id.iv_search);
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rname=et_search.getText().toString().trim();
                if(rname.isEmpty()){
                    Toast.makeText(SearchActivity.this,"请输入要搜索的菜名！",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SearchActivity.this,"搜索中...",Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HashMap map = new HashMap();
                            map.put("rname",rname);
                            response = C.asyncPost(app.getServer() + "searchRecipe", map);
                            handler.sendEmptyMessage(GET_DATA);
                        }
                    }).start();
                }
            }
        });

    }

    //接受广播
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            try {
                JSONObject jo = data.getJSONObject(count);
                Map<String, Object> listItem = new HashMap<String, Object>();
                listItem.put("rname",jo.getString("rname"));
                listItem.put("time",jo.getString("time"));
                listItem.put("pic", QCloud.bmp);
                listItem.put("uid",jo.getInt("uid"));
                listItems.add(listItem);
                adapter.notifyDataSetChanged();
                count++;
                if(count<data.length()){
                    QCloud.downloadPic(data.getJSONObject(count).getString("pic"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_DATA:
                    try {
                        data=new JSONArray(response.getString("data"));
                        if(data.length()>0){
                            count=0;
                            listItems = new ArrayList<Map<String, Object>>();
                            adapter = new SimpleAdapter(SearchActivity.this, listItems, R.layout.simple_item,
                                    new String[]{"rname", "pic"},
                                    new int[]{R.id.picture_name,R.id.shared_pictures});
                            adapter.setViewBinder(new ListViewBinder());
                            list.setAdapter(adapter);

                            JSONObject jo = data.getJSONObject(count);
                            QCloud.downloadPic(jo.getString("pic"));
                        }else{
                            Toast.makeText(SearchActivity.this,"未找到！",Toast.LENGTH_SHORT).show();
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

}
