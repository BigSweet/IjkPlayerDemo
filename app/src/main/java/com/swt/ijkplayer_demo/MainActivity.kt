package com.swt.ijkplayer_demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var currentSeekPosition = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        player.setVideoPath("http://vfx.mtime.cn/Video/2019/03/21/mp4/190321153853126488.mp4")
//        player.setVideoPath("rtmp://58.200.131.2:1935/livetv/hunantv")
//        player.setVideoPath("rtmp://202.69.69.180:443/webcast/bshdlive-pc")
        player.start()
        val viewAgent = VideoViewTouchDeleget()
        viewAgent.init(this)
        viewAgent.setListener {
            showOrHide()
        }
        player.setOnTouchListener { p0, p1 ->
            viewAgent.agent(p1)
            true
        }

        player.setDurationListener { current, totalCurrent, seekBarCurrentPosition, seekBuff ->
            time_play_txt.text = current
            time_toal_txt.text = totalCurrent
            vudioSeekBar.progress = seekBarCurrentPosition.toInt()
            vudioSeekBar.secondaryProgress = (seekBuff.toInt())
        }
        vudioSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val duration = player.duration
                currentSeekPosition = (duration.toDouble() * p1.toDouble() * 1.0 / 100).toInt()
                val time = player.generateTime(currentSeekPosition.toLong())
                time_play_txt.text = time
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                player.seekTo(currentSeekPosition)
            }

        })
    }


    private fun showOrHide() {
        if (bottom_view.visibility == View.VISIBLE) {

            val animation1 = AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.option_leave_from_bottom
            )
            bottom_view.startAnimation(animation1)
            bottom_view.clearAnimation()
            bottom_view.visibility = View.GONE

            val decorView = window.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView.systemUiVisibility = uiOptions
        } else {
            bottom_view.visibility = View.VISIBLE
            bottom_view.clearAnimation()

            val animation1 = AnimationUtils.loadAnimation(this, R.anim.option_entry_from_bottom)
            bottom_view.startAnimation(animation1)
            mHandler.removeCallbacks(hideRunnable)
            mHandler.postDelayed(hideRunnable, 6000)
        }
    }

    private val hideRunnable = Runnable {
        if (bottom_view.visibility == View.VISIBLE) {
            showOrHide()
        }
    }

    @SuppressLint("HandlerLeak")
    private val mHandler = object : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
//                1 -> if (mVideo.getCurrentPosition() > 0) {
//                    mCurrentPosition = mVideo.getCurrentPosition()
//                    playTxt.setText(formatTime(mVideo.getCurrentPosition()))
//                    val progress = mVideo.getCurrentPosition() * 100 / mVideo.getDuration()
//                    //                        if (mVideo.isPlaying() && dialog.getVisibility() == View.VISIBLE) {
//                    //                            dialog.setVisibility(View.GONE);
//                    //                        }
//                    vudioSeekBar.progress = progress
//                    if (mVideo.getCurrentPosition() > mVideo.getDuration() - 100) {
//                        playTxt.setText("00:00")
//                        vudioSeekBar.progress = 0
//                    }
//                    vudioSeekBar.secondaryProgress = mVideo.getBufferPercentage()
//                } else {
//                    playTxt.setText("00:00")
//                    vudioSeekBar.progress = 0
//                }
                2 -> showOrHide()
                else -> {
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        player.stopPlayback()
        player.release(true)
        player.stopBackgroundPlay()
    }


}
