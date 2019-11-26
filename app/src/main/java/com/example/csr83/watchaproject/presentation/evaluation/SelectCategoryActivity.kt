package com.example.csr83.watchaproject.presentation.evaluation

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.utils.Utils
import kotlinx.android.synthetic.main.activity_select_category.*




class SelectCategoryActivity : Activity() {
    companion object {
        const val INTENT_KEY_CATEGORY = "category"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setNoTranslucentStatusBar(window, R.color.gray9)
        setContentView(R.layout.activity_select_category)

        val selectedCategory = intent!!.extras!!.getString(INTENT_KEY_CATEGORY)
        recycler_view.adapter = RvAdapter(selectedCategory!!)

        btnClose.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    inner class RvAdapter(private val selectedCategory: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val HEADER_OR_FOOTER = 0
        private val ITEM = 1

        private val listCategory = arrayListOf<String>()

        init {
            listCategory.add("랜덤 영화")
            listCategory.add("역대 100만 관객 돌파 영화")
            listCategory.add("왓챠 평균별점 TOP 영화")
            listCategory.add("전세계 흥행 Top 영화")
            listCategory.add("국내 누적 관객수 TOP 영화")
            listCategory.add("전문가 고평점 영화")
            listCategory.add("저예산 독립 영화")
            listCategory.add("고전 영화")
            listCategory.add("느와르 영화")
            listCategory.add("슈퍼 히어로 영화")
            listCategory.add("스포츠 영화")
            listCategory.add("범죄, 드라마, 코미디 영화")
            listCategory.add("로멘스/멜로 영화")
            listCategory.add("스릴러 영화")
            listCategory.add("로맨틱코미디 영화")
            listCategory.add("전쟁/가족/판타지 영화")
            listCategory.add("마블 영화")
            listCategory.add("판타지/액션/SF 영화")
            listCategory.add("블록버스터 영화")
            for (index in listCategory.indices) {
                if (listCategory[index] == selectedCategory) {
                    recycler_view.scrollToPosition(index + 2)
                }
            }
            Log.d("ViewHolder", "listCategory.size=${listCategory.size}")
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder = ViewHolder()
        override fun getItemViewType(position: Int): Int = when (position) {
            0, 1, (listCategory.size + 2), (listCategory.size + 3) -> HEADER_OR_FOOTER
            else -> ITEM
        }

        override fun getItemCount(): Int = (listCategory.size + 4)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            Log.d("ViewHolder", "position=$position")
//            val item = holder.itemView as TextView

            when (getItemViewType(position)) {
                HEADER_OR_FOOTER -> {
                    Log.d("ViewHolder", "HEADER_OR_FOOTER")
                }
                ITEM -> {
                    Log.d("ViewHolder", "ITEM")
                    with(holder.itemView as TextView) {
                        val category = listCategory[position - 2]
                        text = category

                        if (category == selectedCategory) {
                            setTextSize(COMPLEX_UNIT_SP, 24f)
                            setTypeface(null, Typeface.BOLD)
                            setTextColor(resources.getColor(R.color.white))
                        }
                        setOnClickListener {
                            setResult(RESULT_OK, Intent().putExtra(INTENT_KEY_CATEGORY, text.toString()))
                            finish()
                        }
                    }
                }
            }
        }

        inner class ViewHolder : RecyclerView.ViewHolder(
            TextView(this@SelectCategoryActivity).apply {
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                    .apply { setMargins(0, Utils.convertDpToPx(16, context), 0, Utils.convertDpToPx(16, context)) }

                gravity = Gravity.CENTER
                setTextSize(COMPLEX_UNIT_SP, 17f)
                setTextColor(context.resources.getColor(R.color.gray0_1))
            })
    }
}
