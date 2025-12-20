package com.example.mtgcollectionmanager.di

import android.content.Context
import androidx.room.Room
import com.example.mtgcollectionmanager.data.local.database.MTGDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMTGDatabase(@ApplicationContext context: Context): MTGDatabase =
        Room.databaseBuilder(
            context,
            MTGDatabase::class.java,
            "mtg_database"
        )
            .addMigrations(
//migraciis shemtxvevashi sxva failshi vwert da aq viyenebt.
                object : androidx.room.migration.Migration(2, 3) {
                    override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                        database.execSQL("ALTER TABLE collections ADD COLUMN firestoreId TEXT NOT NULL DEFAULT ''")
                    }
                }
            )
            .fallbackToDestructiveMigration(false)
            .build()

    @Provides
    @Singleton
    fun provideCollectionDao(database: MTGDatabase) = database.collectionDao()

    @Provides
    @Singleton
    fun provideCategoryDao(database: MTGDatabase) = database.categoryDao()

    @Provides
    @Singleton
    fun provideCollectionCardDao(database: MTGDatabase) = database.collectionCardDao()
}
