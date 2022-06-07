package com.meishe.libmakeup;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.meishe.libmakeup.bean.BeautyData;
import com.meishe.libmakeup.bean.CategoryInfo;
import com.meishe.libmakeup.bean.Makeup;
import com.meishe.libmakeup.bean.MakeupCustomModel;
import com.meishe.libmakeup.bean.MakeupData;
import com.meishe.libmakeup.bean.MakeupEffectContent;
import com.meishe.libmakeup.bean.NoneItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author ms
 */
public class MakeupManager {

    private static volatile MakeupManager sMakeupCache;

    /**
     * 单妆路径
     */
    private static final String ASSETS_MAKEUP_CUSTOM_PATH = "beauty/customcompose";

    /**
     * 配置json name
     */
    private static final String ASSETS_MAKEUP_RECORD_NAME = File.separator + "info.json";

    /**
     * 记录应用的整妆index
     */
    private int mComposeIndex = 0;
    /**
     * 记录应用的单妆数据
     */
    private Map<String, MakeupData> mCustomMakeupArgsMap;
    private Map<String, List<MakeupData>> mCustomMakeupPackagesArgsMap = new TreeMap<>();

    public MakeupData getMakeupPackageEffect(String effectId, String mMakeupUUID) {
        if (mCustomMakeupPackagesArgsMap != null && !TextUtils.isEmpty(effectId)) {
            List<MakeupData> makeupDataList = mCustomMakeupPackagesArgsMap.get(effectId);
            if (makeupDataList != null) {
                for (MakeupData makeupData : makeupDataList) {
                    if (!TextUtils.isEmpty(mMakeupUUID) && TextUtils.equals(mMakeupUUID, makeupData.getUuid())) {
                        return makeupData;
                    }
                }
            }
        }
        return null;
    }


    /**
     * 记录应用的妆容index
     */
    private int mMakeupIndex = 0;

    /**
     * 记录使用的妆容数据
     */
    private Makeup mMakeup;

    /**
     * 滤镜效果
     */
    private Set<String> mFxSet = new HashSet<>();

    /**
     * 美妆效果中带的美颜，美型
     */
    private HashMap<String, String> mFxMap = new HashMap<>();


    /**
     * 记录整妆数据
     */
    private Makeup item;

    /**
     * 记录对单妆参数的调节内容
     */
    private HashMap<String, Object> makeupArgs = new HashMap<>();


    private MakeupManager() {
    }

    public static MakeupManager getInstacne() {
        if (sMakeupCache == null) {
            synchronized (MakeupManager.class) {
                if (sMakeupCache == null) {
                    sMakeupCache = new MakeupManager();
                }
            }
        }
        return sMakeupCache;
    }


    private List<BeautyData> sdMakeUps = new ArrayList<>();
    private List<BeautyData> localMakeUps = new ArrayList<>();


    public  ArrayList<CategoryInfo> getMakeupTab(Context context) {
        String path = ASSETS_MAKEUP_CUSTOM_PATH;
        String tabJsonStr = ParseJsonFile.readAssetJsonFile(context, path + ASSETS_MAKEUP_RECORD_NAME);
        if (TextUtils.isEmpty(tabJsonStr)) {
            return null;
        }
        //单妆
        ArrayList<CategoryInfo> customTab = ParseJsonFile.fromJson(tabJsonStr, new TypeToken<List<CategoryInfo>>() {
        }.getType());
        CategoryInfo makeupInfo = new CategoryInfo();
        makeupInfo.setId(0);
        makeupInfo.setMaterialType(21);
        makeupInfo.setDisplayName("Makeup");
        makeupInfo.setDisplayNameZhCn("妆容");
        customTab.add(0, makeupInfo);
        return customTab;
    }



    private BeautyData updateNewBeauty(Context context, Makeup data, File singleFile, String assetSu) {
        if (singleFile == null || !singleFile.exists() || !singleFile.isDirectory()) {
            return null;
        }
        if (data == null) {
            File jsonFile = new File(singleFile, "info.json");
            // 解析并设置name等属性
            String readInfo = ParseJsonFile.readSDJsonFile(context, jsonFile.getAbsolutePath());
            data = ParseJsonFile.fromJson(readInfo, Makeup.class);
        }
        if (data == null) {
            return null;
        }
        data.setCustom(true);
        data.setFolderPath(singleFile.getAbsolutePath());
        data.setMakeupId(singleFile.getName());
        data.setIsBuildIn(true);
        return data;
    }

