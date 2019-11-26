package com.example.csr83.watchaproject.model.movie

import android.content.Context
import com.example.csr83.watchaproject.data.remote.Movie

class MovieRepository(context: Context) : MovieSource {

    private val movieRemoteDataSource = MovieRemoteDataSource(context)

    override fun getData(title: String?, callback: MovieSource.LoadDataCallBack?) {
        movieRemoteDataSource.getData(title, object : MovieSource.LoadDataCallBack {
            override fun onLoadData(list: List<Movie>) {
                callback?.onLoadData(list)
            }
        })
    }
}