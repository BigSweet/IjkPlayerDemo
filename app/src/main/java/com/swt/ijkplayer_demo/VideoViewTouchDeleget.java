package com.swt.ijkplayer_demo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 介绍：这里写介绍
 * 作者：sunwentao
 * 邮箱：wentao.sun@yintech.cn
 * 时间: 2020-03-24
 */

interface ShowOrHideListener {
    void showOrHid();
}

public class VideoViewTouchDeleget {

    Activity activity;
    ShowOrHideListener listener;
    private float mLastMotionX;
    private float mLastMotionY;
    private int startX;
    private int startY;
    protected boolean mChangeVolume = false;//是否改变音量
    protected Dialog mBrightnessDialog;
    protected float mBrightnessData = -1; //亮度
    protected TextView mBrightnessDialogTv;
    protected boolean mFirstTouch = false;//是否首次触摸
    protected boolean mBrightness = false;//是否改变亮度
    protected int mThreshold = 80; //手势偏差值
    protected int mSeekEndOffset = 0;
    protected int mScreenWidth = 0;
    protected int mScreenHeight = 0;
    protected int mGestureDownVolume; //手势调节音量的大小
    private AudioManager mAudioManager;
    private int threshold;

    protected Dialog mVolumeDialog;
    protected Drawable mVolumeProgressDrawable;
    protected ProgressBar mDialogVolumeProgressBar;

    private boolean isClick = true;

    void setListener(ShowOrHideListener listener) {
        this.listener = listener;
    }

    void init(Activity activity) {
        this.activity = activity;
        threshold = DisplayUtils.dip2px(18);
        mAudioManager = (AudioManager) activity.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        mScreenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = activity.getResources().getDisplayMetrics().heightPixels;
        mSeekEndOffset = DisplayUtils.dip2px(50); //手动滑动的起始偏移位置
    }


    void agent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                mLastMotionY = y;
                startX = (int) x;
                startY = (int) y;
                mFirstTouch = true;
                mChangeVolume = false;
                mBrightness = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = x - mLastMotionX;
                float deltaY = y - mLastMotionY;
                float absDeltaX = Math.abs(deltaX);
                float absDeltaY = Math.abs(deltaY);
                if (!mChangeVolume && !mBrightness) {
                    if (absDeltaX > mThreshold || absDeltaY > mThreshold) {
//                            cancelProgressTimer();
                        if (absDeltaX >= mThreshold) {
                        } else {
                            int screenHeight = DisplayUtils.getScreenHeight(activity);
                            boolean noEnd = Math.abs(screenHeight - mLastMotionY) > mSeekEndOffset;
                            if (mFirstTouch) {
                                mBrightness = (mLastMotionX < mScreenWidth * 0.5f) && noEnd;
                                mFirstTouch = false;
                            }
                            if (!mBrightness) {
                                mChangeVolume = noEnd;
                                mGestureDownVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                            }
//                                mShowVKey = !noEnd;
                        }
                    }
                }

