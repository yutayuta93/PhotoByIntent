package com.example.android.photobyintent;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    //Request Codes that is used for startActivityForResult()
    private static final int ACTION_TAKE_PHOTO = 1;
    private static final int ACTION_TAKE_PHOTO_SMALL = 2;
    private static final int ACTION_TAKE_VIDEO = 3;

    //パーミッション用定数
    private final int REQUEST_PERMISSION = 1;
    private static final String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    private ImageView mImageView;
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //パーミッションチェック
        int permission_write = ActivityCompat.checkSelfPermission(this, Manifest.permission
                .WRITE_EXTERNAL_STORAGE);
        int permission_read = ActivityCompat.checkSelfPermission(this,Manifest.permission
                .READ_EXTERNAL_STORAGE);
        if(permission_write != PackageManager.PERMISSION_GRANTED || permission_read !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,PERMISSIONS,REQUEST_PERMISSION);
        }


        Button btnIntentS = (Button)findViewById(R.id.btnIntentS);
        setBtnListenerOrDisable(btnIntentS,createOnClickListener(ACTION_TAKE_PHOTO_SMALL),MediaStore
                .ACTION_IMAGE_CAPTURE);

        Button btnIntent = (Button)findViewById(R.id.btnIntent);
        setBtnListenerOrDisable(btnIntent,createOnClickListener(ACTION_TAKE_PHOTO),MediaStore
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

        switch(actionCode){
            case ACTION_TAKE_PHOTO:
                File file = null;
                try{
                    file = createImageFile();
                    mCurrentPhotoPath = file.getAbsolutePath();
                    Uri photoUri = FileProvider.getUriForFile(this,
                            getApplicationContext().getPackageName() + ".provider",file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                }catch(Exception e){
                    e.printStackTrace();
                    file = null;
                    mCurrentPhotoPath = null;
                }
                break;
            default:
                break;
        }

        startActivityForResult(intent,actionCode);
    }

    private File createImageFile() throws IOException{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timeStamp = sdf.format(new Date());
        String fileName = JPEG_FILE_PREFIX + timeStamp;
        File albumDir = getAlbumDir();
        /*
        return new File(albumDir,fileName);
        */
        File imageFile = File.createTempFile(fileName,JPEG_FILE_SUFFIX,albumDir);
        return imageFile;
    }

    private File getAlbumDir() {
        File storageDir = null;
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            storageDir = getAlbumStorageDirectory(getString(R.string.album_name));
            if(storageDir != null){
                if(! storageDir.mkdirs()){
                    if(! storageDir.exists()){
                        Log.d("CameraSample","failed to create directory");
                        return null;
                    }
                }
            }
        }else{
            Log.d("Yutatakeuchi","External storage is not mounted READ/WRITE");
        }
        return storageDir;

    }
    private File getAlbumStorageDirectory(String albumName){
        return new File(Environment.getExternalStoragePublicDirectory(Environment
                .DIRECTORY_PICTURES),albumName);
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
                    handleBigCameraPhoto(data);
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

    private void handleBigCameraPhoto(Intent intent){
        Log.d("handleBigCameraPhoto","Result Received");
        if(mCurrentPhotoPath != null){
            galleryAddPic();
            mCurrentPhotoPath = null;
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case REQUEST_PERMISSION:
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] !=
                        PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"パーミッションが与えられませんでした",Toast.LENGTH_LONG).show();
                }
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] ==
                        PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"パーミッションが与えられました",Toast.LENGTH_LONG).show();
                }

        }
    }
}


