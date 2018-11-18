package com.green.grodnonews.room;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface NewsDetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecords(List<NewsDetailItem> items);

    @Query("delete from newsDetail where articleUrl=:url")
    void deleteArticle(String url);

    @Query("delete from newsDetail")
    void deleteAllRecords();

//    @Query("select d.* from newsDetail d " +
//            " where d.articleUrl=:url ")
//    LiveData<List<NewsDetailItem>> getNewsDetail(String url);

    @Query("select d.* from newsDetail d " +
            " left join blackLst bl on (ifnull(bl.userName,'') = ifnull(d.authorName,'')) " +
            " where d.articleUrl=:url and (ifnull(bl.userName,'')='')")
    LiveData<List<NewsDetailItem>> getNewsDetail(String url);
}
