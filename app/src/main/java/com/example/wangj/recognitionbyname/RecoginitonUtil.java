package com.example.wangj.recognitionbyname;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    static final String sAddSymbol = ".{0,1}";

    static final String sTopBottom360Brand = "vrtm, vrst, vrat, 3dsvr, crvr, kvr, mivr, onvr, wpvr, skhvr, dpvr, prvr, adn, afes, aph, arm, bana, blit, ctrb, den, dpki, endo, hind, lot, mc, mlky, mnks, ofks, opg, osl, pri, samtk, taik, tami, tgi, tma, wow, vrhush";
    static final String sLeftRight180Brand = "naughty+america, vpd, virtual+porn+desire, vrporncom, prw, prw, milfvr, sexbabesvr, realitypussy.com, slr, vitualrealporn.com, vrsmash, tmwvrnet.com, nam, czechvr, wankz+vr, badoink+vr, virtual+real+porn, real+teens+vr, virtual+real+trans, eudol+vr, teensmegaworldvr, kink+vr";
    static final String sTopBottom180Brand = "stockingsvr";
    static final String s2D360Brand = "vrb, vrbangers";

    static final String sTopBottom360FileName = "evolution of verse, invasion!, invasion baobab, rainbow crow, asteroids!, asteroids baobab, step to the line, giant, shigeto, the protectors walk in the ranger shoes, the possible, under neon lights, kinoscope vr, notes on blindness, onerepublic kids, mr. robot, valen reef, walking new york, the click effect, clouds over sidra, a history of cuban dance, within ted2016 vr montage, squarepusher stor eiglass, the displaced vr, waves of grace, the voodoo healer, take flight vr, the source vr, my mother’s wing, snl 40 - seinfeld q&a, virtual giving trip, smile more: the journey of a song, vice news vr, new wave vr, cold lairs, kaa jungle, through mowgli eyes, trust in me, king kong helicopter crash, pete dragon 360, rogue one recon";
    static final String s2D360FileName = "dear angelica, rain and shine, planet of the couches, buggy night, on ice, special delivery, dreams of dalí, henry vr, henry oculus, lost vr, lost oculus, google spotlight story, google pearl, google help, hunting of the fallen vr, the jfk assassination vr, scuba diving 360 green island taiwan, the room vr horror, angel falls 360, chrysalis hp, rally 360, ships factions new eden, virtually inside tanks, elephants on the brink, from pencil to metal, japan changing grow old, 360 autzen player experience, grand canyon vr, grand canyon 360, experience blue angels 360";

    static List<String> mTopBottom360BrandList = new ArrayList<>();
    static List<String> mLeftRight180BrandList = new ArrayList<>();
    static List<String> mTopBottom180BrandList = new ArrayList<>();
    static List<String> m2D360BrandList = new ArrayList<>();

    public static String checkTypeByFileName(String fileName) {

        FileType type = new FileType();

        if (TextUtils.isEmpty(fileName)) {
            return getFileTypeByCode(-1);
        }

        fileName = fileName.toLowerCase(Locale.ENGLISH);

        // TODO 播放格式识别
        type = checkPlayFormatByFied(fileName);

//        Log.d("wj","type = "+type.toString());
        if (type.getMode() != -1 && type.getView() != -1) {
            return getFileTypeByCode(type.toPlayType());
        }

        // TODO 发布厂牌识别
        FileType brandType = checkPlayFormatByBrand(fileName);

        if (brandType != null) {
            Log.d("wj","brandType = "+brandType.toString());
            if (type.getMode() != -1) {
                brandType.setMode(type.getMode());
            }

            if (type.getView() != -1) {
                brandType.setView(type.getView());
            }
            return getFileTypeByCode(brandType.toPlayType());
        }

        // TODO 视频名称识别
        FileType fileNameType = checkPlayFormatByFileName(fileName);
        if (fileNameType != null) {
            Log.d("wj","fileNameType = "+fileNameType.toString());
            if (type.getMode() != -1) {
                fileNameType.setMode(type.getMode());
            }

            if (type.getView() != -1) {
                fileNameType.setView(type.getView());
            }
            return getFileTypeByCode(fileNameType.toPlayType());
        }

        return getFileTypeByCode(type.toPlayType());
    }

    private static FileType checkPlayFormatByFileName(String fileName) {
        FileType type = new FileType();
//        sTopBottomFileName
        String[] split1 = sTopBottom360FileName.split(", ");
        for (String name : split1) {
            boolean isMatch = false;
            String[] nameWord = name.split(" ");
            for (String word: nameWord) {
                if (!fileName.contains(word)) {
                    isMatch = false;
                    break;
                }
                isMatch = true;
            }
            if (isMatch) {
                type.setFileType(mtb, v360);
                return type;
            }
        }

//        s2D360FileName
        String[] split2 = s2D360FileName.split(", ");
        for (String name : split2) {
            boolean isMatch = false;
            String[] nameWord = name.split("\\+");
            for (String word: nameWord) {
                if (!fileName.contains(word)) {
                    isMatch = false;
                    break;
                }
                isMatch = true;
            }
            if (isMatch) {
                type.setFileType(m2D, v360);
                return type;
            }
        }

        return null;
    }

    private static FileType checkPlayFormatByBrand(String fileName) {
        //Init brand map
        //the common brand match .{0,1}brand.+

        FileType type = new FileType();
        mTopBottom360BrandList = Arrays.asList(sTopBottom360Brand.split(", "));
        mLeftRight180BrandList = Arrays.asList(sLeftRight180Brand.split(", "));
        mTopBottom180BrandList = Arrays.asList(sTopBottom180Brand.split(", "));
        m2D360BrandList = Arrays.asList(s2D360Brand.split(", "));

        // Top Bottom 360 brand
        for (String brand : mTopBottom360BrandList) {

            int brandLenght = brand.length();

            int sublength = brandLenght+1 > fileName.length() ? fileName.length() : brandLenght+1;

            brand = brand.replace("+", sAddSymbol);
//            Log.d("wj", "fileName.substring(0, brandLenght+1) ="+fileName.substring(0, sublength)+"  :"+sAddSymbol + brand + ".{0,5}");
            if (fileName.substring(0, sublength).matches(sAddSymbol + brand + ".{0,5}")) {
                type.setFileType(mtb, v360);
                return type;
            }
        }

        // Left Right 180 brand
        for (String brand : mLeftRight180BrandList) {
            int brandLenght = brand.length();
            int sublength = brandLenght+1 > fileName.length() ? fileName.length() : brandLenght+1;

            brand = brand.replace("+", sAddSymbol);
            if (fileName.substring(0, sublength).matches(sAddSymbol + brand + ".{0,5}")) {
                type.setFileType(mlr, v180);
                return type;
            }

            // this brand is special, 012-czechvr-3d-2880x1440-60fps-oculusrift.mp4
            if ("czechvr".equals(brand)) {
                int sublengh1 = brandLenght+5 > fileName.length() ? fileName.length() : brandLenght+5;
                if (fileName.substring(0, sublengh1).contains(brand)) {
                    type.setFileType(mlr, v180);
                    return type;
                }
            }
        }

        // Top Bottom 180 brand
        for (String brand : mTopBottom180BrandList) {
            int brandLenght = brand.length();
            int sublength = brandLenght+1 > fileName.length() ? fileName.length() : brandLenght+1;

            brand = brand.replace("+", sAddSymbol);
            if (fileName.substring(0, sublength).matches(sAddSymbol + brand + ".{0,5}")) {
                type.setFileType(mtb, v180);
                return type;
            }

            // this brand is special, 012-czechvr-3d-2880x1440-60fps-oculusrift.mp4
            if ("czechvr".equals(brand)) {
                int sublengh1 = brandLenght+5 > fileName.length() ? fileName.length() : brandLenght+5;
                if (fileName.substring(0, sublengh1).contains(brand)) {
                    type.setFileType(mtb, v180);
                    return type;
                }
            }
        }

        // 2D 360 brand
        for (String brand : m2D360BrandList) {
            int brandLenght = brand.length();
            brand = brand.replace("+", sAddSymbol);
            int sublength = brandLenght+1 > fileName.length() ? fileName.length() : brandLenght+1;
//            Log.d("wj", "fileName.substring(0, brandLenght+1) ="+fileName.substring(0, sublength)+"  :"+sAddSymbol + brand + ".{0,5}");
            if (fileName.substring(0, sublength).matches(sAddSymbol + brand + ".{0,5}")) {
                type.setFileType(m2D,v360);
                return type;
            }
        }
        return null;
    }

    private static FileType checkPlayFormatByFied(String fileName) {

        FileType type = new FileType();
        fileName += "_";

        if (fileName.contains("3dh") || fileName.matches(".*[^a-z^A-Z]lr[^a-z^A-Z].*")
                || fileName.contains("sbs") || fileName.contains("hsbs")) {
            type.setMode(mlr);
        } else if(fileName.matches(".*left.{0,1}right.*") || fileName.matches(".*side.{0,1}by.{0,1}side.*")
                || fileName.matches(".*half.{0,1}side.{0,1}by.{0,1}side.*")) {
            type.setMode(mlr);
        }

        if (fileName.contains("3dv") || fileName.matches(".*[^a-z^A-Z]tb[^a-z^A-Z].*")
                || fileName.matches(".*[^a-z^A-Z]ou[^a-z^A-Z].*")
                || fileName.matches(".*[^a-z^A-Z]hou[^a-z^A-Z].*")) {
            if (type.getMode() == mlr) {
                type.setMode(-1);
            } else {
                type.setMode(mtb);
            }
        } else if (fileName.matches(".*top.{0,1}bottom.*") || fileName.matches(".*over.{0,1}under.*")
                || fileName.matches(".*half.{0,1}over.{0,1}under.*")) {
            if (type.getMode() == mlr) {
                type.setMode(-1);
            } else {
                type.setMode(mtb);
            }
        }

        if (fileName.contains("180x180") || fileName.contains("180°")) {
            type.setView(v180);
        } else if (fileName.contains("180")
                && (fileName.contains("vr") || fileName.matches(".*virtual.{0,1}reality.*"))) {
            type.setView(v180);
        }

        if (fileName.contains("360°")) {
            if (type.getView() == v180) {
                type.setView(-1);
            } else {
                type.setView(v360);
            }
        } else if (fileName.contains("360")
                && (fileName.contains("vr") || fileName.matches(".+virtual.{0,1}reality.+"))) {
            if (type.getView() == v180) {
                type.setView(-1);
            } else {
                type.setView(v360);
            }
        }
        return type;
    }

    static class FileType {
        private int mode = -1;
        private int view = -1;

        FileType () {
        }

        void setFileType(int mode, int view) {
            this.mode = mode;
            this.view = view;
        }

        public int toPlayType () {
            if (view != -1 && mode != -1) {
                return view + mode;
            } else {
                return -1;
            }
        }

        public void setMode(int mode) {
            this.mode = mode;
        }

        public void setView(int view) {
            this.view = view;
        }

        public int getView() {
            return view;
        }

        public int getMode() {
            return mode;
        }

        @Override
        public String toString() {
            return "mode = "+mode + "and view = "+view;
        }
    }

    public static String getFileTypeByCode (int type) {
        String typeStr;
        switch (type) {
            case e2D180D:
                typeStr = "2D_180";
                break;
            case e2D360D:
                typeStr = "2D_360";
                break;
            case e2DScreen:
                typeStr = "2D_Flat";
                break;
            case eLeftRight180D:
                typeStr = "LR_180";
                break;
            case eLeftRight360D:
                typeStr = "LR_360";
                break;
            case eLeftRightScreen:
                typeStr = "LR_Flat";
                break;
            case eTopBottom180D:
                typeStr = "OU_180";
                break;
            case eTopBottom360D:
                typeStr = "OU_360";
                break;
            case eTopBottomScreen:
                typeStr = "OU_Flat";
                break;
            default:
                typeStr = "ERROR";
                break;
        }
        return typeStr;
    }
}
