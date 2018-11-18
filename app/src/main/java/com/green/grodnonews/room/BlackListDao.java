package com.green.grodnonews.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface BlackListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecords(List<BlackListItem> blackListItems);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecord(BlackListItem blackListItems);

    @Query("delete from blackLst where userName=:userName")
    void removeFromBlackList(String userName);

    @Query("delete from blackLst")
    void clearBlackList();

    @Query("select * from blackLst order by userName DESC")
    LiveData<List<BlackListItem>> getBlackList();
}
