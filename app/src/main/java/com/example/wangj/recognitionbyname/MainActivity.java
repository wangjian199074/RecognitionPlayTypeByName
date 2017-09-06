package com.example.wangj.recognitionbyname;

import android.Manifest;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextView mFileName;
    private Button mStartButton;
    private TextView mFileNameList;
    private HashMap<String, String> fileTypeMap = new HashMap<>();
    private HashMap<String, String> errorRecognitionMap = new HashMap<>();
    private final String mReadFilePath = Environment.getExternalStorageDirectory() + "/RecognitionByName.txt";
    private final String mOutFilePath = Environment.getExternalStorageDirectory() + "/log.txt";

    private boolean allPermissionsGranted = false;
    private static final int RC_ASK_PERMISSION = 778;
    private String[] permissionArray = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    static final String sAddSymbol = ".{0,1}";

    private long mStartTime;
    private long mFinishTime;
    private long mStartTimeSingle;
    private long mFinishTimeSigle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFileName = (TextView) findViewById(R.id.filename_text);
        mStartButton = (Button) findViewById(R.id.start_button);
        mFileNameList = (TextView) findViewById(R.id.filename_list_text);

        mFileName.setText(mReadFilePath);

        if (!checkPermissions()) {
            requestPermissions();
        }

//        String spec = ".*[^a-z^A-Z]OU[^a-z^A-Z].*";
//
//        String str1 = "asdsadas_OU_";
//        String str2 = "As-OU_";
//        String str3 = "A.OU@I_";
//        String str4 = "O UI_";
//        String str5 = "EEEEEOUIOOOO_";
//
//        showToast(""+str1.matches(spec)+str2.matches(spec)+str3.matches(spec)+str4.matches(spec)+str5.matches(spec));

    }

    public void startRecognitionByName (View view) {
        //TODO start recognition
        mStartTime = System.currentTimeMillis();
        fileTypeMap.clear();
        errorRecognitionMap.clear();
        try {
            String encoding="GBK";
            File file=new File(mReadFilePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file),encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    String fileName = lineTxt.substring(0, lineTxt.lastIndexOf("."));
                    String setFormat = lineTxt.substring(lineTxt.lastIndexOf(" ")+1, lineTxt.length());
                    String recognitionType = RecoginitonUtil.checkTypeByFileName(fileName);
                    String notify;
                    if ("ERROR".equals(recognitionType)) {
                        notify = " : noMatch";
                        errorRecognitionMap.put(lineTxt, recognitionType);
                    } else {
                        Boolean isEqual = setFormat.equals(recognitionType);
                        notify = recognitionType+ " : " + (isEqual ? "√" : "X");
                        if (!isEqual) {
                            errorRecognitionMap.put(lineTxt, recognitionType);
                        }
                    }
                    fileTypeMap.put(lineTxt, notify);
                }
                read.close();
            } else {
                showToast("找不到指定的文件");
            }
        } catch (Exception e) {
            showToast("读取文件内容出错");
            e.printStackTrace();
        }
        mFinishTime = System.currentTimeMillis();

        ShowRecoginitionFileType();
        checkPermissions();
    }

    private void ShowRecoginitionFileType() {
        String result = "";
        Iterator iter = fileTypeMap.entrySet().iterator();
        while (iter.hasNext()) {
            HashMap.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();
            result += (key + "      :" + val )+ "\n";
        }

        Iterator ErrorIter = errorRecognitionMap.entrySet().iterator();
        result += "\n\n--------------------------------------------------------------------------------------\nError Reconition FileName is : \n";
        while (ErrorIter.hasNext()) {
            HashMap.Entry entry = (Map.Entry) ErrorIter.next();
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();
            result += (key + "      :" + val )+ "\n";
        }

        if ("".equals(result)) {
            return;
        }

        try {
            File file = new File(mOutFilePath);
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(result);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mFinishTime = System.currentTimeMillis();

        mStartButton.setText("Start : time = "+ (mFinishTime-mStartTime));
        mFileNameList.setText(result);
    }

    private void showToast (String str) {
        if ("".equals(str)) {
            return;
        }
        Toast.makeText(MainActivity.this, str , Toast.LENGTH_LONG ).show();
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RC_ASK_PERMISSION) {
            this.allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != 0) {
                    this.allPermissionsGranted = false;
                    break;
                }
            }

            if (!allPermissionsGranted) {
                requestPermissions();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private boolean checkPermissions() {
        boolean[] grantResults = hasPermissions(permissionArray);
        for (boolean grant : grantResults) {
            if (!grant) {
                return false;
            }
        }
        return true;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= 23) {

            return ContextCompat.checkSelfPermission(this, permission) == PermissionChecker.PERMISSION_GRANTED;
        }
        return true;
    }

    private boolean[] hasPermissions(String[] permissions) {
        if (permissions == null) {
            return new boolean[0];
        }

        int length = permissions.length;
        boolean[] grantResults = new boolean[length];
        for (int i = 0; i < length; i++) {
            grantResults[i] = hasPermission(permissions[i]);
        }

        return grantResults;
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(this.permissionArray, RC_ASK_PERMISSION);
        }
    }
}
