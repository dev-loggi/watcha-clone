package com.example.csr83.watchaproject.model.movie

import android.content.Context
import com.example.csr83.watchaproject.data.remote.Movie

interface MovieSource {

    interface LoadDataCallBack {
        fun onLoadData(list: List<Movie>)
    }

    fun getData(title: String?, callback: LoadDataCallBack?)
}