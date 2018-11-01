package com.green.grodnonews.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface NewsFeedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecords(List<NewsFeedItem> entityList);

    @Query("delete from newsfeed where feedSourceId=:feedSourceID")
    void clearNews(int feedSourceID);


    @Query("select * from newsfeed where feedSourceId=:feedSourceID order by id DESC")
    LiveData<List<NewsFeedItem>> getNewsFeedLiveData(int feedSourceID);

}
