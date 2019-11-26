package com.example.csr83.watchaproject.model

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.csr83.watchaproject.data.rating.MovieRating
import com.example.csr83.watchaproject.data.remote.Movie

/**
 * Created by seongrok on 2017-04-13.
 */

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {

    companion object {
        // Logcat tag
        private const val TAG = "SQLiteHelper"

        // Database Name
        private const val DATABASE_NAME = "watcha"

        // Database Version
        private const val DATABASE_VERSION = 2

        // Table Names
        private const val TABLE_MOVIE_RATING = "TB_MOVIE_RATING"

        // Message Table - column names
        private const val COLUMN_NO = "no"
        private const val COLUMN_MOVIE_TITLE = "movie_title"
        private const val COLUMN_RATING = "rating"
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d(TAG, "onCreate()")

        // Create Table
        val query = StringBuilder()
        query.append("CREATE TABLE $TABLE_MOVIE_RATING (")
        query.append("$COLUMN_NO INTEGER PRIMARY KEY AUTOINCREMENT, ")
        query.append("$COLUMN_MOVIE_TITLE TEXT NOT NULL, ")
        query.append("$COLUMN_RATING FLOAT NOT NULL);")
        db.execSQL(query.toString())
    }

    @Synchronized
    override fun close() {
        super.close()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "onUpgrade(), DB:$databaseName, oldVersion:$oldVersion, newVersion:$newVersion")
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//        super.onDowngrade(db, oldVersion, newVersion);
        Log.d(TAG, "onDowngrade(), DB:$databaseName, oldVersion:$oldVersion, newVersion:$newVersion")
    }

    fun insertNewMovieRating(title: String, rating: Float) {
        val db = writableDatabase

        val query = "INSERT INTO $TABLE_MOVIE_RATING ($COLUMN_MOVIE_TITLE, $COLUMN_RATING) " +
                "VALUES ('$title', $rating);"

        try {
            db.execSQL(query)
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
    }

    fun deleteMovieRating(title: String) {
        val db = writableDatabase

        val query = "DELETE FROM $TABLE_MOVIE_RATING WHERE $COLUMN_MOVIE_TITLE = '$title'"

        try {
            db.execSQL(query)
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
    }

    fun selectMovieRating(title: String): Float? {
        val db = readableDatabase
        val query = "SELECT $COLUMN_RATING FROM $TABLE_MOVIE_RATING WHERE $COLUMN_MOVIE_TITLE = '$title'"

        var rating : Float? = null
        try {
            val cursor = db.rawQuery(query, null)

            while (cursor.moveToNext()) {
                rating = cursor.getFloat(0)
            }

            cursor.close()
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
        return rating
    }

    fun selectAllMovieRating(): ArrayList<MovieRating> {
        val db = readableDatabase
        val query = "SELECT $COLUMN_MOVIE_TITLE, $COLUMN_RATING FROM $TABLE_MOVIE_RATING"

        val listRatings = arrayListOf<MovieRating>()
        try {
            val cursor = db.rawQuery(query, null)

            while (cursor.moveToNext()) {
                listRatings.add(MovieRating(
                    0,
                    cursor.getString(0),
                    cursor.getFloat(1),
                    "",
                    "",
                    ""
                ))
            }

            cursor.close()
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
        return listRatings
    }

    fun updateMovieRating(title: String, rating: Float) {
        if (isExistRatingData(title)) {
            val db = writableDatabase

            val query = "UPDATE $TABLE_MOVIE_RATING " +
                    "SET $COLUMN_RATING = $rating " +
                    "WHERE $COLUMN_MOVIE_TITLE = '$title'"

            try {
                db.execSQL(query)
            } catch (e: SQLiteException) {
                e.printStackTrace()
            }
        } else {
            insertNewMovieRating(title, rating)
        }
    }

    fun isExistRatingData(title: String): Boolean {
        val rating = selectMovieRating(title)

        return (rating != null)
    }

    /**
     * data set
     */
    data class Rating(val title: String, val rating: Float)


}
