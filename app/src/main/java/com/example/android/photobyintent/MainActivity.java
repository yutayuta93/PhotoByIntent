package com.example.android.photobyintent;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    //Request Codes that is used for startActivityForResult()
    private static final int ACTION_TAKE_PHOTO = 1;
    private static final int ACTION_TAKE_PHOTO_SMALL = 2;
    private static final int ACTION_TAKE_VIDEO = 3;

    private ImageView mImageView;
    private Bitmap mImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnIntentS = (Button)findViewById(R.id.btnIntentS);
        setBtnListenerOrDisable(btnIntentS,createOnClickListener(ACTION_TAKE_PHOTO_SMALL),MediaStore
                .ACTION_IMAGE_CAPTURE);

        mImageView = (ImageView)findViewById(R.id.imageView1);

    }

    //実装済みイベントリスナを返すメソッド
    private Button.OnClickListener createOnClickListener(final int actionCode){
        return new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                dispatchIntent(actionCode);
            }
        };
    }

    /*
    インテントアクションが利用可能かチェックし、利用可能であればボタンにイベントリスナを登録。
    利用不可であれば、ボタンをクリックできなくする
     */
    private void setBtnListenerOrDisable(Button button,Button.OnClickListener listener,String
            action){
        if(isIntentAvailable(this,action)){
            button.setOnClickListener(listener);
        }else{
            button.setText(getText(R.string.cannnot) + " " + button.getText());
            button.setClickable(false);
        }

    }


    //インテントを作成・実行するメソッド
    private void dispatchIntent(int actionCode){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,actionCode);
    }


    //インテントアクションが利用可能かどうかをチェック
    public static boolean isIntentAvailable(Context context,String action){
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,PackageManager
                .MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ACTION_TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                }
                break;

            case ACTION_TAKE_PHOTO_SMALL:
                if(resultCode == RESULT_OK){
                    handleSmallCameraPhoto(data);
                }
                break;

            case ACTION_TAKE_VIDEO:
                if(resultCode == RESULT_OK){

                }
                break;
        }
    }


    private void handleSmallCameraPhoto(Intent intent){
        Uri data = intent.getData();
        if(data != null) {
            Log.d("RESULT_DATA", data.toString());
        }
        Bundle bundle = intent.getExtras();
        /*
        Set<String> keys = bundle.keySet();
        Iterator<String> iterator = keys.iterator();
        while(iterator.hasNext()){
            Log.d("KEY",iterator.next());
        }
        */
        mImageBitmap =(Bitmap)bundle.get("data");
        mImageView.setImageBitmap(mImageBitmap);
        mImageView.setVisibility(View.VISIBLE);

    }
}


