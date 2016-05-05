package github.com.pwalan.dfood;

import
        android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.upload.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import github.com.pwalan.dfood.myview.RoundImageView;
import github.com.pwalan.dfood.utils.C;

/**
 * 用户登录/注册
 */
public class UserAcitvity extends Activity implements View.OnClickListener {

    protected static final int LOGIN = 1;
    protected static final int REGISTER = 2;
    protected static final int UPLOAD = 3;

    /**
     * 要上传图片的本地地址
     */
    private String picPath = null;

    /**
     * 腾讯云上传管理类
     */
    private UploadManager photoUploadMgr;


    /**
     * 上传参数
     */
    private String bucket;
    private String signUrl;
    private String sign;
    private String result;

    private ProgressDialog progressDialog;

    private String passwd;
    private String passwdconf;

    private EditText et_username;
    private EditText et_passwd;
    private EditText et_passwdconf;
    private RoundImageView img_head;

    private Button btn_login;
    private Button btn_register;
    private Button btn_toregister;

    private App app;
    private JSONObject response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        app=(App)getApplication();

        et_username = (EditText) findViewById(R.id.et_username);
        et_username.requestFocus();
        et_passwd = (EditText) findViewById(R.id.et_passwd);
        et_passwdconf = (EditText) findViewById(R.id.et_passwdconf);
        img_head = (RoundImageView) findViewById(R.id.img_head);
        img_head.setOnClickListener(this);

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(this);
        btn_toregister = (Button) findViewById(R.id.btn_toregister);
        btn_toregister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                //登录
                app.setUsername(et_username.getText().toString().trim());
                passwd=et_passwd.getText().toString().trim();

                if(progressDialog==null) progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("请稍后");
                progressDialog.setMessage("登录中...");
                progressDialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HashMap map=new HashMap();
                        map.put("username",app.getUsername());
                        map.put("passwd",passwd);
                        response= C.asyncPost(app.getServer()+"login",map);
                        handler.sendEmptyMessage(LOGIN);
                    }
                }).start();
                break;

            case R.id.btn_toregister:
                //点击去注册后此按钮和登录按钮隐藏，确认密码和注册显示
                btn_toregister.setVisibility(View.INVISIBLE);
                btn_login.setVisibility(View.INVISIBLE);
                et_passwdconf.setVisibility(View.VISIBLE);
                btn_register.setVisibility(View.VISIBLE);
                break;

            case R.id.btn_register:
                app.setUsername(et_username.getText().toString().trim());
                passwd=et_passwd.getText().toString().trim();
                passwdconf=et_passwdconf.getText().toString().trim();

                if(passwd.equals(passwdconf)){
                    if(progressDialog==null) progressDialog=new ProgressDialog(this);
                    progressDialog.setTitle("请稍后");
                    progressDialog.setMessage("注册中...");
                    progressDialog.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HashMap map=new HashMap();
                            map.put("username",app.getUsername());
                            map.put("passwd",passwd);
                            map.put("head",app.getHeadurl());
                            response= C.asyncPost(app.getServer()+"register",map);
                            Log.i("register_response",response.toString());
                            handler.sendEmptyMessage(REGISTER);
                        }
                    }).start();
                }else{
                    Toast.makeText(this,"两次密码不一致，请重新输入！",Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.img_head:
                //上传头像
                break;
            default:
                break;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOGIN:
                    //取消进度框
                    if(progressDialog!=null) progressDialog.dismiss();

                    try {
                        String status=response.getString("status");
                        if(status.equals("succeed")){
                            Toast.makeText(UserAcitvity.this,"登录成功！",Toast.LENGTH_SHORT).show();
                            app.setUid(response.getInt("uid"));
                            app.setIsLogin(true);

                            finish();
                        }else{
                            et_passwd.setText("");
                            Toast.makeText(UserAcitvity.this,"账号验证失败，请重试！",Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case REGISTER:
                    //取消进度框
                    if(progressDialog!=null) progressDialog.dismiss();

                    try {
                        String status=response.getString("status");
                        if(status.equals("succeed")){
                            Toast.makeText(UserAcitvity.this,"注册成功！",Toast.LENGTH_SHORT).show();
                            //注册成功后注册按钮、确认密码隐藏，登录、去注册显示
                            btn_toregister.setVisibility(View.VISIBLE);
                            btn_login.setVisibility(View.VISIBLE);
                            et_passwdconf.setVisibility(View.INVISIBLE);
                            btn_register.setVisibility(View.INVISIBLE);
                        }else{
                            Toast.makeText(UserAcitvity.this,"注册失败，用户名已存在！",Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case UPLOAD:
                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    // 获取app的签名
    private void getUploadImageSign(final String s) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    Log.d("Demo", "Start getSign");
                    URL url = new URL(s);
                    HttpURLConnection urlConnection = (HttpURLConnection) url
                            .openConnection();
                    InputStreamReader in = new InputStreamReader(urlConnection
                            .getInputStream());
                    BufferedReader buffer = new BufferedReader(in);
                    String inpuLine = null;
                    while ((inpuLine = buffer.readLine()) != null) {
                        result = inpuLine + "\n";
                    }
                    JSONObject jsonData = new JSONObject(result);
                    sign = jsonData.getString("sign");
                    Log.i("Sign", "SIGN: " + sign);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }).start();
    }

}
