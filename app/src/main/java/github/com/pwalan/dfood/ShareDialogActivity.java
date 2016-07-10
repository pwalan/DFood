package github.com.pwalan.dfood;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.util.ArrayList;


public class ShareDialogActivity extends Activity implements View.OnClickListener {
    private RelativeLayout dialogLayout;
    private Button btn_qq, btn_qqkj, btn_weixin;

    private Tencent mTencent;
    private int rid;
    private String rname,rpic,content;
    private ArrayList<String> pics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_dialog);

        rid=getIntent().getIntExtra("rid",0);
        rname=getIntent().getStringExtra("rname");
        rpic=getIntent().getStringExtra("rpic");
        content=getIntent().getStringExtra("content");
        pics=new ArrayList<String>();
        pics.add(rpic);

        mTencent = Tencent.createInstance("1105530038", this.getApplicationContext());

        dialogLayout = (RelativeLayout) findViewById(R.id.dialog_layout);
        btn_qq = (Button) findViewById(R.id.btn_qq);
        btn_qq.setOnClickListener(this);
        btn_qqkj = (Button) findViewById(R.id.btn_qqkj);
        btn_qqkj.setOnClickListener(this);
        btn_weixin = (Button) findViewById(R.id.btn_weixin);
        btn_weixin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_layout:
                finish();
                break;
            case R.id.btn_qq:
                Toast.makeText(this, "分享到QQ好友", Toast.LENGTH_SHORT).show();
                sharetoQQ();
                finish();
                break;
            case R.id.btn_qqkj:
                Toast.makeText(this, "分享到QQ空间", Toast.LENGTH_SHORT).show();
                sharetoQQKJ();
                finish();
                break;
            case R.id.btn_weixin:
                Toast.makeText(this, "分享到微信", Toast.LENGTH_SHORT).show();
                finish();
                break;
            default:
                finish();
                break;
        }
    }

    /**
     * 分享到QQ
     */
    public void sharetoQQ() {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE,rname);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  content);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  "http://pwalan.cn/AndroidServer/recipeShare?rid="+rid);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,rpic);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "美食天下");
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, 0);
        mTencent.shareToQQ(ShareDialogActivity.this, params, new BaseUiListener());
    }

    /**
     * 分享到QQ空间
     */
    public void sharetoQQKJ(){
        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, rname);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, content);
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, "http://pwalan.cn/AndroidServer/recipeShare?rid=" + rid);//必填
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, pics);
        mTencent.shareToQzone(ShareDialogActivity.this, params, new BaseUiListener());
    }

    private class BaseUiListener implements IUiListener {

        public void onComplete(JSONObject response) {

        }

        protected void doComplete(JSONObject values) {
        }

        @Override
        public void onComplete(Object o) {

        }

        @Override
        public void onError(UiError e) {

        }

        @Override
        public void onCancel() {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, new BaseUiListener());
    }
}
