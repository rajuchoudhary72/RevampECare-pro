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

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Step 1: Create a new table without the 'slides' column
        database.execSQL(
            """
            CREATE TABLE schools_new (
                schoolCode TEXT PRIMARY KEY NOT NULL,
                active INTEGER,
                assessmentMarksURL TEXT,
                city TEXT,
                contactEmail TEXT,
                eCareProSch INTEGER,
                feePaymentURL TEXT,
                feeReportURL TEXT,
                isBoardingSchool INTEGER,
                logo TEXT,
                logoNScName TEXT,
                logoScName TEXT,
                marksEntryURL TEXT,
                schAdd1 TEXT,
                schAdd2 TEXT,
                schUpdatedOn TEXT,
                schoolName TEXT,
                state TEXT,
                supportDays TEXT,
                supportEmail TEXT,
                supportHours TEXT,
                supportPhone TEXT,
                themColor TEXT,
                webSite TEXT
            )
            """.trimIndent()
        )

        // Step 2: Copy data from old table to new table
        database.execSQL(
            """
            INSERT INTO schools_new (
                schoolCode, active, assessmentMarksURL, city, contactEmail, 
                eCareProSch, feePaymentURL, feeReportURL, isBoardingSchool, 
                logo, logoNScName, logoScName, marksEntryURL, schAdd1, 
                schAdd2, schUpdatedOn, schoolName, state, supportDays, 
                supportEmail, supportHours, supportPhone, themColor, webSite
            ) 
            SELECT 
                schoolCode, active, assessmentMarksURL, city, contactEmail, 
                eCareProSch, feePaymentURL, feeReportURL, isBoardingSchool, 
                logo, logoNScName, logoScName, marksEntryURL, schAdd1, 
                schAdd2, schUpdatedOn, schoolName, state, supportDays, 
                supportEmail, supportHours, supportPhone, themColor, webSite
            FROM schools
            """.trimIndent()
        )

        // Step 3: Remove the old table
        database.execSQL("DROP TABLE schools")

        // Step 4: Rename the new table to match the old table name
        database.execSQL("ALTER TABLE schools_new RENAME TO schools")
    }
}