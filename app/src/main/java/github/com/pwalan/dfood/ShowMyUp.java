package github.com.pwalan.dfood;

import android.content.Intent;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import github.com.pwalan.dfood.fragment.UnderAuditFragment;
import github.com.pwalan.dfood.fragment.UpFailedFragment;
import github.com.pwalan.dfood.fragment.UpSucceedFragment;
import github.com.pwalan.dfood.myview.RefreshableView;
import github.com.pwalan.dfood.utils.C;

public class ShowMyUp extends FragmentActivity implements View.OnClickListener{
    //获取数据
    protected static final int GET_DATA=1;

    // 顶部栏显示
    private ImageView titleLeftImv;
    private TextView titleTv;
    private ImageView img_up;

    // 定义3个Fragment对象
    private UpSucceedFragment fg1;
    private UnderAuditFragment fg2;
    private UpFailedFragment fg3;
    // 帧布局对象，用来存放Fragment对象
    private FrameLayout frameLayout;
    // 定义每个选项中的相关控件
    private RelativeLayout firstLayout;
    private RelativeLayout secondLayout;
    private RelativeLayout thirdLayout;
    private ImageView firstImage;
    private ImageView secondImage;
    private ImageView thirdImage;
    private TextView firstText;
    private TextView secondText;
    private TextView thirdText;
    // 定义几个颜色
    private int whirt = 0xFFFFFFFF;
    private int gray = 0xFF66CCFF;
    private int dark = 0xff000000;
    // 定义FragmentManager对象管理器
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private App app;
    private int status;
    private JSONObject response,data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_up);

        app=(App)getApplication();

        firstText = (TextView) findViewById(R.id.first_text);
        secondText = (TextView) findViewById(R.id.second_text);
        thirdText = (TextView) findViewById(R.id.third_text);
        firstImage = (ImageView) findViewById(R.id.first_image);
        secondImage = (ImageView) findViewById(R.id.second_image);
        thirdImage = (ImageView) findViewById(R.id.third_image);
        firstLayout = (RelativeLayout) findViewById(R.id.first_layout);
        secondLayout = (RelativeLayout) findViewById(R.id.second_layout);
        thirdLayout = (RelativeLayout) findViewById(R.id.third_layout);
        firstLayout.setOnClickListener(ShowMyUp.this);
        secondLayout.setOnClickListener(ShowMyUp.this);
        thirdLayout.setOnClickListener(ShowMyUp.this);

        // 初始化页面标题栏
        titleLeftImv = (ImageView) findViewById(R.id.title_imv);
        titleLeftImv.setImageResource(R.drawable.exit);
        //顶部左侧的图标点击事件
        titleLeftImv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //顶部右侧的刷新
        img_up = (ImageView) findViewById(R.id.img_up);
        img_up.setImageResource(R.drawable.refresh);
        img_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShowMyUp.this,"更新中，请稍后...",Toast.LENGTH_SHORT).show();
                getData();
            }
        });
        //顶部标签
        titleTv = (TextView) findViewById(R.id.title_text_tv);
        titleTv.setText("我的发布");

        fragmentManager = getSupportFragmentManager();
        setChioceItem(0); // 初始化页面加载时显示第一个选项卡
        status=0;

        getData();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.first_layout:
                status=0;
                setChioceItem(0);
                break;
            case R.id.second_layout:
                status=1;
                setChioceItem(1);
                break;
            case R.id.third_layout:
                status=2;
                setChioceItem(2);
                break;
            default:
                break;
        }
    }

    /**
     * 设置点击选项卡的事件处理
     *
     * @param index 选项卡的标号：0, 1, 2
     */
    private void setChioceItem(int index) {
        fragmentTransaction = fragmentManager.beginTransaction();
        clearChioce(); // 清空, 重置选项, 隐藏所有Fragment
        hideFragments(fragmentTransaction);
        switch (index) {
            case 0:
                // firstImage.setImageResource(R.drawable.XXXX); 需要的话自行修改
                firstText.setTextColor(dark);
                firstLayout.setBackgroundColor(gray);
                // 如果fg1为空，则创建一个并添加到界面上
                if (fg1 == null) {
                    fg1 = new UpSucceedFragment();
                    titleTv.setText("发布成功");
                    fragmentTransaction.add(R.id.content, fg1);
                } else {
                    // 如果不为空，则直接将它显示出来
                    titleTv.setText("发布成功");
                    fragmentTransaction.show(fg1);
                }
                break;
            case 1:
                // secondImage.setImageResource(R.drawable.XXXX);
                secondText.setTextColor(dark);
                secondLayout.setBackgroundColor(gray);
                if (fg2 == null) {
                    fg2 = new UnderAuditFragment();
                    titleTv.setText("审核中");
                    fragmentTransaction.add(R.id.content, fg2);
                } else {
                    titleTv.setText("审核中");
                    fragmentTransaction.show(fg2);
                }
                break;
            case 2:
                // thirdImage.setImageResource(R.drawable.XXXX);
                thirdText.setTextColor(dark);
                thirdLayout.setBackgroundColor(gray);
                if (fg3 == null) {
                    fg3 = new UpFailedFragment();
                    titleTv.setText("发布失败");
                    fragmentTransaction.add(R.id.content, fg3);
                } else {
                    titleTv.setText("发布失败");
                    fragmentTransaction.show(fg3);
                }
                break;
        }
        fragmentTransaction.commit(); // 提交
    }

    /**
     * 当选中其中一个选项卡时，其他选项卡重置为默认
     */
    private void clearChioce() {
        // firstImage.setImageResource(R.drawable.XXX);
        firstText.setTextColor(gray);
        firstLayout.setBackgroundColor(whirt);
        // secondImage.setImageResource(R.drawable.XXX);
        secondText.setTextColor(gray);
        secondLayout.setBackgroundColor(whirt);
        // thirdImage.setImageResource(R.drawable.XXX);
        thirdText.setTextColor(gray);
        thirdLayout.setBackgroundColor(whirt);
    }

    /**
     * 隐藏Fragment
     *
     * @param fragmentTransaction
     */
    private void hideFragments(FragmentTransaction fragmentTransaction) {
        if (fg1 != null) {
            fragmentTransaction.hide(fg1);
        }
        if (fg2 != null) {
            fragmentTransaction.hide(fg2);
        }
        if (fg3 != null) {
            fragmentTransaction.hide(fg3);
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
                map.put("uid",app.getUid());
                response = C.asyncPost(app.getServer() + "getUpSituation", map);
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
                        data=new JSONObject(response.getString("data"));
                        /**
                         * 利用bundle向fragment传数据，注意fragmentTransaction的使用
                         */
                        Bundle b1 = new Bundle();
                        b1.putString("data",data.getString("succeed"));
                        Bundle b2 = new Bundle();
                        b2.putString("data", data.getString("audit"));
                        Bundle b3 = new Bundle();
                        b3.putString("data",data.getString("failed"));
                        fg1 = new UpSucceedFragment();
                        fg2 = new UnderAuditFragment();
                        fg3 = new UpFailedFragment();
                        fg1.setArguments(b1);
                        fg2.setArguments(b2);
                        fg3.setArguments(b3);
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.content, fg1);
                        fragmentTransaction.commit();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.add(R.id.content, fg2);
                        fragmentTransaction.commit();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.add(R.id.content, fg3);
                        fragmentTransaction.commit();
                        setChioceItem(0);
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
