package github.com.pwalan.dfood;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import github.com.pwalan.dfood.utils.C;


public class MakeCommentActivity extends Activity {
    protected static final int COMMENT=1;

    private App app;
    private int rid;
    private JSONObject response;

    //startActivityForResult需要的intent
    private Intent lastIntent ;

    private EditText et_comment;
    private Button btn_publish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_comment);

        rid=getIntent().getIntExtra("rid",0);
        app=(App)getApplication();
        lastIntent = getIntent();

        et_comment = (EditText) findViewById(R.id.et_comment);
        btn_publish=(Button)findViewById(R.id.btn_publish);
        btn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String comment=et_comment.getText().toString().trim();
                        HashMap map = new HashMap();
                        map.put("uid",app.getUid());
                        map.put("rid", rid);
                        map.put("comment",comment);
                        response = C.asyncPost(app.getServer() + "makeComment", map);
                        handler.sendEmptyMessage(COMMENT);
                    }
                }).start();
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case COMMENT:
                    try {
                        String result=response.getString("data");
                        if(result.equals("add")){
                            Toast.makeText(MakeCommentActivity.this,"发表成功",Toast.LENGTH_SHORT).show();
                            //设置结果
                            setResult(Activity.RESULT_OK, lastIntent);
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
