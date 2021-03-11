package com.pj567.movie.cache;

import com.lzy.okgo.utils.IOUtils;
import com.pj567.movie.bean.VodInfo;
import com.pj567.movie.data.AppDataManager;

import java.util.ArrayList;
import java.util.List;

import static com.lzy.okgo.utils.IOUtils.toByteArray;
import static com.lzy.okgo.utils.IOUtils.toObject;

/**
 * @author pj567
 * @date :2021/1/7
 * @description:
 */
public class RoomDataManger {
    public static void insertVodRecord(String apiUrl, VodInfo vodInfo) {
        VodRecord record = AppDataManager.get().getVodRecordDao().getVodRecord(apiUrl, vodInfo.id);
        if (record == null) {
            record = new VodRecord();
        }
        record.apiUrl = apiUrl;
        record.vodId = vodInfo.id;
        record.updateTime = System.currentTimeMillis();
        record.data = toByteArray(vodInfo);
        AppDataManager.get().getVodRecordDao().insert(record);
    }

    public static VodInfo getVodInfo(String apiUrl, int vodId) {
        VodRecord record = AppDataManager.get().getVodRecordDao().getVodRecord(apiUrl, vodId);
        if (record != null && record.data != null) {
            return (VodInfo) toObject(record.data);
        }
        return null;
    }

    public static void deleteVodRecord(String apiUrl, VodInfo vodInfo) {
        VodRecord record = AppDataManager.get().getVodRecordDao().getVodRecord(apiUrl, vodInfo.id);
        if (record != null) {
            AppDataManager.get().getVodRecordDao().delete(record);
        }
    }

    public static List<VodInfo> getAllVodRecord(String apiUrl) {
        List<VodRecord> recordList = AppDataManager.get().getVodRecordDao().getAll(apiUrl);
        List<VodInfo> vodInfoList = new ArrayList<>();
        if (recordList != null) {
            for (VodRecord record : recordList) {
                if (record.data != null) {
                    Object obj = IOUtils.toObject(record.data);
                    if (obj != null) {
                        vodInfoList.add((VodInfo) obj);
                    }
                }
            }
        }
        return vodInfoList;
    }
}