package com.xinzy.component.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.xinzy.component.R
import kotlinx.android.synthetic.main.activity_notice_board.*

class NoticeBoardActivity : AppCompatActivity() {
    private val TEXTS = arrayOf("静夜思", "床前明月光", "疑是地上霜", "举头望明月", "低头思故乡", "本节讲解TranslateAnimation动画,TranslateAnimation比较常用,比如QQ,网易新闻菜单条的动画,就可以用TranslateAnimation实现,本文将详细介绍通过")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_board)

        noticeBoard.setTexts(TEXTS.asList())

    }
}