    private BeautyData updateNewBeauty(Context context, boolean assetFlag, Makeup data, String filePath) {
        if (!assetFlag) {
            File singleFile = new File(filePath);
            if (singleFile == null || !singleFile.exists() || !singleFile.isDirectory()) {
                return null;
            }
        }
        if (data == null) {
            // 解析并设置name等属性
            String jsonPath = filePath + File.separator + "info.json";
            String readInfo = assetFlag ? ParseJsonFile.readAssetJsonFile(context, jsonPath) : ParseJsonFile.readSDJsonFile(context, jsonPath);
            try {
                data = ParseJsonFile.fromJson(readInfo, Makeup.class);
            } catch (Exception e) {
                return null;
            }
        }
        if (data == null) {
            return null;
        }
        data.setLocalFlag(true);
        data.setCustom(true);
        data.setFolderPath(filePath);
        String[] split = filePath.split("/");
        data.setMakeupId(split[split.length - 1]);
        data.setUuid(split[split.length - 1]);
        data.setIsBuildIn(true);
        return data;
    }



    public MakeupData getMakeupEffect(String effectId) {
        if (mCustomMakeupArgsMap == null || TextUtils.isEmpty(effectId)) {
            return null;
        }
        return mCustomMakeupArgsMap.get(effectId);
    }

    public void addMakeupEffect(String makeupId, MakeupData data) {
        if (TextUtils.isEmpty(makeupId)) {
            return;
        }
        if (mCustomMakeupArgsMap == null) {
            mCustomMakeupArgsMap = new HashMap<>();
        }
        mCustomMakeupArgsMap.put(makeupId, data);

    }

//    public void addMakeupPackageEffect(String makeupId, MakeupData data) {
//        if (TextUtils.isEmpty(makeupId)) {
//            return;
//        }
//        if (!mCustomMakeupPackagesArgsMap.containsKey(makeupId)) {
//            mCustomMakeupPackagesArgsMap.put(makeupId,
//                    new ArrayList<>());
//        }
//        mCustomMakeupPackagesArgsMap.get(makeupId).add(data);
//
//    }

    public void removeMakeupEffect(String makeupId) {
        if (TextUtils.isEmpty(makeupId) || (mCustomMakeupArgsMap == null)) {
            return;
        }
        mCustomMakeupArgsMap.remove(makeupId);

    }

    public Map<String, MakeupData> getCustomMakeupArgsMap() {
        return mCustomMakeupArgsMap;
    }

    public void clearCustomData() {
        if (mCustomMakeupArgsMap != null) {
            mCustomMakeupArgsMap.clear();
        }
        if (mCustomMakeupPackagesArgsMap != null) {
            mCustomMakeupPackagesArgsMap.clear();
        }
    }

    public int getComposeIndex() {
        return mComposeIndex;
    }

    public void setComposeIndex(int composeIndex) {
        this.mComposeIndex = composeIndex;
    }

    public int getMakeupIndex() {
        return mMakeupIndex;
    }

    public void setMakeupIndex(int makeupIndex) {
        mMakeupIndex = makeupIndex;
    }

    public void clearAllData() {
        mComposeIndex = 0;
        clearCustomData();
        mMakeupIndex = 0;
        clearMapFxData();
        clearMakeupArgs();
        clearData();
        item = null;
    }

    public void putMapFx(String fxName, String value) {
        if (mFxMap != null) {
            mFxMap.put(fxName, value);
        }
    }


    public void removeMapFx(String fxName) {
        if (mFxMap != null) {
            mFxMap.remove(fxName);
        }
    }

    public HashMap<String, String> getMapFxMap() {
        return mFxMap;
    }

    public void clearMapFxData() {
        mFxMap.clear();
    }

    public void putFx(String fxName) {
        if (!mFxSet.contains(fxName) && !TextUtils.isEmpty(fxName)) {
            mFxSet.add(fxName);
        }
    }

    public void removeFx(String fxName) {
        mFxSet.remove(fxName);
    }

    public Set<String> getFxSet() {
        return mFxSet;
    }

    public void clearData() {
        mFxSet.clear();
    }


    public Makeup getItem() {
        return item;
    }

    public void setItem(Makeup item) {
        this.item = item;
    }


    /**
     * 给单妆添加
     *
     * @param key
     * @param value
     */
    public void putMakeupArgs(String key, Object value) {
        makeupArgs.put(key, value);
    }

    public void clearMakeupArgs() {
        if (makeupArgs != null) {
            makeupArgs.clear();
        }
    }

    public HashMap<String, Object> getMakeupArgs() {
        return makeupArgs;
    }


    public void installNewMakeUp(String downloadPath) {

    }

    public Makeup getMakeup() {
        return mMakeup;
    }

    public void setMakeup(Makeup makeup) {
        this.mMakeup = makeup;
    }

    public void clearFilterFx() {
        if (mFxSet!=null){
            mFxSet.clear();
        }
    }
}
