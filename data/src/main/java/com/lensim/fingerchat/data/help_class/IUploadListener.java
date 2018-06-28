package com.lensim.fingerchat.data.help_class;

/**
 * Created by LL130386 on 2017/12/21.
 */

public interface IUploadListener {

  void onSuccess(Object result);

  void onFailed();

  void onProgress(int progress);

}
