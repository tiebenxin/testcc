package com.lens.chatmodel.cache;


import android.text.TextUtils;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.ChatEnum.EPlayType;
import com.lens.chatmodel.bean.body.VideoUploadEntity;
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.helper.FileCache;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lensim.fingerchat.commons.global.CommonEnum.EProgressType;
import com.lensim.fingerchat.commons.global.CommonEnum.EUploadFileType;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.ThreadUtils;
import com.lensim.fingerchat.data.HttpChannel;
import com.lensim.fingerchat.data.help_class.IProgressListener;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;


public class FileManager {

    private static final String TAG = "FileManager";

    public static final int SHOT_SCREEN = 1;
    public static final int EX = 2;

    private Set<String> startedDownloads;
    private Set<String> startedUploads;

    private final static FileManager instance;

    private boolean useOrigin;

    static {
        instance = new FileManager();

    }

    private String uploadResult;
    private static String downloadUrl;

    public static FileManager getInstance() {
        return instance;
    }

    public FileManager() {
        this.startedDownloads = new ConcurrentSkipListSet<>();
        this.startedUploads = new ConcurrentSkipListSet<>();
    }

    private static boolean treatAsDownloadable(String message) {
        boolean download;

        download = (message.toLowerCase().startsWith("hnlensimage") || message.toLowerCase()
            .startsWith("/hnlensimage"));
        return download;
    }

    /**
     * 设置是否发送原图
     */
    public void setUseOrigin(boolean useOrigin) {
        this.useOrigin = useOrigin;
    }

    public boolean isUseOrigin() {
        return useOrigin;
    }

    /*
    * 聊天模块语音视频资源下载
    * */
    public void downloadFile(final IChatRoomModel model, final IProgressListener listener) {
        if (model.getMsgType() == EMessageType.VOICE) {
            downloadUrl = model.getContent();
            L.d("音频下载路径", downloadUrl);
        } else if (model.getMsgType() == EMessageType.VIDEO) {
            String json = model.getContent();
            VideoUploadEntity entity = VideoUploadEntity.fromJson(json);
            if (entity == null) {
                return;
            }
            downloadUrl = entity.getVideoUrl();
        } else {
            downloadUrl = "";
        }
        if (startedDownloads.contains(downloadUrl)) {
            L.i(FileManager.class.getSimpleName(),
                "Downloading of file " + downloadUrl + " already started");
            return;
        }
        startedDownloads.add(downloadUrl);

        HttpChannel.retrofitGetBytes(downloadUrl, new IProgressListener() {

            @Override
            public void onSuccess(byte[] bytes) {
                saveFile(bytes, model, listener);

            }

            @Override
            public void progress(int progress) {
                listener.progress(progress);
            }

            @Override
            public void onFailed() {
                listener.onFailed();

            }
        });
    }

    /*
    * 非聊天模块中，语音视频下载
    * */
    public void downloadFile(final String url, final EUploadFileType type,
        final IProgressListener listener) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        downloadUrl = url;
        if (startedDownloads.contains(downloadUrl)) {
            L.i(FileManager.class.getSimpleName(),
                "Downloading of file " + downloadUrl + " already started");
            return;
        }
        startedDownloads.add(downloadUrl);

        HttpChannel.retrofitGetBytes(downloadUrl, new IProgressListener() {

            @Override
            public void onSuccess(byte[] bytes) {
                saveDownloadedFile(bytes, url, type, listener);

            }

            @Override
            public void progress(int progress) {
                listener.progress(progress);
            }

            @Override
            public void onFailed() {
                listener.onFailed();

            }
        });
    }

    /*
    *密聊语音视频下载
    * */
    public void downloadFileSecret(final String url, final EUploadFileType type,
        final IProgressListener listener) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        downloadUrl = url;
        if (startedDownloads.contains(downloadUrl)) {
            L.i(FileManager.class.getSimpleName(),
                "Downloading of file " + downloadUrl + " already started");
            return;
        }
        startedDownloads.add(downloadUrl);
        HttpChannel.retrofitGetBytes(downloadUrl, listener);
    }

    /**
     * 聊天模块，保存文件
     */
    public void saveFile(final byte[] responseBody, final IChatRoomModel model,
        final IProgressListener progressListener) {

        ThreadUtils.runInBackground(new Runnable() {
            @Override
            public void run() {
                if (model.getMsgType() == EMessageType.VOICE) {
                    boolean Loaded = false;
                    try {
                        L.i("缓存存储的路径:" + model.getContent());
                        String json = model.getContent();

                        Loaded = FileCache.getInstance()
                            .saveVoice(json, responseBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (Loaded) {
                        ProviderChat.updatePlayStatus(ContextHelper.getContext(), model.getMsgId(),
                            EPlayType.NOT_PALYED);
                        notifyAction(progressListener, EProgressType.SUCCESS, 100);

                    } else {
                        notifyAction(progressListener, EProgressType.FAILED, 0);
                    }
                } else if (model.getMsgType() == EMessageType.VIDEO) {
                    boolean Loaded = false;
                    try {
                        String json = model.getContent();
                        VideoUploadEntity entity = VideoUploadEntity.fromJson(json);
                        if (entity == null) {
                            return;
                        }
                        Loaded = FileCache.getInstance()
                            .saveVideo(entity.getVideoUrl(), responseBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (Loaded) {
                        ProviderChat.updatePlayStatus(ContextHelper.getContext(), model.getMsgId(),
                            EPlayType.NOT_PALYED);
                        notifyAction(progressListener, EProgressType.SUCCESS, 100);
                    } else {
                        notifyAction(progressListener, EProgressType.FAILED, 0);

                    }
                }
            }
        });
    }

    /**
     * 非聊天模块，保存下载语音或视频文件
     */
    public void saveDownloadedFile(final byte[] responseBody, final String originUrl,
        final EUploadFileType type,
        final IProgressListener progressListener) {

        ThreadUtils.runInBackground(new Runnable() {
            @Override
            public void run() {
                if (type == EUploadFileType.VOICE) {
                    boolean Loaded = false;
                    try {
                        Loaded = FileCache.getInstance()
                            .saveVoice(originUrl, responseBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (Loaded) {
                        notifyAction(progressListener, EProgressType.SUCCESS, 100);
                    } else {
                        notifyAction(progressListener, EProgressType.FAILED, 0);
                    }
                } else if (type == EUploadFileType.VIDEO) {
                    boolean Loaded = false;
                    try {

                        Loaded = FileCache.getInstance()
                            .saveVideo(originUrl, responseBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (Loaded) {
                        notifyAction(progressListener, EProgressType.SUCCESS, 100);
                    } else {
                        notifyAction(progressListener, EProgressType.FAILED, 0);

                    }
                }
            }
        });
    }

    private void notifyAction(final IProgressListener progressListener, final EProgressType type,
        final int progress) {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressListener != null) {
                    switch (type) {
                        case SUCCESS:
                            progressListener.onSuccess(null);
                            break;
                        case FAILED:
                            progressListener.onFailed();
                            break;
                        case PROGRES:
                            progressListener.progress(progress);
                            break;

                    }
                }
            }
        });
    }


    public static boolean checkVideoHasEncrypt(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (path.contains(".mp4")) {
                String jpg = path.replace(".mp4", ".jpg");
                File file = new File(jpg);
                if (file.exists()) {
                    return true;
                }
            } else if (path.contains(".0")) {
                return true;
            } else {
                File file = FileCache.getInstance().getVideo(path);
                if (file.exists()) {
                    return true;
                }
            }
        }
        return false;
    }

}
