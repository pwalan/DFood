package github.com.pwalan.dfood;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import github.com.pwalan.dfood.myview.RoundImageView;

/**
 * 菜单详情页
 */
public class ShowRecipeActivity extends Activity {

    private App app;
    private String rname,username,rcontent;
    private int rid,uid;

    //标题
    private ImageView titleLeftImv;
    private TextView titleTv;
    private ImageView img_up;
    PopupMenu popup = null;

    //主体
    private ImageView iv_recipe;
    private TextView tv_rname;
    private RoundImageView iv_head;
    private TextView tv_username;
    private TextView tv_rcontent;

    //步骤列表
    private ListView step_list;

    //评论列表
    private ListView comment_list;

    SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recipe);

        rname=getIntent().getStringExtra("rname");

        initView();
    }

    /**
     * 初始化页面
     */
    private void initView() {
        app=(App)getApplication();

        //初始化标题栏
        titleLeftImv=(ImageView)findViewById(R.id.title_imv);
        titleLeftImv.setImageResource(R.drawable.exit);
        titleLeftImv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleTv=(TextView)findViewById(R.id.title_text_tv);
        titleTv.setText("菜谱详情");

        img_up=(ImageView)findViewById(R.id.img_up);
        img_up.setImageResource(R.drawable.menu);
        img_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup = new PopupMenu(ShowRecipeActivity.this, v);
                getMenuInflater().inflate(R.menu.menu_show_recipe, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                                     @Override
                                                     public boolean onMenuItemClick(MenuItem item) {
                                                         Intent intent;
                                                         switch (item.getItemId()) {
                                                             case R.id.comment:
                                                                 popup.dismiss();
                                                                 Toast.makeText(ShowRecipeActivity.this,"评论",Toast.LENGTH_SHORT).show();
                                                                 break;
                                                             case R.id.favorite:
                                                                 popup.dismiss();
                                                                 Toast.makeText(ShowRecipeActivity.this,"收藏",Toast.LENGTH_SHORT).show();
                                                                 break;
                                                             case R.id.share:
                                                                 popup.dismiss();
                                                                 Toast.makeText(ShowRecipeActivity.this,"分享",Toast.LENGTH_SHORT).show();
                                                                 break;
                                                             default:
                                                                 break;
                                                         }
                                                         return true;
                                                     }
                                                 }
                );
                popup.show();
            }
        });

        //初始化主体
        iv_recipe=(ImageView)findViewById(R.id.iv_recipe);
        tv_rname=(TextView)findViewById(R.id.tv_rname);
        tv_rname.setText(rname);
        iv_head=(RoundImageView)findViewById(R.id.iv_head);
        tv_username=(TextView)findViewById(R.id.tv_username);
        tv_rcontent=(TextView)findViewById(R.id.tv_rcontent);

        //初始化步骤
        step_list=(ListView)findViewById(R.id.step_list);

        //初始化评论
        comment_list=(ListView)findViewById(R.id.comment_list);
    }

}
