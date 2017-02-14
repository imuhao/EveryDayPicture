package com.imuhao.common.utils;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 单例
 * 录音
 *
 * @author 明明大美女
 */

public class AudioManager {

	private MediaRecorder mMediaRecorder;
	// 文件路径
	private String mDir;
	private String mCurrentFilePath;

	private boolean isPrepare;

	private static AudioManager mInstance;

	private AudioManager(String dir) {
		mDir = dir;
	}

	public interface AudioStateListener {
		void wellPrepared();
	}

	public AudioStateListener mListener;

	public void setOnAudioStateListener(AudioStateListener listener) {
		mListener = listener;
	}

	public static AudioManager getInstance(String dir) {
		if (mInstance == null) {
			synchronized (AudioManager.class) {
				if (mInstance == null) {
					mInstance = new AudioManager(dir);
				}
			}
		}
		return mInstance;
	}

	public void prepareAudio() {

		try {
			isPrepare = false;

			File dir = new File(mDir);
			if (!dir.exists())
				dir.mkdir();
			String fileName = generateFileName();
			File file = new File(dir, fileName);

			mCurrentFilePath = file.getAbsolutePath();
			mMediaRecorder = new MediaRecorder();
			// 设置输出文件
			mMediaRecorder.setOutputFile(file.getAbsolutePath());
			// 设置MediaRecorder的音频源为麦克风
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			// 设置音频格式
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
			// 设置音频的编码为amr
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mMediaRecorder.prepare();
			mMediaRecorder.start();
			// 准备结束
			isPrepare = true;
			if (mListener != null) {
				mListener.wellPrepared();
			}

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String generateFileName() {
		return UUID.randomUUID().toString() + ".amr";
	}

	public int getVoiceLevel(int maxLevel) {
		if (isPrepare) {
			try {
				// mMediaRecorder.getMaxAmplitude() 1-32767
				return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return 1;
	}

	public void release() {
		mMediaRecorder.stop();
		mMediaRecorder.release();
		mMediaRecorder = null;
	}

	public void cancel() {
		//		mMediaRecorder.release();
		release();
		if (mCurrentFilePath != null) {
			File file = new File(mCurrentFilePath);
			file.delete();
			mCurrentFilePath = null;
		}
	}

	public String getCurrentFilePath() {
		return mCurrentFilePath;
	}
}
