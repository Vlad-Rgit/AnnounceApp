package com.announce.framework.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.announce.framework.database.dao.MessageDao
import com.announce.framework.database.models.MessageDatabase
import javax.inject.Singleton

@Singleton
@Database(entities = [MessageDatabase::class], version = 1, exportSchema = false)
abstract class AnnounceDatabase: RoomDatabase() {

    abstract val messageDao: MessageDao

    companion object {
        private lateinit var _instance: AnnounceDatabase

        fun getInstance(context: Context): AnnounceDatabase {
            return synchronized(this) {

                return if(::_instance.isInitialized) {
                    _instance
                }
                else {
                    _instance = Room.databaseBuilder(
                        context,
                        AnnounceDatabase::class.java,
                        "announceDB"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    _instance
                }
            }
        }
    }

}