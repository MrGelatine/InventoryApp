/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.inventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.io.IOException

/**
 * Database class with a singleton Instance object.
 */
private const val DB_NAME = "item_database"
private const val DUMMY_PASSWORD = "password"
@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class InventoryDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var Instance: InventoryDatabase? = null

        fun getDatabase(context: Context,password: String = DUMMY_PASSWORD): InventoryDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                val dbFile = context.getDatabasePath(DB_NAME)
                val passphrase = "password".toByteArray()
                val state = SQLCipherUtils.getDatabaseState(context, dbFile)
                if(state == SQLCipherUtils.State.UNENCRYPTED){
                    val dbTemp = context.getDatabasePath("_temp")

                    dbTemp.delete()

                    SQLCipherUtils.encryptTo(context,dbFile,dbTemp,passphrase)

                    val dbBackup = context.getDatabasePath("_backup")

                    if(dbFile.renameTo(dbBackup)){
                        if(dbTemp.renameTo(dbFile)){
                            dbBackup.delete()
                        }else{
                            dbBackup.renameTo(dbFile)
                            throw IOException("Could not rename $dbTemp to $dbFile")
                        }
                    }else{
                        dbTemp.delete()
                        throw IOException("Could not rename $dbFile to $dbBackup")
                    }
                }

                return Room.databaseBuilder(context, InventoryDatabase::class.java, DB_NAME)
                    .openHelperFactory(SupportFactory(passphrase))
                    .build()

                val supportFactory = SupportFactory(SQLiteDatabase.getBytes(password.toCharArray()))
                Room.databaseBuilder(context, InventoryDatabase::class.java, "item_database")
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
                    .openHelperFactory(supportFactory)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
