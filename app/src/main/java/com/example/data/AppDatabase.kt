package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AppDao
import com.example.data.entity.ClientEntity
import com.example.data.entity.CurrencySettingEntity
import com.example.data.entity.ExpenseEntity
import com.example.data.entity.LanguageSettingEntity
import com.example.data.entity.PaymentReceiptEntity
import com.example.data.entity.ProductEntity
import com.example.data.entity.PurchaseInvoiceEntity
import com.example.data.entity.SaleInvoiceEntity
import com.example.data.entity.StockMovementEntity
import com.example.data.entity.SupplierEntity
import com.example.data.entity.SupplierPaymentEntity

@Database(
    entities = [
        ProductEntity::class,
        ClientEntity::class,
        SupplierEntity::class,
        SupplierPaymentEntity::class,
        SaleInvoiceEntity::class,
        PaymentReceiptEntity::class,
        PurchaseInvoiceEntity::class,
        StockMovementEntity::class,
        ExpenseEntity::class,
        CurrencySettingEntity::class,
        LanguageSettingEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val CREATE_CURRENCY_SETTINGS_SQL =
            "CREATE TABLE IF NOT EXISTS `currency_settings` (`id` INTEGER NOT NULL, `selectedCode` TEXT NOT NULL, `customSymbol` TEXT NOT NULL, `currencyNameAr` TEXT NOT NULL, `currencyNameEn` TEXT NOT NULL, `countryNameAr` TEXT NOT NULL, `symbolPlacement` TEXT NOT NULL, `decimalPlaces` INTEGER NOT NULL, `useGrouping` INTEGER NOT NULL, `updatedAtMillis` INTEGER NOT NULL, PRIMARY KEY(`id`))"

        private const val CREATE_LANGUAGE_SETTINGS_SQL =
            "CREATE TABLE IF NOT EXISTS `language_settings` (`id` INTEGER NOT NULL, `selectedCode` TEXT NOT NULL, `nameNative` TEXT NOT NULL, `nameAr` TEXT NOT NULL, `nameEn` TEXT NOT NULL, `isRtl` INTEGER NOT NULL, `flagEmoji` TEXT NOT NULL, `updatedAtMillis` INTEGER NOT NULL, PRIMARY KEY(`id`))"

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL(CREATE_LANGUAGE_SETTINGS_SQL)
                } catch (e: Throwable) {
                    android.util.Log.e("AppDatabase", "Error executing MIGRATION_7_8: ${e.message}", e)
                }
            }
        }

        val MIGRATION_6_8 = object : Migration(6, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL(CREATE_CURRENCY_SETTINGS_SQL)
                    db.execSQL(CREATE_LANGUAGE_SETTINGS_SQL)
                } catch (e: Throwable) {
                    android.util.Log.e("AppDatabase", "Error executing MIGRATION_6_8: ${e.message}", e)
                }
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL(CREATE_CURRENCY_SETTINGS_SQL)
                } catch (e: Throwable) {
                    android.util.Log.e("AppDatabase", "Error executing MIGRATION_6_7: ${e.message}", e)
                }
            }
        }

        val MIGRATION_5_7 = object : Migration(5, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL(CREATE_CURRENCY_SETTINGS_SQL)
                } catch (e: Throwable) {
                    android.util.Log.e("AppDatabase", "Error executing MIGRATION_5_7: ${e.message}", e)
                }
            }
        }

        val MIGRATION_4_7 = object : Migration(4, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL(CREATE_CURRENCY_SETTINGS_SQL)
                } catch (e: Throwable) {
                    android.util.Log.e("AppDatabase", "Error executing MIGRATION_4_7: ${e.message}", e)
                }
            }
        }

        val MIGRATION_3_7 = object : Migration(3, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL(CREATE_CURRENCY_SETTINGS_SQL)
                } catch (e: Throwable) {
                    android.util.Log.e("AppDatabase", "Error executing MIGRATION_3_7: ${e.message}", e)
                }
            }
        }

        val MIGRATION_2_7 = object : Migration(2, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL(CREATE_CURRENCY_SETTINGS_SQL)
                } catch (e: Throwable) {
                    android.util.Log.e("AppDatabase", "Error executing MIGRATION_2_7: ${e.message}", e)
                }
            }
        }

        val MIGRATION_1_7 = object : Migration(1, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL(CREATE_CURRENCY_SETTINGS_SQL)
                } catch (e: Throwable) {
                    android.util.Log.e("AppDatabase", "Error executing MIGRATION_1_7: ${e.message}", e)
                }
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mandoubak_database"
                )
                    .addMigrations(
                        MIGRATION_7_8,
                        MIGRATION_6_8,
                        MIGRATION_6_7,
                        MIGRATION_5_7,
                        MIGRATION_4_7,
                        MIGRATION_3_7,
                        MIGRATION_2_7,
                        MIGRATION_1_7
                    )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
