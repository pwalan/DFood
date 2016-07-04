package github.com.pwalan.dfood;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import github.com.pwalan.dfood.utils.C;


public class ForgetPasswdActivity extends Activity {
    protected static final int GET_DATA = 1;
    protected static final int FIND = 2;

    //startActivityForResult需要的intent
    private Intent lastIntent ;

    private TextView tv_question;
    private EditText et_answer;
    private Button btn_find;

    private App app;
    private String username;
    private JSONObject response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_passwd);

        lastIntent=getIntent();
        username=lastIntent.getStringExtra("username");

        app=(App)getApplication();

        tv_question=(TextView)findViewById(R.id.tv_question);
        et_answer=(EditText)findViewById(R.id.et_answer);
        btn_find=(Button)findViewById(R.id.btn_find);
        btn_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HashMap map = new HashMap();
                        map.put("username",username);
                        map.put("answer",et_answer.getText().toString().trim());
                        response = C.asyncPost(app.getServer() + "findPasswd", map);
                        handler.sendEmptyMessage(FIND);
                    }
                }).start();
            }
        });

        getData();
    }

    /**
     * 获取关注数据
     */
    private void getData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("username",username);
                response = C.asyncPost(app.getServer() + "getSquestion", map);
                handler.sendEmptyMessage(GET_DATA);
            }
        }).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_DATA:
                    try {
                        JSONObject jo=new JSONObject(response.getString("data"));
                        String question=jo.getString("question");
                        if(question.equals("failed")){
                            Toast.makeText(ForgetPasswdActivity.this,"用户名不存在或无密保问题",Toast.LENGTH_SHORT).show();
                        }else{
                            tv_question.setText(question);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case FIND:
                    try {
                        String passwd=response.getString("data");
                        if(passwd.equals("failed")){
                            Toast.makeText(ForgetPasswdActivity.this,"验证失败，无法找回密码！",Toast.LENGTH_SHORT).show();
                        }else{
                            lastIntent.putExtra("passwd",passwd);
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