                if (mChangeVolume) {
                    deltaY = -deltaY;
                    int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    int deltaV = (int) (max * deltaY * 3 / mScreenWidth);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mGestureDownVolume + deltaV, 0);
                    int volumePercent = (int) (mGestureDownVolume * 100 / max + deltaY * 3 * 100 / mScreenWidth);
                    Log.d("swt", "mGestureDownVolume" + mGestureDownVolume + "volumePercent" + volumePercent + "mScreenHeight" + mScreenWidth + "deltay" + deltaY);
                    showVolumeDialog(-deltaY, volumePercent);
                } else if (mBrightness) {
                    if (Math.abs(deltaY) > mThreshold) {
                        float percent = (-deltaY / mScreenHeight);
                        onBrightnessSlide(percent);
                        mLastMotionY = y;
                    }
                }
                break;
            case MotionEvent.ACTION_UP://触摸屏幕
                if (Math.abs(x - startX) > threshold
                        || Math.abs(y - startY) > threshold) {
                    isClick = false;
                }
                mLastMotionX = 0;
                mLastMotionY = 0;
                startX = (int) 0;
                if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && isClick) {
                    listener.showOrHid();
                }
                isClick = true;
                dismissVolumeDialog();
                dismissBrightnessDialog();
                break;

            default:
                break;
        }
    }


    private void onBrightnessSlide(float percent) {
        mBrightnessData = activity.getWindow().getAttributes().screenBrightness;
        if (mBrightnessData <= 0.00f) {
            mBrightnessData = 0.50f;
        } else if (mBrightnessData < 0.01f) {
            mBrightnessData = 0.01f;
        }
        WindowManager.LayoutParams lpa = activity.getWindow().getAttributes();
        lpa.screenBrightness = mBrightnessData + percent;
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }
        showBrightnessDialog(lpa.screenBrightness);
        activity.getWindow().setAttributes(lpa);
    }


    protected void showBrightnessDialog(float percent) {
        if (mBrightnessDialog == null) {
            View localView = LayoutInflater.from(activity).inflate(R.layout.video_brightness, null);
            mBrightnessDialogTv = localView.findViewById(R.id.app_video_brightness);
            mBrightnessDialog = new Dialog(activity, R.style.video_style_dialog_progress);
            mBrightnessDialog.setContentView(localView);
            mBrightnessDialog.getWindow().addFlags(8);
            mBrightnessDialog.getWindow().addFlags(32);
            mBrightnessDialog.getWindow().addFlags(16);
            mBrightnessDialog.getWindow().setLayout(-2, -2);
            WindowManager.LayoutParams localLayoutParams = mBrightnessDialog.getWindow().getAttributes();
            localLayoutParams.gravity = Gravity.RIGHT;
//            localLayoutParams.width = getWidth();
//            localLayoutParams.height = getHeight();
            int location[] = new int[2];
//            getLocationOnScreen(location);
            localLayoutParams.x = location[0];
            localLayoutParams.y = location[1];
            mBrightnessDialog.getWindow().setAttributes(localLayoutParams);
        }
        if (!mBrightnessDialog.isShowing()) {
            mBrightnessDialog.show();
        }
        if (mBrightnessDialogTv != null)
            mBrightnessDialogTv.setText((int) (percent * 100) + "%");
    }


    private void dismissVolumeDialog() {
        if (mVolumeDialog != null) {
            mVolumeDialog.dismiss();
            mVolumeDialog = null;
        }
    }

    protected void dismissBrightnessDialog() {
        if (mBrightnessDialog != null) {
            mBrightnessDialog.dismiss();
            mBrightnessDialog = null;
        }
    }

    private void showVolumeDialog(float v, int volumePercent) {
        if (mVolumeDialog == null) {
            View localView = LayoutInflater.from(activity).inflate(R.layout.video_volume_dialog, null);
            mDialogVolumeProgressBar = ((ProgressBar) localView.findViewById(R.id.volume_progressbar));
            if (mVolumeProgressDrawable != null) {
                mDialogVolumeProgressBar.setProgressDrawable(mVolumeProgressDrawable);
            }
            mVolumeDialog = new Dialog(activity, R.style.video_style_dialog_progress);
            mVolumeDialog.setContentView(localView);
            mVolumeDialog.getWindow().addFlags(8);
            mVolumeDialog.getWindow().addFlags(32);
            mVolumeDialog.getWindow().addFlags(16);
            mVolumeDialog.getWindow().setLayout(-2, -2);
            WindowManager.LayoutParams localLayoutParams = mVolumeDialog.getWindow().getAttributes();
            localLayoutParams.gravity = Gravity.LEFT;
            int location[] = new int[2];
            localLayoutParams.x = location[0];
            localLayoutParams.y = location[1];
            mVolumeDialog.getWindow().setAttributes(localLayoutParams);
        }
        if (!mVolumeDialog.isShowing()) {
            mVolumeDialog.show();
        }

        mDialogVolumeProgressBar.setProgress(volumePercent);
    }
}
