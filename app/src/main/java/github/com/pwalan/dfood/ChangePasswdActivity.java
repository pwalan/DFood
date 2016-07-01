package github.com.pwalan.dfood;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import github.com.pwalan.dfood.utils.C;


public class ChangePasswdActivity extends Activity {
    //获取数据
    protected static final int GET_DATA = 1;

    private EditText et_oldpasswd, et_newpasswd,et_confnewpasswd;
    private Button btn_change;

    private App app;
    private JSONObject response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_passwd);

        app=(App)getApplication();

        et_confnewpasswd=(EditText)findViewById(R.id.et_confnewpasswd);
        et_newpasswd=(EditText)findViewById(R.id.et_newpasswd);
        et_oldpasswd=(EditText)findViewById(R.id.et_oldpasswd);
        btn_change=(Button)findViewById(R.id.btn_change);
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(app.getPasswd().equals(et_oldpasswd.getText().toString().trim())){
                    if(et_newpasswd.getText().toString().trim().equals(et_confnewpasswd.getText().toString().trim())){
                        app.setPasswd(et_newpasswd.getText().toString().trim());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HashMap map = new HashMap();
                                map.put("uid",app.getUid());
                                map.put("passwd",et_newpasswd.getText().toString().trim());
                                response = C.asyncPost(app.getServer() + "changePasswd", map);
                                handler.sendEmptyMessage(GET_DATA);
                            }
                        }).start();
                    }else{
                        Toast.makeText(ChangePasswdActivity.this,"两次密码不一致！",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ChangePasswdActivity.this,"原密码错误！",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_DATA:
                    try {
                        String data=response.getString("data");
                        if(data.equals("changed")){
                            Toast.makeText(ChangePasswdActivity.this,"更改成功！",Toast.LENGTH_SHORT).show();
                            finish();
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
