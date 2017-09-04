package com.example.wangj.recognitionbyname;

import android.text.TextUtils;

import java.util.Locale;

/**
 * Created by wangj on 2017/9/4.
 */

public class RecoginitonUtil {

    static final int eError = -1;
    static final int e2D180D = 0;
    static final int e2D360D = 1;
    static final int e2DScreen = 2;
    static final int eLeftRight180D = 3;
    static final int eLeftRight360D = 4;
    static final int eLeftRightScreen = 5;
    static final int eTopBottom180D = 6;
    static final int eTopBottom360D = 7;
    static final int eTopBottomScreen = 8;

    static final int m2D = 0;
    static final int mlr = 3;
    static final int mtb = 6;
    static final int v180 = 0;
    static final int v360 = 1;
    static final int vScreen = 2;



//    String View = "Screen, 180, 360";
//    String Mode = "2D, lr, tb";

    public static String checkTypeByFileName2(String fileName) {

        int type = e2DScreen;

        if (TextUtils.isEmpty(fileName)) {
            return getFileTypeByCode(-1);
        }

//        fileName = fileName.toLowerCase(Locale.ENGLISH);

        // TODO 播放格式识别

        type = checkPlayFormatByFied(fileName);

        if (type > 2) {
            return getFileTypeByCode(type);
        }
        // TODO 发布厂牌识别
        checkPlayFormatByBrand(fileName);

        // TODO 视频名称识别


        return getFileTypeByCode(-1);
    }

    private static int checkPlayFormatByBrand(String fileName) {
        int view = vScreen;
        int mode = m2D;
        return -1;
    }

    private static int checkPlayFormatByFied(String fileName) {

        int view = vScreen;
        int mode = m2D;
        if (fileName.contains("180x180") || fileName.contains("180°")) {
            view = v180;
        }
        if (fileName.contains("180")
                && (fileName.contains("VR") || fileName.matches(".+Virtual.{1}Reality.+"))) {
            view = v180;
        }

        if (fileName.contains("360°")) {
            view = v360;
        }
        if(fileName.contains("360")
                && (fileName.contains("VR") || fileName.matches(".+Virtual.{1}Reality.+"))) {
            view = v360;
        }


        if (fileName.contains("3dh") || fileName.contains("LR")
                || fileName.contains("SBS") || fileName.contains("HSBS")) {
            mode = mlr;
        }
        if(fileName.matches(".+Left.{1}Right.+") || fileName.matches(".+Side.{1}By.{1}Side.+")
                || fileName.matches(".+Half.{1}Side.{1}By.{1}Side.+")) {
            mode = mlr;
        }

        if (fileName.contains("3dv") || fileName.contains("TB")
                || fileName.contains("OU") || fileName.contains("HOU")) {
            mode = mtb;
        }
        if (fileName.matches(".+Top.{1}Bottom.+") || fileName.matches(".+Over.{1}Under.+")
                || fileName.matches(".+Half.{1}Over.{1}Under.+")) {
            mode = mtb;
        }
        return view +mode;
    }

    public static String checkTypeByFileName(String fileName) {

        if (TextUtils.isEmpty(fileName)) {
            return getFileTypeByCode(-1);
        }

        fileName = fileName.toLowerCase(Locale.ENGLISH);

        if (fileName.contains("180x180_3dh")) {
            return getFileTypeByCode(3); // 3D LR 180
        } else if (fileName.contains("180x180_3dv")) {
            return getFileTypeByCode(6); // 3D TB 180
        }

        if (fileName.contains("360")) {
            if (fileName.contains("3dh")) {
                return getFileTypeByCode(4); // 3D LR 360
            } else if (fileName.contains("3dv")) {
                return getFileTypeByCode(7); // 3D TB 360
            }
        }

        if (fileName.endsWith("_3dv")
                && !fileName.contains("180") && !fileName.contains("180x180")) {
            return getFileTypeByCode(7);// 3D TB 360
        }

        return getFileTypeByCode(-1);
    }


    public static String getFileTypeByCode (int type) {
        String typeStr;
        switch (type) {
            case e2D180D:
                typeStr = "2D-180";
                break;
            case e2D360D:
                typeStr = "2D-360";
                break;
            case e2DScreen:
                typeStr = "2D-Screen";
                break;
            case eLeftRight180D:
                typeStr = "LeftRight-180";
                break;
            case eLeftRight360D:
                typeStr = "LeftRight-360";
                break;
            case eLeftRightScreen:
                typeStr = "LeftRight-Screen";
                break;
            case eTopBottom180D:
                typeStr = "TopBottom-180";
                break;
            case eTopBottom360D:
                typeStr = "TopBottom-360";
                break;
            case eTopBottomScreen:
                typeStr = "TopBottom-Screen";
                break;
            default:
                typeStr = "ERROR";
                break;
        }
        return typeStr;
    }
}
