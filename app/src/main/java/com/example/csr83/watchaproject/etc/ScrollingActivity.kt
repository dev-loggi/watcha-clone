package com.example.csr83.watchaproject.etc

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.example.csr83.watchaproject.R

import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.android.synthetic.main.content_scrolling.*

class ScrollingActivity : AppCompatActivity() {

    val imageUrl = "https://postfiles.pstatic.net/MjAxOTAyMTlfMTcw/MDAxNTUwNTYwOTk5NTIy.ecHQ21KnNHOVtrb5kNNOyleIW-XBF4BmAcmSFbhh4-wg.su3yHoL-We2R2JZS2O-VuPSBSvEcZsG5CRKIVNWUda8g.JPEG.shebfollowme/maxresdefault.jpg?type=w580"
    val image2 = "https://i.ytimg.com/vi/J_yjeCD87uc/maxresdefault.jpg"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()

        }

        Glide.with(this)
            .load(image2)
            .error(R.drawable.watcha)
            .into(iv)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
