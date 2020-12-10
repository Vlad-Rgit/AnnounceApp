package com.announce.framework.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.announce.framework.database.models.MessageDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Query("Select * from message")
    fun getAllFlow(): Flow<List<MessageDatabase>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: Iterable<MessageDatabase>)

    @Query("Select count(idMessage) from message")
    suspend fun count(): Int

    @Query("""Delete from message where idMessage not in (
        Select idMessage from message order by timestamp asc limit :amount)""")
    suspend fun deleteOldest(amount: Int)
}