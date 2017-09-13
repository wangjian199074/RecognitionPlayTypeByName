package com.example.wangj.recognitionbyname;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;
import android.util.Log;
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
import android.support.v7.app.AppCompatActivity;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class MainActivity extends AppCompatActivity {
    private TextView mFileName;
    private Button mStartButton;
    private TextView mFileNameList;
    private HashMap<String, String> fileTypeMap = new HashMap<>();
    private HashMap<String, String> errorRecognitionMap = new HashMap<>();
    private final String mReadFilePath = Environment.getExternalStorageDirectory() + "/RecognitionByName.txt";
    private final String mOutFilePath = Environment.getExternalStorageDirectory() + "/log.txt";
    private String mUri = Environment.getExternalStorageDirectory() + "/tbs/LadyBug 5.mp4;" +
            "";
    private String mTestVideoPath = Environment.getExternalStorageDirectory() + "/tbs/";


    private boolean allPermissionsGranted = false;
    private static final int RC_ASK_PERMISSION = 778;
    private String[] permissionArray = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    static final String sAddSymbol = ".{0,1}";

    private long mStartTime;
    private long mFinishTime;

    String [] METADATA_KEYS = {"stereo_mode","stereo_mode","st3d","spherical", "stitched", "stitching_software", "projection_type", "stereo mode"
            , "album", "album_artist", "artist", "comment", "composer", "copyright", "creation_time", "date", "disc", "encoder"
            , "encoded_by", "filename", "genre", "language", "performer", "publisher", "service_name", "service_provider", "title", "filesize"
            , "chapter_count", "track", "bitrate", "duration", "audio_codec", "video_codec", "rotate", "icy_metadata", "icy_artist", "icy_title"
            , "framerate", "chapter_start_time", "chapter_end_time"};
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

    }

    public void getMetadataInfo(View view) throws ImageProcessingException, IOException {
//        FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
////        File file = new File(mUri);
////        Uri uri = getImageContentUri(MainActivity.this, file);
//        try {
////            String filePath = uri.getEncodedPath();
////            String filePath = uri.getPath();
////            showToast(filePath + "<<<<<<");
//            String str = "NULL + \n";
//            if (!TextUtils.isEmpty(mUri)) {
//                mmr.setDataSource(mUri);
////                showToast(mUri);
//                int count =0;
//                for (int i=0; i < METADATA_KEYS.length; i++) {
//                    String notation = mmr.extractMetadata(METADATA_KEYS[i]);
//                    str += METADATA_KEYS[i] + "  :  " +notation + "\n";
//                    count++;
//                }
//
//                str += "\n\n\n\n------------------------------------\n";
//                FFmpegMediaMetadataRetriever.Metadata metadata = mmr.getMetadata();
//                HashMap<String, String> all = metadata.getAll();
//                Iterator<Map.Entry<String, String>> iterator = all.entrySet().iterator();
//                while (iterator.hasNext()) {
//                    Map.Entry entry = (Map.Entry) iterator.next();
//                    Object key = entry.getKey();
//                    Object value = entry.getValue();
//                    str += key + ":" + value + "\n";
//                }
//
//                mFileNameList.setText(str);
//            }
//        } catch (Exception e) {
//            String name = "load rotation Exception: "+mUri;
//            Log.d("wj", e.getMessage());
//            showToast(e.toString());
//        } finally {
//            mmr.release();
//        }


        File file = new File(mUri);
        Metadata metadata = ImageMetadataReader.readMetadata(file);
        String str = "NULL + \n";
        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                str += tag + "\n";
                Log.d("wj", " " + tag);
            }
        }
        mFileNameList.setText(str);
    }

    public static Uri getImageContentUri(Context context, java.io.File file) {
        String filePath = file.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Video.Media._ID },
                MediaStore.Video.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/video/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (file.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Video.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
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
    public Uri LocationToUri(String location) {
        Uri uri = Uri.parse(location);
        if (uri.getScheme() == null)
            throw new IllegalArgumentException("location has no scheme");
        return uri;
    }


    public String getFilePath(Uri uri) {

        String fileURI = uri.toString();
        showToast(fileURI+"?");
        if (TextUtils.isEmpty(fileURI)) {
            showToast(fileURI+"isEmpty");
            return null;
        }
        if (!fileURI.contains("file://")) {
            showToast(fileURI+"!fileURI.contains");
            return null;
        }

        // This uri string was encoded, need to decode.
        fileURI = Uri.decode(fileURI);
        File sourceFile = new File(fileURI.replace("file://", ""));
        showToast(" sourceFile.exists() = "+sourceFile.exists());
        if (!sourceFile.exists()) {
            return null;
        }

        return sourceFile.getAbsolutePath();
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
