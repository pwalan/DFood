package github.com.pwalan.dfood;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

/**
 * 用户登录/注册
 */
public class UserAcitvity extends Activity {

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

    }

}
