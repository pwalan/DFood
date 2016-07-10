package github.com.pwalan.dfood;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity; // 注意这里我们导入的V4的包，不要导成app的包了
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import github.com.pwalan.dfood.fragment.FoodCircleFragment;
import github.com.pwalan.dfood.fragment.HomeFragment;
import github.com.pwalan.dfood.fragment.RecipeFragment;
import github.com.pwalan.dfood.fragment.SeasonsetFragment;
import github.com.pwalan.dfood.myview.SlidingMenu;
import github.com.pwalan.dfood.utils.C;
import github.com.pwalan.dfood.utils.QCloud;

/**
 * app整体框架
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {
    //下载头像
    protected static final int DOWNLOAD_FILE_DONE = 1;
    //获取数据
    protected static final int GET_DATA = 2;

    private App app;
    private Bitmap bitmap;
    private JSONObject response;

    //侧滑菜单
    private SlidingMenu menu;
    //侧滑菜单中的登录注册按钮
    private Button btn_user;

    // 顶部栏显示
    private ImageView titleLeftImv;
    private TextView titleTv;
    private ImageView img_up;
    // 定义4个Fragment对象
    private HomeFragment fg1;
    private SeasonsetFragment fg2;
    private RecipeFragment fg3;
    private FoodCircleFragment fg4;
    // 帧布局对象，用来存放Fragment对象
    private FrameLayout frameLayout;
    // 定义每个选项中的相关控件
    private RelativeLayout firstLayout;
    private RelativeLayout secondLayout;
    private RelativeLayout thirdLayout;
    private RelativeLayout fourthLayout;
    private ImageView firstImage;
    private ImageView secondImage;
    private ImageView thirdImage;
    private ImageView fourthImage;
    private TextView firstText;
    private TextView secondText;
    private TextView thirdText;
    private TextView fourthText;
    // 定义几个颜色
    private int whirt = 0xFFFFFFFF;
    private int gray = 0xFF66CCFF;
    private int dark = 0xff000000;
    // 定义FragmentManager对象管理器
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        initView(); // 初始化界面控件
        getData();
        setChioceItem(0); // 初始化页面加载时显示第一个选项卡
        menu = (SlidingMenu) findViewById(R.id.slide_menu);
    }

    /**
     * 初始化页面
     */
    private void initView() {
        app = (App) getApplication();
        btn_user = (Button) findViewById(R.id.btn_user);
        // 初始化页面标题栏
        titleLeftImv = (ImageView) findViewById(R.id.title_imv);
        //顶部左侧的图标点击事件
        titleLeftImv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.toggle();
            }
        });
        //顶部右侧的加号图标点击启动上传
        img_up = (ImageView) findViewById(R.id.img_up);
        img_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (app.isLogin()) {
                    startActivity(new Intent(MainActivity.this, UploadActivity.class));
                } else {
                    Toast.makeText(MainActivity.this, "请登录！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //顶部标签
        titleTv = (TextView) findViewById(R.id.title_text_tv);
        titleTv.setText("首 页");
        // 初始化底部导航栏的控件
        firstImage = (ImageView) findViewById(R.id.first_image);
        secondImage = (ImageView) findViewById(R.id.second_image);
        thirdImage = (ImageView) findViewById(R.id.third_image);
        fourthImage = (ImageView) findViewById(R.id.fourth_image);
        firstText = (TextView) findViewById(R.id.first_text);
        secondText = (TextView) findViewById(R.id.second_text);
        thirdText = (TextView) findViewById(R.id.third_text);
        fourthText = (TextView) findViewById(R.id.fourth_text);
        firstLayout = (RelativeLayout) findViewById(R.id.first_layout);
        secondLayout = (RelativeLayout) findViewById(R.id.second_layout);
        thirdLayout = (RelativeLayout) findViewById(R.id.third_layout);
        fourthLayout = (RelativeLayout) findViewById(R.id.fourth_layout);
        firstLayout.setOnClickListener(MainActivity.this);
        secondLayout.setOnClickListener(MainActivity.this);
        thirdLayout.setOnClickListener(MainActivity.this);
        fourthLayout.setOnClickListener(MainActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.first_layout:
                setChioceItem(0);
                break;
            case R.id.second_layout:
                setChioceItem(1);
                break;
            case R.id.third_layout:
                setChioceItem(2);
                break;
            case R.id.fourth_layout:
                setChioceItem(3);
                break;
            default:
                break;
        }
    }

    /**
     * 设置点击选项卡的事件处理
     *
     * @param index 选项卡的标号：0, 1, 2, 3
     */
    private void setChioceItem(int index) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        clearChioce(); // 清空, 重置选项, 隐藏所有Fragment
        hideFragments(fragmentTransaction);
        switch (index) {
            case 0:
                // firstImage.setImageResource(R.drawable.XXXX); 需要的话自行修改
                firstText.setTextColor(dark);
                firstLayout.setBackgroundColor(gray);
                titleTv.setText(R.string.fg1_name);
                // 如果fg1为空，则创建一个并添加到界面上
                if (fg1 == null) {
                    fg1 = new HomeFragment();
                    fragmentTransaction.add(R.id.content, fg1);
                } else {
                    // 如果不为空，则直接将它显示出来
                    fragmentTransaction.show(fg1);
                }
                break;
            case 1:
                // secondImage.setImageResource(R.drawable.XXXX);
                secondText.setTextColor(dark);
                secondLayout.setBackgroundColor(gray);
                titleTv.setText(R.string.fg2_name);
                if (fg2 == null) {
                    fg2 = new SeasonsetFragment();
                    fragmentTransaction.add(R.id.content, fg2);
                } else {
                    fragmentTransaction.show(fg2);
                }
                break;
            case 2:
                // thirdImage.setImageResource(R.drawable.XXXX);
                thirdText.setTextColor(dark);
                thirdLayout.setBackgroundColor(gray);
                titleTv.setText(R.string.fg3_name);
                if (fg3 == null) {
                    fg3 = new RecipeFragment();
                    fragmentTransaction.add(R.id.content, fg3);
                } else {
                    fragmentTransaction.show(fg3);
                }
                break;
            case 3:
                // fourthImage.setImageResource(R.drawable.XXXX);
                fourthText.setTextColor(dark);
                fourthLayout.setBackgroundColor(gray);
                titleTv.setText(R.string.fg4_name);
                if (fg4 == null) {
                    fg4 = new FoodCircleFragment();
                    fragmentTransaction.add(R.id.content, fg4);
                } else {
                    fragmentTransaction.show(fg4);
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
        // fourthImage.setImageResource(R.drawable.XXX);
        fourthText.setTextColor(gray);
        fourthLayout.setBackgroundColor(whirt);
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
        if (fg4 != null) {
            fragmentTransaction.hide(fg4);
        }
    }

    /**
     * 获取网落图片资源
     *
     * @param url
     */
    public void getHttpBitmap(final String url) {
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
                    //不使用缓存
                    conn.setUseCaches(false);
                    //这句可有可无，没有影响
                    //conn.connect();
                    //得到数据流
                    InputStream is = conn.getInputStream();
                    //解析得到图片
                    bitmap = BitmapFactory.decodeStream(is);
                    //关闭数据流
                    is.close();
                    handler.sendEmptyMessage(DOWNLOAD_FILE_DONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
                response = C.asyncPost(app.getServer() + "getHomeRecipes", map);
                handler.sendEmptyMessage(GET_DATA);
            }
        }).start();
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD_FILE_DONE:
                    titleLeftImv.setImageBitmap(bitmap);
                    Log.i("main", "获取首页头像");
                    getData();
                    break;
                case GET_DATA:
                    try {
                        Bundle bundle = new Bundle();
                        bundle.putString("data",response.getString("data"));
                        fg1=new HomeFragment();
                        fg1.setArguments(bundle);
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.content, fg1);
                        fragmentTransaction.commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //********************************************************
    //菜单中的点击事件

    public void onUserClicked(View v) {
        if (app.isLogin()) {
            menu.toggle();
            startActivityForResult(new Intent(this, UserUpdateDialog.class), 0);
        } else {
            menu.toggle();
            startActivityForResult(new Intent(this, UserAcitvity.class), 1);
        }
    }

    public void onConcernClicked(View v) {
        if (app.isLogin()) {
            menu.toggle();
            startActivity(new Intent(this, ShowConcernActivity.class));
        } else {
            Toast.makeText(MainActivity.this, "请登录！", Toast.LENGTH_SHORT).show();
        }
    }

    public void onFavoriteClicked(View v) {
        if (app.isLogin()) {
            menu.toggle();
            startActivity(new Intent(this, ShowFavoritesActivity.class));
        } else {
            Toast.makeText(MainActivity.this, "请登录！", Toast.LENGTH_SHORT).show();
        }
    }

    public void onMyupClicked(View v) {
        if (app.isLogin()) {
            menu.toggle();
            Intent intent = new Intent(this, ShowMyUp.class);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "请登录！", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSearchClicked(View v){
        menu.toggle();
        startActivity(new Intent(this,SearchActivity.class));
    }

    public void onHelpClicked(View v){
        menu.toggle();
        startActivity(new Intent(this,HelpActivity.class));
    }

    public void onExitClicked(View v) {
        finish();
    }

    //********************************************************

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 0) {
            btn_user.setText("登录/注册");
            titleLeftImv.setImageResource(R.drawable.user);
            app.setIsLogin(false);
        }
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            Log.d("dfood", "login finished");
            if (app.isLogin()) {
                btn_user.setText(app.getUsername());
                if (app.getHeadurl() != null) {
                    getHttpBitmap(app.getHeadurl());
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();
        }
        return false;
    }

    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true;
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }
}