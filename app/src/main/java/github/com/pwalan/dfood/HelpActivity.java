package github.com.pwalan.dfood;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


public class HelpActivity extends Activity {
    private float mPosX;
    private float mPosY;
    private float mCurrentPosX;
    private float mCurrentPosY;
    private ImageView imageview;

    private int[] imageIds = new int[]{
            R.drawable.picture0, R.drawable.picture1, R.drawable.picture2,
            R.drawable.picture3, R.drawable.picture4
    };
    private int len;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        len=imageIds.length;
        imageview = (ImageView) findViewById(R.id.helpimg);
        Toast.makeText(HelpActivity.this, "[ 1/"+len+" ]", Toast.LENGTH_SHORT).show();
        imageview.setImageResource(R.drawable.picture0);
        imageview.setOnTouchListener(new View.OnTouchListener() {
            int position=0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPosX = event.getX();
                        mPosY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCurrentPosX = event.getX();
                        mCurrentPosY = event.getY();
                        break;
                    default:
                        break;
                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if (mCurrentPosX - mPosX < 0) {
                        if(position<len-1){
                            position++;
                            Toast.makeText(HelpActivity.this, "[ "+(position+1)+"/"+len+" ]", Toast.LENGTH_SHORT).show();
                            imageview.setImageResource(imageIds[position]);
                        }else{
                            Intent intent = new Intent(HelpActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    else if (mCurrentPosX - mPosX > 0 ) {
                        if(position>0){
                            position--;
                            Toast.makeText(HelpActivity.this, "[ "+(position+1)+"/"+len+" ]", Toast.LENGTH_SHORT).show();
                            imageview.setImageResource(imageIds[position]);
                        }
                    }
                }
                return true;
            }
        });
    }

}
