package com.example.encryption;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.myapplication.R;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallbackWithBeforeParam;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.permissionx.guolindev.request.ForwardScope;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView imageList;
    private List<String> pathList;
    private String lIBR6juwOCB6MuNJGbD7jOnT6kN89Epk;
    private String key = lIBR6juwOCB6MuNJGbD7jOnT6kN89Epk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button selectImage = (Button) findViewById(R.id.selectImage);
        Button encrypt = (Button) findViewById(R.id.encrypt);
        Button decode = (Button) findViewById(R.id.decode);
        imageList = (TextView) findViewById(R.id.imageList);
        TextView log = (TextView) findViewById(R.id.log);
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageList.setText("");
                pathList = null;
                Toast.makeText(getApplicationContext(), "????????????????????????", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES,new String[]{"*/*"});
                //intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*;*/xky");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                //????????????-->bitmap-->uri??????
                startActivityForResult(intent.createChooser(intent, "Select Picture"), 1);
            }
        });
        //????????????
        encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] encryp;
                try {
                    for (int i = 0; i < pathList.size(); i++) {
                        String url = pathList.get(i);
                        //??????????????????
                        byte[] picData = getPicData(url);
                        log.append("?????????" + url + "??????????????????...\r\n");
                        //????????????
                        byte[] inputData = Encryption.encrypt(picData, Base64.decode("lIBR6juwOCB6MuNJGbD7jOnT6kN89Epk", 0));
                        log.append("?????????" + url + "?????????...\r\n");
                        String savePath;
                        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                        {
                             savePath=url.split("\\.")[0]+"."+url.split("\\.")[1]+"."+url.split("\\.")[2] + ".sky";
                        }else {
                             savePath=url.split("\\.")[0]+ ".sky";
                        }
                        //????????????
                        savePicForByte(inputData, savePath);
                        log.append("?????????" + savePath + "?????????...\r\n");
                        //encryp = encryption("??????".getBytes());
                        //System.out.println(Base64.encodeToString(encryp, 0));
                    }
                    pathList.clear();
                    imageList.setText("");
                    Toast.makeText(getApplicationContext(), "???????????????", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    log.append("?????????" + "????????????...\r\n");

                    e.printStackTrace();
                }

            }
        });
        //????????????
        decode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //??????
                try {
                    for (int i = 0; i < pathList.size(); i++) {

                        String url = pathList.get(i);
                        //????????????
                        byte[] pic = getPicData(url);
                        log.append("?????????" + url + "????????????...\r\n");
                        //??????
                        byte[] outputData = new byte[0];

                        outputData = Encryption.decrypt(pic, Base64.decode("lIBR6juwOCB6MuNJGbD7jOnT6kN89Epk", 0));
                        log.append("?????????" + url + "????????????...\r\n");
                        //????????????
                        String savePath;
                        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                            savePath=url.split("\\.")[0]+"."+url.split("\\.")[1]+"."+url.split("\\.")[2] + ".jpg";
                        }else {
                            savePath=url.split("\\.")[0] + ".jpg";
                        }
                        savePicForByte(outputData, savePath);
                        log.append("?????????" + savePath+ "?????????...\r\n");
                    }
                    pathList.clear();
                    imageList.setText("");
                    Toast.makeText(getApplicationContext(), "????????????", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    log.append("?????????" + "????????????...\r\n");
                    e.printStackTrace();
                }
            }
        });
        requestPermission();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            ClipData clipData = data.getClipData();
            System.out.println("=============clipData" + clipData);
            pathList = new ArrayList<>();
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();
                    String path = FilePathTool.getFileAbsolutePath(getApplicationContext(), uri);
                    pathList.add(path);
                    imageList.append(path + "\r\n");
                }
            }
            //TODO ??????????????????????????????path???????????????
            System.out.println("pathList:" + pathList);
        }
    }


    //?????????????????????????????????proice????????????
    private byte[] encryption(byte[] data) {
        String inputStr = "DESede";
        byte[] inputData;
        byte[] outputData = new byte[0];
        try {
            System.err.println("??????:" + new String(data, "utf-8"));
            // ???????????????
//        byte[] key = Encryption.initKey();
//        System.err.println("??????:\t" + Base64.encodeToString(key,0));
            System.out.println("?????????\t" + key);
            // ??????
            inputData = Encryption.encrypt(data, Base64.decode(key, 0));
            System.err.println("?????????:\t" + new String(inputData, "utf-8"));
            // ??????
            outputData = Encryption.decrypt(inputData, Base64.decode(key, 0));
            System.out.println("????????????" + new String(outputData, "utf-8"));

        } catch (Exception e) {
            System.out.println(e);
        }
        return outputData;
    }

    //???????????????????????????
    private byte[] getPicData(String url) {
        byte[] b = null;
        try {
            InputStream is = new FileInputStream(url);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] bytes = new byte[8 * 1024];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len;
            while ((len = bis.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
            }
            b = bos.toByteArray();
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    //????????????
    private void savePicForByte(byte[] bytes, String url) {
        System.out.println("????????????" + url);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(url));
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //????????????
    private void requestPermission() {
        PermissionX.init(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onExplainRequestReason(
                        new ExplainReasonCallbackWithBeforeParam() {
                            @Override
                            public void onExplainReason(ExplainScope scope, List<String> deniedList, boolean beforeRequest) {
                                scope.showRequestReasonDialog(deniedList, "???????????????????????????????????????????????????", "????????????");
                            }
                        })
                .onForwardToSettings(new ForwardToSettingsCallback() {
                    @Override
                    public void onForwardToSettings(ForwardScope scope, List<String> deniedList) {
                        scope.showForwardToSettingsDialog(deniedList, "??????????????????????????????????????????????????????", "????????????");
                    }
                })
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
                        if (allGranted) {
                            // Toast.makeText(MainActivity.this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(MainActivity.this, "???????????????????????????" + deniedList, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}