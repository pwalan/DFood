package github.com.pwalan.dfood;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * 用户登录/注册
 */
public class UserAcitvity extends Activity implements View.OnClickListener{

    private String passwd;
    private String passwdconf;

    private EditText et_username;
    private EditText et_passwd;
    private EditText et_passwdconf;

    private Button btn_login;
    private Button btn_register;
    private Button btn_toregister;

    private App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        et_username=(EditText)findViewById(R.id.et_username);
        et_passwd=(EditText)findViewById(R.id.et_passwd);
        et_passwdconf=(EditText)findViewById(R.id.et_passwdconf);

        btn_login=(Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        btn_register=(Button)findViewById(R.id.btn_register);
        btn_register.setOnClickListener(this);
        btn_toregister=(Button)findViewById(R.id.btn_toregister);
        btn_toregister.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                break;
            case R.id.btn_toregister:
                //点击去注册后此按钮和登录按钮隐藏，确认密码和注册显示
                btn_toregister.setVisibility(View.INVISIBLE);
                btn_login.setVisibility(View.INVISIBLE);
                et_passwdconf.setVisibility(View.VISIBLE);
                btn_register.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_register:
                break;
            default:
                break;
        }
    }
}
