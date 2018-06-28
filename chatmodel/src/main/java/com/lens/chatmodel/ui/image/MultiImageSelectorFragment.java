package com.lens.chatmodel.ui.image;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.ListPopupWindow;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import com.lens.chatmodel.R;
import com.lens.chatmodel.base.ChatEnvironment;
import com.lensim.fingerchat.commons.app.AppManager;
import com.lensim.fingerchat.commons.permission.EPermission;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.data.bean.FolderBean;
import com.lensim.fingerchat.data.bean.ImageBean;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 图片选择Fragment
 * Created by Nereo on 2015/4/7.
 */
public class MultiImageSelectorFragment extends Fragment {

    public static final String TAG = "me.nereo.multi_image_selector.MultiImageSelectorFragment";

    private static final String KEY_TEMP_FILE = "key_temp_file";

    /**
     * 最大图片选择次数，int类型
     */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /**
     * 图片选择模式，int类型
     */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /**
     * 是否显示相机，boolean类型
     */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /**
     * 默认选择的数据集
     */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_result";
    /**
     * 所有数据
     */
    public static final String ALL_DATA_LIST = "default_result";
    /**
     * 选择的数据集
     */
    public static final String SELECTED_LIST = "selected_list";
    /**
     * 数据索引
     */
    public static final String SELECTED_INDEX = "selected_index";
    /**
     * 单选
     */
    public static final int MODE_SINGLE = 0;
    /**
     * 多选
     */
    public static final int MODE_MULTI = 1;
    // 不同loader定义
    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;
    private static final int LOADER_VIDEO = 2;
    // 请求加载系统照相机
    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_PREVIEW = 101;


    // 结果数据
    private List<ImageBean> resultList = new ArrayList<>();
    // 文件夹数据
    private ArrayList<FolderBean> mResultFolder = new ArrayList<>();
    private ArrayList<ImageBean> mMediaList = new ArrayList<>();
    private ArrayList<String> mPathList = new ArrayList<>();


    // 图片Grid
    private GridView mGridView;
    private Callback mCallback;

    private ImageGridAdapter mImageAdapter;
    private FolderAdapter mFolderAdapter;

    private ListPopupWindow mFolderPopupWindow;

    // 类别
    private TextView mCategoryText;
    // 预览按钮
    private Button mPreviewBtn;
    // 底部View
    private View mPopupAnchorView;

    private int mDesireImageCount;

    private boolean hasFolderGened = false;
    private boolean mIsShowCamera = false;

    private ImageBean mTmpImageBean;
    private File mTmpFile;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (Callback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                "The Activity must implement MultiImageSelectorFragment.Callback interface...");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_multi_image, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 选择图片数量
        mDesireImageCount = getArguments().getInt(EXTRA_SELECT_COUNT);

        // 图片选择模式
        final int mode = getArguments().getInt(EXTRA_SELECT_MODE);

        // 默认选择
        if (mode == MODE_MULTI) {
            List<ImageBean> tmp = getArguments()
                .getParcelableArrayList(EXTRA_DEFAULT_SELECTED_LIST);
            if (tmp != null && tmp.size() > 0) {
                resultList = tmp;
            }
        }

        // 是否显示照相机
        mIsShowCamera = getArguments().getBoolean(EXTRA_SHOW_CAMERA, true);
        mImageAdapter = new ImageGridAdapter(getActivity(), mIsShowCamera, 3);
        // 是否显示选择指示器
        mImageAdapter.showSelectIndicator(mode == MODE_MULTI);

        mPopupAnchorView = view.findViewById(R.id.footer);

