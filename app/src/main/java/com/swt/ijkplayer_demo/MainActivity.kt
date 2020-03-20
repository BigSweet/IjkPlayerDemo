package com.swt.ijkplayer_demo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import tv.danmaku.ijk.media.player.IMediaPlayer
import java.io.IOException


class MainActivity : AppCompatActivity(), VideoListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        player.setPath("http://vfx.mtime.cn/Video/2019/03/21/mp4/190321153853126488.mp4")
        player.setPath("rtmp://58.200.131.2:1935/livetv/hunantv")
        player.setVideoListener(this)
        try {
            player.load()
        } catch (e: IOException) {
            Toast.makeText(this, "播放失败", Toast.LENGTH_SHORT)
            e.printStackTrace()
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    override fun onSeekComplete(p0: IMediaPlayer?) {

    }

    override fun onInfo(p0: IMediaPlayer?, p1: Int, p2: Int): Boolean {
        return false
    }

    override fun onVideoSizeChanged(p0: IMediaPlayer?, p1: Int, p2: Int, p3: Int, p4: Int) {
    }

    override fun onBufferingUpdate(p0: IMediaPlayer?, p1: Int) {
    }

    override fun onPrepared(p0: IMediaPlayer?) {
        player.start()
    }

    override fun onCompletion(p0: IMediaPlayer?) {
    }

    override fun onError(p0: IMediaPlayer?, p1: Int, p2: Int): Boolean {
        return false
    }
}
