package com.example.csr83.watchaproject.model.movie

import android.app.Activity
import android.content.Context
import android.os.Handler
import com.example.csr83.watchaproject.data.remote.MovieDB
import com.example.csr83.watchaproject.utils.Utils

class MovieRemoteDataSource(private val context: Context) : MovieSource {

    companion object {
        const val SERVICE_LATENCY_IN_MILLIS = 2000L
    }

    private val movieDao = MovieDB.getInstance(context).movieDAO()

    override fun getData(title: String?, callback: MovieSource.LoadDataCallBack?) {
        Handler().postDelayed({
            when (title) {
                null -> callback?.onLoadData(movieDao.getAllMovieData().shuffled())
                else -> callback?.onLoadData(movieDao.getMovieData(title))
            }
        }, SERVICE_LATENCY_IN_MILLIS)
    }
}