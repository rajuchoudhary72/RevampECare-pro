package com.app.ecarepro.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


val MIGRATION_4_5 = object : Migration(4, 5){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE users ADD COLUMN id INTEGER" )
    }
}

val MIGRATION_5_6 = object : Migration(5, 6){
    override fun migrate(database: SupportSQLiteDatabase) {
        //create new table
        database.execSQL("CREATE TABLE `new_users_table`(`id` INTEGER NOT NULL," +
                " `user_id` INTEGER NOT NULL," +
                " `name` TEXT," +
                " `photo` TEXT," +
                " `userType` INTEGER NOT NULL," +
                " `auth_token` TEXT," +
                " `roleName` TEXT," +
                " `schoolCode` TEXT," +
                " `mobileNumber` TEXT," +
                " `classID` TEXT," +
                " `loginTime` TEXT," +
                " `is_user_authenticated` INTEGER," +
                " `is_verified` INTEGER," +
                " PRIMARY KEY(`id`) )")

        //insert data from old table into new table
        database.execSQL("INSERT INTO new_users_table(user_id," +
                " name," +
                " photo," +
                " userType," +
                " auth_token," +
                " roleName," +
                " schoolCode," +
                " mobileNumber," +
                " classID," +
                " loginTime," +
                " is_user_authenticated," +
                " is_verified)" +
                " SELECT user_id, name, photo, userType, auth_token, roleName, schoolCode, mobileNumber, classID, loginTime, is_user_authenticated, is_verified FROM users")

        //drop old table
        database.execSQL("DROP TABLE users")

        //rename new table to the old table name
        database.execSQL("ALTER TABLE new_users_table RENAME TO users")

    }
}