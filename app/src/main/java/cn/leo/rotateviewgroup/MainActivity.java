package cn.leo.rotateviewgroup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RotateViewGroup rotateViewGroup = findViewById(R.id.rotateViewGroup);
        //当前选择view回调
        rotateViewGroup.setSelectListener(new RotateViewGroup.rotateViewSelectListener() {
            @Override
            public void selectViewNo(int i, View v) {
                toast("select:"+i);
            }
        });
        //点击view回调
        rotateViewGroup.setClickListener(new RotateViewGroup.rotateViewClickListener() {
            @Override
            public void clickViewNo(int i, View v) {
                toast("click:"+i);
            }
        });
    }

    /**
     * 打印一下事件回调
     */
    private void toast(String s){
        Toast.makeText(MainActivity.this,s, Toast.LENGTH_SHORT).show();
    }
}
