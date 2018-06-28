package com.lensim.fingerchat.fingerchat.ui.settings.clear_cache;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.view.MomeryView;
import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by LY309313 on 2016/11/15.
 */

public class CacheOptionFragment extends PreferenceFragment {


  // @InjectView(R.id.mCacheSize)
   MomeryView mCacheSize;
 //  @InjectView(R.id.mCacheClearBt)
    Button mCacheClearBt;
    // private File imageCache;
    private File cacheDirs;
    private File voiceDirs;
  //private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cache_option, container, false);
        //unbinder=  ButterKnife.bind(this, rootView);
        mCacheSize=(MomeryView)rootView.findViewById(R.id.mCacheSize);
        mCacheClearBt=(Button)rootView.findViewById(R.id.mCacheClearBt);
        ButterKnife.inject(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initListener();
    }

    private void initListener() {
        mCacheClearBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // showDialog();
               // startActivity(new Intent(getActivity(), CacheGuideActivity.class));
            }
        });

    }

//    private void showDialog() {
//        final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(getActivity());
//        builder.withTitle("提示")
//                .withIcon(R.drawable.icon_60)
//                .withMessage("确认删除缓存吗?")
//                .withButton1Text("取消")
//                .withButton2Text("删除")
//                .setButton1Click(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        builder.dismiss();
//                    }
//                })
//                .setButton2Click(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        builder.dismiss();
//                        File[] files = voiceDirs.listFiles();
//                        for (File file : files) {
//                            file.delete();
//                        }
//                        // FileUtil.deleteDirectory(voiceDirs.getPath());
//                        FileCache.getInstance().clear();
////                        ImageLoader.getInstance().clearMemoryCache();
////                        ImageLoader.getInstance().clearDiskCache();
//                        initData();
//                    }
//                }).show();
//    }

    private void initData() {
        //图片、视频、音频
        long totalSize;
        File images = FileUtil.getDiskCacheDirs(getActivity(), "f_images");
        File cacheVoice = FileUtil.getDiskCacheDirs(getActivity(), "cache_voice");
        File cacheVideo = FileUtil.getDiskCacheDirs(getActivity(), "cache_video");
        totalSize = FileUtil.getDirSize(images);
        totalSize += FileUtil.getDirSize(cacheVoice);
        totalSize += FileUtil.getDirSize(cacheVideo);

//        File sdcardFileDir = Environment.getExternalStorageDirectory();
//        String sdcardMemory = getMemoryInfo(sdcardFileDir,FileUtil.formatFileSize(totalSize));

        // 获得手机内部存储控件的状态
        File dataFileDir = Environment.getDataDirectory();
        getMemoryInfo(dataFileDir,totalSize);

       // L.i(/*"SD卡: " + sdcardMemory + */"\n手机内部: " + dataMemory);
//        imageCache = ImageLoader.getInstance().getDiskCache().getDirectory();
//        long imageSize = FileUtil.getDirSize(imageCache);
//        cacheDirs = FileUtil.getDiskCacheDirs(LensApplication.getInstance(), FileCache.CACHE_DIR);
//        long cacheSize = FileUtil.getDirSize(cacheDirs);
//        voiceDirs = FileUtil.getDiskCacheDirs(LensApplication.getInstance(), "voice");
//        if(!voiceDirs.exists()){
//            voiceDirs.mkdirs();
//        }
//        long voiceSize = FileUtil.getDirSize(voiceDirs);
//        String size = FileUtil.formatFileSize(cacheSize+voiceSize);
//        mCacheSize.setText(size);
    }


    /**
     * 根据路径获取内存状态
     *
     * @param path
     * @return
     */
    private void getMemoryInfo(File path,long size) {
        // 获得一个磁盘状态对象
        StatFs stat = new StatFs(path.getPath());

        long blockSize;   // 获得一个扇区的大小

        long totalBlocks;    // 获得扇区的总数

        long availableBlocks;   // 获得可用的扇区数量
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            totalBlocks = stat.getBlockCountLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();
            availableBlocks = stat.getAvailableBlocks();
        }
        // 总空间
       // String totalMemory = Formatter.formatFileSize(getActivity(), totalBlocks * blockSize);
        // 可用空间
       // String availableMemory = Formatter.formatFileSize(getActivity(), availableBlocks * blockSize);
        long[] data = {size,totalBlocks * blockSize - availableBlocks * blockSize - size,availableBlocks * blockSize};
        mCacheSize.setData(totalBlocks * blockSize,data);
        //return "总空间: " + totalMemory + "\n可用空间: " + availableMemory;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //unbinder.unbind();
    }
}
