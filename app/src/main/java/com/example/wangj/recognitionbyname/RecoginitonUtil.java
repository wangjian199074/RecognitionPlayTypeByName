package com.example.wangj.recognitionbyname;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    static final String sTopBottom360Brand = "vrtm, VRST, VRAT, 3DSVR, CRVR, KVR, MIVR, onvr, WPVR, SKHVR, DPVR, PRVR, adn, afes, aph, arm, bana, blit, ctrb, den, dpki, endo, hind, lot, mc, mlky, mnks, ofks, opg, osl, pri, SAMTK, taik, tami, tgi, tma, wow, VRHush";
    static final String sLeftRight180Brand = "Naughty+America, VPD, Virtual+Porn+Desire, vrporncom, PRW, prw, MilfVR, SexBabesVR, RealityPussy.com, SLR, VitualRealPorn.com, VRSmash, TmwVRnet.com, nam, czechvr, wankz+vr, BaDoink+VR, Virtual+Real+Porn, real+teens+vr, virtual+real+trans, Eudol+VR, TeensMegaWorldVR, Kink+VR";
    static final String sTopBottom180Brand = "StockingsVR";
    static final String s2D360Brand = "VRB, VRBangers";

    static final String sTopBottom360FileName = "Evolution of Verse, Invasion!, Invasion Baobab, Rainbow Crow, Asteroids!, Asteroids Baobab, Step to the Line, Giant, Shigeto, The Protectors walk in the ranger shoes, The possible, Under Neon Lights, Kinoscope VR, Notes on Blindness, OneRepublic Kids, Mr. Robot, Valen Reef, Walking New York, The Click Effect, Clouds Over Sidra, A History of Cuban Dance, Within Ted2016 VR Montage, Squarepusher Stor Eiglass, The Displaced VR, Waves of Grace, The Voodoo Healer, Take Flight VR, The Source VR, My Mother’s Wing, SNL 40 - Seinfeld Q&A, Virtual Giving Trip, Smile More: The Journey of a Song, Vice News VR, New Wave VR, Cold Lairs, Kaa Jungle, through Mowgli eyes, Trust in me, King Kong helicopter crash, Pete dragon 360, Rogue One Recon";
    static final String s2D360FileName = "Dear Angelica, Rain and Shine, Planet of the Couches, Buggy Night, On Ice, Special Delivery, Dreams of Dalí, Henry VR, Henry Oculus, Lost VR, Lost Oculus, Google Spotlight Story, Google Pearl, Google Help, Hunting of the Fallen VR, The JFK Assassination VR, Scuba Diving 360 Green Island Taiwan, The RooM VR horror, Angel Falls 360, Chrysalis HP, Rally 360, Ships Factions New Eden, Virtually Inside Tanks, Elephants on the Brink, From Pencil to Metal, Japan Changing Grow Old, 360 Autzen Player Experience, Grand Canyon VR, Grand Canyon 360, Experience Blue Angels 360";

    static List<String> mTopBottom360BrandList = new ArrayList<>();
    static List<String> mLeftRight180BrandList = new ArrayList<>();
    static List<String> mTopBottom180BrandList = new ArrayList<>();
    static List<String> m2D360BrandList = new ArrayList<>();

    public static String checkTypeByFileName(String fileName) {

        FileType type = new FileType();

        if (TextUtils.isEmpty(fileName)) {
            return getFileTypeByCode(-1);
        }

//        fileName = fileName.toLowerCase(Locale.ENGLISH);

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

        if (fileName.contains("3dh") || (fileName+"_").matches(".*[^a-z^A-Z]LR[^a-z^A-Z].*")
                || fileName.contains("SBS") || fileName.contains("HSBS")) {
            type.setMode(mlr);
        }

        if(fileName.matches(".+Left.{0,1}Right.+") || fileName.matches(".+Side.{0,1}By.{0,1}Side.+")
                || fileName.matches(".+Half.{0,1}Side.{0,1}By.{0,1}Side.+")) {
            type.setMode(mlr);
        }

        if (fileName.contains("3dv") || (fileName+"_").matches(".*[^a-z^A-Z]TB[^a-z^A-Z].*")
                || (fileName+"_").contains(".*[^a-z^A-Z]OU[^a-z^A-Z].*")
                || (fileName+"_").contains(".*[^a-z^A-Z]HOU[^a-z^A-Z].*")) {
            type.setMode(mtb);
        }
        if (fileName.matches(".+Top.{0,1}Bottom.+") || fileName.matches(".+Over.{0,1}Under.+")
                || fileName.matches(".+Half.{0,1}Over.{0,1}Under.+")) {
            type.setMode(mtb);
        }

        if (fileName.contains("180x180") || fileName.contains("180°")) {
            type.setView(v180);
        }
        if (fileName.contains("180")
                && (fileName.contains("VR") || fileName.matches(".+Virtual.{0,1}Reality.+"))) {
            type.setView(v180);
        }

        if (fileName.contains("360°")) {
            type.setView(v360);
        }
        if(fileName.contains("360")
                && (fileName.contains("VR") || fileName.matches(".+Virtual.{0,1}Reality.+"))) {
            type.setView(v360);
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
