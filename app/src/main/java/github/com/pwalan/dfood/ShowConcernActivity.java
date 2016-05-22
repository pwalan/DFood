package github.com.pwalan.dfood;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ShowConcernActivity extends ActionBarActivity {
    //获取数据
    protected static final int GET_DATA = 1;
    //获取图片
    protected static final int GET_PICS=2;

    private App app;
    private ListView clist;
    List<Map<String, Object>> listItems;
    private JSONObject response;
    private JSONArray data;
    private int count=0;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_concern);
        app=(App)getApplication();
        clist=(ListView)findViewById(R.id.clist);
        listItems=new ArrayList<Map<String, Object>>();
        getData();
    }

    /**
     * 获取关注数据
     */
    private void getData(){

    }
}
