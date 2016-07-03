package github.com.pwalan.dfood;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 用户登录后修改密码和头像的窗体
 */
public class UserUpdateDialog extends Activity implements View.OnClickListener {
    //startActivityForResult需要的intent
    private Intent lastIntent ;

    private LinearLayout dialogLayout;
    private Button btn_ch_passwd,btn_ch_head,btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update_dialog);

        lastIntent = getIntent();

        dialogLayout = (LinearLayout) findViewById(R.id.dialog_layout);
        dialogLayout.setOnClickListener(this);
        btn_ch_head=(Button)findViewById(R.id.btn_ch_head);
        btn_ch_head.setOnClickListener(this);
        btn_ch_passwd=(Button)findViewById(R.id.btn_ch_passwd);
        btn_ch_passwd.setOnClickListener(this);
        btn_cancel=(Button)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_layout:
                finish();
                break;
            case R.id.btn_ch_head:
                startActivityForResult(new Intent(this, ChangeHeadActivity.class),0);
                break;
            case R.id.btn_ch_passwd:
                startActivityForResult(new Intent(this, ChangePasswdActivity.class), 1);
                break;
            default:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK)
        {
            //设置结果
            setResult(Activity.RESULT_OK, lastIntent);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
