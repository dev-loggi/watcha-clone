package com.example.csr83.watchaproject.legacy

import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.csr83.watchaproject.R
import kotlinx.android.synthetic.main.fragment_transition_test2.*

class TransitionTestFragment2 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_transition_test2, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button.setOnClickListener {
            if (Build.VERSION.SDK_INT < 21)
                return@setOnClickListener

            with(activity as TransitionTestActivity) {
                changeFragment(
                    this@TransitionTestFragment2,
                    TransitionTestFragment1(),
//                    MovieDetailFragment.newInstance(0, 1 + 1,
//                        Movie("어메이징 스파이더맨2", "https://postfiles.pstatic.net/MjAxOTAyMTlfMTcw/MDAxNTUwNTYwOTk5NTIy.ecHQ21KnNHOVtrb5kNNOyleIW-XBF4BmAcmSFbhh4-wg.su3yHoL-We2R2JZS2O-VuPSBSvEcZsG5CRKIVNWUda8g.JPEG.shebfollowme/maxresdefault.jpg?type=w580", "https://dhgywazgeek0d.cloudfront.net/watcha/image/upload/c_fill,h_400,q_80,w_280/v1466074148/lvn09r4q9usuvaak6yxy.jpg", "2014")),
                    iv_movie_wide
                )
            }

        }
    }
}