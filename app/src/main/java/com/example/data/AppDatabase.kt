package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "trades")
data class TradeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val market: String,
    val type: String, // "BUY" or "SELL"
    val profit: Double,
    val deltaMs: Long,
    val timestamp: Long,
    val source: String
)

@Entity(tableName = "news_pipeline")
data class NewsPipelineEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val headline: String,
    val sentiment: String, // "BULLISH", "BEARISH", "NEUTRAL"
    val score: Double,
    val detectedBy: String,
    val timestamp: Long
)

@Dao
interface TradeDao {
    @Query("SELECT * FROM trades ORDER BY timestamp DESC LIMIT 150")
    fun getAllTrades(): Flow<List<TradeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrade(trade: TradeEntity)

    @Query("DELETE FROM trades")
    suspend fun clearAllTrades()

    @Query("SELECT SUM(profit) FROM trades")
    fun getTotalProfitFlow(): Flow<Double?>
}

@Dao
interface NewsPipelineDao {
    @Query("SELECT * FROM news_pipeline ORDER BY timestamp DESC LIMIT 100")
    fun getAllNews(): Flow<List<NewsPipelineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: NewsPipelineEntity)

    @Query("DELETE FROM news_pipeline")
    suspend fun clearAllNews()
}

@Database(entities = [TradeEntity::class, NewsPipelineEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tradeDao(): TradeDao
    abstract fun newsPipelineDao(): NewsPipelineDao
}

class HftRepository(private val db: AppDatabase) {
    val allTrades: Flow<List<TradeEntity>> = db.tradeDao().getAllTrades()
    val allNews: Flow<List<NewsPipelineEntity>> = db.newsPipelineDao().getAllNews()
    val totalProfitFlow: Flow<Double?> = db.tradeDao().getTotalProfitFlow()

    suspend fun insertTrade(trade: TradeEntity) {
        db.tradeDao().insertTrade(trade)
    }

    suspend fun insertNews(news: NewsPipelineEntity) {
        db.newsPipelineDao().insertNews(news)
    }

    suspend fun clearDatabase() {
        db.tradeDao().clearAllTrades()
        db.newsPipelineDao().clearAllNews()
    }
}
