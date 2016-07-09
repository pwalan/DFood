package github.com.pwalan.dfood;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import java.util.Random;


public class WelcomeActivity extends Activity {

    ImageView homeImage;
    Boolean isFirstIn;

    private int[] imageIds = new int[]{
            R.drawable.bg1, R.drawable.bg2, R.drawable.bg3,
            R.drawable.bg4, R.drawable.bg5, R.drawable.bg6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        homeImage = (ImageView) findViewById(R.id.homeimg);
        //通过随机数设置欢迎界面
        Random random=new Random();
        homeImage.setImageResource(imageIds[random.nextInt(imageIds.length)]);

        AlphaAnimation alphaAnimation = new AlphaAnimation((float) 0.5, 1);
        alphaAnimation.setDuration(3000);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent;
                SharedPreferences pref = getSharedPreferences("DFood", 0);
                isFirstIn = pref.getBoolean("isFirstIn", true);
                if (isFirstIn) {
                    intent = new Intent(WelcomeActivity.this, HelpActivity.class);
                }else{
                    intent = new Intent(WelcomeActivity.this, MainActivity.class);
                }
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("isFirstIn", false);
                editor.commit();
                startActivity(intent);
                finish();
            }
        });
        homeImage.setAnimation(alphaAnimation);
        homeImage.setVisibility(View.VISIBLE);
    }

}
