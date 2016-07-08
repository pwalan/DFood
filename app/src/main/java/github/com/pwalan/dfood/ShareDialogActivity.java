package github.com.pwalan.dfood;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class ShareDialogActivity extends Activity implements View.OnClickListener {
    private RelativeLayout dialogLayout;
    private Button btn_qq, btn_qqkj, btn_weibo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_dialog);

        dialogLayout = (RelativeLayout) findViewById(R.id.dialog_layout);
        btn_qq=(Button)findViewById(R.id.btn_qq);
        btn_qq.setOnClickListener(this);
        btn_qqkj=(Button)findViewById(R.id.btn_qqkj);
        btn_qqkj.setOnClickListener(this);
        btn_weibo=(Button)findViewById(R.id.btn_weibo);
        btn_weibo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_layout:
                finish();
                break;
            case R.id.btn_qq:
                Toast.makeText(this,"分享到QQ好友",Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.btn_qqkj:
                Toast.makeText(this,"分享到QQ空间",Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.btn_weibo:
                Toast.makeText(this,"分享到新浪微博",Toast.LENGTH_SHORT).show();
                finish();
                break;
            default:
                finish();
                break;
        }
    }
}