        mCategoryText = (TextView) view.findViewById(R.id.category_btn);
        // 初始化，加载所有图片
        mCategoryText.setText(R.string.folder_all);
        mCategoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mFolderPopupWindow == null) {
                    createPopupFolderList();
                }

                if (mFolderPopupWindow.isShowing()) {
                    mFolderPopupWindow.dismiss();
                } else {
                    mFolderPopupWindow.show();
                    int index = mFolderAdapter.getSelectIndex();
                    index = index == 0 ? index : index - 1;
                    mFolderPopupWindow.getListView().setSelection(index);
                }
            }
        });

        mPreviewBtn = (Button) view.findViewById(R.id.preview);
        // 初始化，按钮状态初始化
        if (resultList == null || resultList.size() <= 0) {
            mPreviewBtn.setText(R.string.preview);
            mPreviewBtn.setEnabled(false);
        }
        mPreviewBtn.setOnClickListener(view1 -> {
            // TODO 预览
            // 多选模式
            Intent intent = new Intent(getActivity(), PhotoPreviewActivity.class);
            // 将所有的传过去，并将当前的索引传过去
            List<ImageBean> images = ((ImageGridAdapter) mGridView.getAdapter())
                .getmSelectedImages();
            ImageBean image = (ImageBean) mGridView.getAdapter().getItem(0);
            List<ImageBean> allImages = ((ImageGridAdapter) mGridView.getAdapter())
                .getmImages();
            intent.putExtra(SELECTED_LIST, new ArrayList<>(images));
            intent.putExtra(SELECTED_INDEX, allImages.indexOf(image));
            startActivityForResult(intent, REQUEST_PREVIEW);

        });

        mGridView = (GridView) view.findViewById(R.id.grid);
        mGridView.setAdapter(mImageAdapter);

        mImageAdapter.setOnIndicatorClickListener(new ImageGridAdapter.OnIndicatorClickListener() {
            @Override
            public void onClick(int i, ImageBean image) {
                if (mImageAdapter.isShowCamera()) {
                    // 如果显示照相机，则第一个Grid显示为照相机，处理特殊逻辑
                    if (i == 0) {
                        if (AppManager.getInstance().checkCamara(getActivity())) {
                            showCameraAction();
                        }
                    } else {
                        // 正常操作
                        selectImageByIndicator(image, mode);
                    }
                } else {
                    // 正常操作
                    selectImageByIndicator(image, mode);
                }
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mImageAdapter.isShowCamera()) {
                    // 如果显示照相机，则第一个Grid显示为照相机，处理特殊逻辑
                    if (i != 0) {
                        if (mode == MODE_MULTI) {
                            // 多选模式
                            Intent intent = new Intent(getActivity(), PhotoPreviewActivity.class);
                            // 将所有的传过去，并将当前的索引传过去
                            List<ImageBean> images = ((ImageGridAdapter) adapterView.getAdapter())
                                .getmSelectedImages();
                            ImageBean image = (ImageBean) adapterView.getAdapter().getItem(i);
                            List<ImageBean> allImages = ((ImageGridAdapter) adapterView
                                .getAdapter()).getmImages();
                            intent.putExtra(SELECTED_LIST, new ArrayList<>(images));
                            intent.putExtra(SELECTED_INDEX, allImages.indexOf(image));
                            intent.putExtra(ALL_DATA_LIST, new ArrayList<>(allImages));
                            startActivityForResult(intent, REQUEST_PREVIEW);
                        } else if (mode == MODE_SINGLE) {
                            // 单选模式
                            if (mCallback != null) {
                                ImageBean image = (ImageBean) adapterView.getAdapter().getItem(i);
                                mCallback.onSingleImageSelected(image);
                            }
                        }
                    } else if (AppManager.getInstance().checkCamara(getActivity())) {
                        showCameraAction();
                    }
                } else {
                    if (mode == MODE_MULTI) {
                        // 多选模式
                        Intent intent = new Intent(getActivity(), PhotoPreviewActivity.class);
                        // 将所有的传过去，并将当前的索引传过去
                        List<ImageBean> images = ((ImageGridAdapter) adapterView.getAdapter())
                            .getmSelectedImages();
                        ImageBean image = (ImageBean) adapterView.getAdapter().getItem(i);
                        List<ImageBean> allImages = ((ImageGridAdapter) adapterView.getAdapter())
                            .getmImages();
                        intent.putExtra(SELECTED_LIST, new ArrayList<>(images));
                        intent.putExtra(SELECTED_INDEX, allImages.indexOf(image));
                        intent.putExtra(ALL_DATA_LIST, new ArrayList<>(allImages));
                        startActivityForResult(intent, REQUEST_PREVIEW);
                    } else if (mode == MODE_SINGLE) {
                        // 单选模式
                        if (mCallback != null) {
                            ImageBean image = (ImageBean) adapterView.getAdapter().getItem(i);
                            mCallback.onSingleImageSelected(image);
                        }
                    }
                }
            }
        });
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                int totalItemCount) {

            }
        });

        mFolderAdapter = new FolderAdapter(getActivity());
    }

    /**
     * 选择相机
     */
    private void showCameraAction() {
        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            mTmpFile = FileUtil.createTmpFile(getActivity());
            if (mTmpFile != null && mTmpFile.exists()) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
                startActivityForResult(cameraIntent, REQUEST_CAMERA);
            } else {
                Toast.makeText(getActivity(), "图片错误", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 创建弹出的ListView
     */
    private void createPopupFolderList() {
        Point point = TDevice.getScreenSize(getActivity());
        int width = point.x;
        int height = (int) (point.y * (4.5f / 8.0f));
        mFolderPopupWindow = new ListPopupWindow(getActivity());
        mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mFolderPopupWindow.setAdapter(mFolderAdapter);
        mFolderPopupWindow.setContentWidth(width);
        mFolderPopupWindow.setWidth(width);
        mFolderPopupWindow.setHeight(height);
        mFolderPopupWindow.setAnchorView(mPopupAnchorView);
        mFolderPopupWindow.setModal(true);
        mFolderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                mFolderAdapter.setSelectIndex(i);

                final int index = i;
                final AdapterView v = adapterView;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFolderPopupWindow.dismiss();

                        if (index == 0) {

                            getActivity().getSupportLoaderManager()
                                .restartLoader(LOADER_ALL, null, mLoaderCallback);
                            mCategoryText.setText(R.string.folder_all);
                            if (mIsShowCamera) {
                                mImageAdapter.setShowCamera(true);
                            } else {
                                mImageAdapter.setShowCamera(false);
                            }
                        } else {
                            FolderBean folder = (FolderBean) v.getAdapter().getItem(index);
                            if (null != folder) {
                                mImageAdapter.setData(folder.images);
                                mCategoryText.setText(folder.name);
                                // 设定默认选择
                                if (resultList != null && resultList.size() > 0) {
                                    mImageAdapter.setDefaultSelected(resultList);
                                }
                            }
                            mImageAdapter.setShowCamera(false);
                        }

                        // 滑动到最初始位置
                        mGridView.smoothScrollToPosition(0);
                    }
                }, 100);

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_TEMP_FILE, mTmpImageBean);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mTmpImageBean = savedInstanceState.getParcelable(KEY_TEMP_FILE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 首次加载所有图片
        //new LoadImageTask().execute();
        ChatEnvironment.getInstance().getPermissionExecutor()
            .checkPermission(getActivity(), EPermission.STORAGE,
                (permission, isGranted, withAsk) -> {
                    if (permission == EPermission.STORAGE && isGranted) {
                        getActivity().getSupportLoaderManager()
                            .initLoader(LOADER_ALL, null, mLoaderCallback);
//                        getActivity().getSupportLoaderManager()
//                            .initLoader(LOADER_CATEGORY, null, mLoaderCallback);
                        //TODO:暂时不支持视频
//                        getActivity().getSupportLoaderManager()
//                            .initLoader(LOADER_VIDEO, null, mLoaderCallback);
                    }

                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 相机拍照完成后，返回图片路径
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                if (mTmpImageBean != null) {
                    if (mCallback != null) {
                        mCallback.onCameraShot(mTmpImageBean);
                    }
                }
            } else {
                File file = new File(mTmpImageBean.path);
                while (file != null && file.exists()) {
                    boolean success = file.delete();
                    if (success) {
                        mTmpImageBean = null;
                    }
                }
            }
        } else if (requestCode == REQUEST_PREVIEW) {
            if (resultCode == RESULT_OK) {
                //重新取结果刷新
                List<ImageBean> datas = data.getParcelableArrayListExtra("need_to_send");
                if (!datas.isEmpty()) {
                    resultList = datas;
                    mImageAdapter.setDefaultSelected(resultList);
                }

            } else if (resultCode == -2) {
                //用户选择了发送，直接返回结果就好
                ArrayList<ImageBean> datas = data.getParcelableArrayListExtra("need_to_send");
                boolean useOrigin = data.getBooleanExtra("need_origin", false);
                if (datas.size() > 0) {
                    // 返回已选择的图片数据
                    Intent result = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(MultiImageSelectorActivity.EXTRA_RESULT, datas);
                    result.putExtras(bundle);
                    result.putExtra(MultiImageSelectorActivity.EXTRA_RESULT_ORIGIN, useOrigin);
                    getActivity().setResult(RESULT_OK, result);
                    getActivity().finish();
                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mFolderPopupWindow != null) {
            if (mFolderPopupWindow.isShowing()) {
                mFolderPopupWindow.dismiss();
            }
        }
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 选择图片操作———图片右上角checkbox
     */
    private void selectImageByIndicator(ImageBean image, int mode) {
        if (image != null) {
            // 多选模式
            if (mode == MODE_MULTI) {
                if (resultList.contains(image)) {
                    resultList.remove(image);
                    if (resultList.size() != 0) {
                        mPreviewBtn.setEnabled(true);
                        mPreviewBtn.setText(
                            getResources().getString(R.string.preview) + "(" + resultList.size()
                                + ")");
                    } else {
                        mPreviewBtn.setEnabled(false);
                        mPreviewBtn.setText(R.string.preview);
                    }
                    if (mCallback != null) {
                        mCallback.onImageUnselected(image);
                    }
                } else {
                    // 判断选择数量问题
                    if (mDesireImageCount <= resultList.size()) {
                        Toast.makeText(getActivity(), R.string.msg_amount_limit, Toast.LENGTH_SHORT)
                            .show();
                        return;
                    }

                    resultList.add(image);
                    mPreviewBtn.setEnabled(true);
                    mPreviewBtn
                        .setText(
                            getResources().getString(R.string.preview) + "(" + resultList.size()
                                + ")");
                    if (mCallback != null) {
                        mCallback.onImageSelected(image);
                    }
                }
                mImageAdapter.select(image);
            } else if (mode == MODE_SINGLE) {
                // 单选模式
                if (mCallback != null) {
                    mCallback.onSingleImageSelected(image);
                }
            }
        }
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media._ID};

        private final String[] VIDEO_PROJECTION = {
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media._ID};

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id == LOADER_ALL) {
                CursorLoader cursorLoader = new CursorLoader(getActivity(),
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                    IMAGE_PROJECTION[4] + ">0 AND " + IMAGE_PROJECTION[3] + "=? OR "
                        + IMAGE_PROJECTION[3]
                        + "=? OR " + IMAGE_PROJECTION[3] + "=? /*or*/ " /*+IMAGE_PROJECTION[3] + "=? "*/,
                    new String[]{"image/jpeg", "image/png", "image/gif"},
                    IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            } else if (id == LOADER_CATEGORY) {
                CursorLoader cursorLoader = new CursorLoader(getActivity(),
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                    IMAGE_PROJECTION[4] + ">0 AND " + IMAGE_PROJECTION[0] + " like '%" + args
                        .getString("path") + "%'",
                    null, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            } else if (id == LOADER_VIDEO) {
                CursorLoader cursorLoader = new CursorLoader(getActivity(),
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_PROJECTION,
                    VIDEO_PROJECTION[4] + ">0 AND " + VIDEO_PROJECTION[3] + "=?",
                    new String[]{"video/mp4"}, VIDEO_PROJECTION[2] + " DESC");
                return cursorLoader;
            }

            return null;
        }

        private boolean fileExist(String path) {
            if (!TextUtils.isEmpty(path)) {
                return new File(path).exists();
            }
            return false;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                if (data.getCount() > 0) {
                    data.moveToFirst();
                    do {
                        String path = data
                            .getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        String name = data
                            .getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data
                            .getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        int width = data
                            .getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[5]));
                        int hight = data
                            .getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[6]));
                        ImageBean image = null;
                        if (fileExist(path) && !mPathList.contains(path)) {
                            mPathList.add(path);
                            image = new ImageBean(path, name, dateTime, width + "x" + hight);
                            mMediaList.add(image);
                        }
                        if (!hasFolderGened) {
                            // 获取文件夹名称
                            File folderFile = new File(path).getParentFile();
                            if (folderFile != null && folderFile.exists()) {
                                String fp = folderFile.getAbsolutePath();
                                FolderBean f = getFolderByPath(fp);
                                if (f == null) {
                                    FolderBean folder = new FolderBean();
                                    folder.name = folderFile.getName();
                                    folder.path = fp;
                                    folder.cover = image;
                                    List<ImageBean> imageList = new ArrayList<>();
                                    imageList.add(image);
                                    folder.images = imageList;
                                    mResultFolder.add(folder);
                                } else {
                                    f.images.add(image);
                                }
                            }
                        }

                    } while (data.moveToNext());
                    Collections.sort(mMediaList);
                    mImageAdapter.setData(mMediaList);
                    // 设定默认选择
                    if (resultList != null && resultList.size() > 0) {
                        mImageAdapter.setDefaultSelected(resultList);
                    }

                    if (!hasFolderGened) {
                        mFolderAdapter.setData(mResultFolder);
                        hasFolderGened = true;
                    }


                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    private FolderBean getFolderByPath(String path) {
        if (mResultFolder != null) {
            for (FolderBean folder : mResultFolder) {
                if (TextUtils.equals(folder.path, path)) {
                    return folder;
                }
            }
        }
        return null;
    }

    /**
     * 回调接口
     */
    public interface Callback {

        void onSingleImageSelected(ImageBean bean);

        void onImageSelected(ImageBean bean);

        void onImageUnselected(ImageBean bean);

        void onCameraShot(ImageBean bean);
    }
}
