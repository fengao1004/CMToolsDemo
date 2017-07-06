package com.dayang.common;

/**
 * 进度监听器的接口
 * @author 任育伟
 *
 */
public interface UploadProgressListener {
	public void onUploadProgress(String currentStep, long uploadSize,
			long filesLength);
}
