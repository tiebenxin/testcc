package com.lensim.fingerchat.commons.utils.compress;


import static com.lensim.fingerchat.commons.utils.compress.Preconditions.checkNotNull;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.L;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ImageCompress {

    public static final int TYPE_AVATAR = 1;
    public static final int TYPE_CIRCLE = 2;
    public static final int TYPE_MESSAGE = 3;

    private static final String TAG = "ImageCompress";
    private static final String DEFAULT_DISK_CACHE_DIR = "images";

    private static volatile ImageCompress INSTANCE;

    private final File mCacheDir;

    private OnCompressListener compressListener;
    private String filename;

    private List<ImageInterface> images;

    private ImageCompress(File cacheDir) {
        mCacheDir = cacheDir;
    }

    /**
     * 获取默认的压缩文件所在目录
     *
     * @param context A context.
     * @see #getPhotoCacheDir(Context, String)
     */
    private static File getPhotoCacheDir(Context context) {
        return getPhotoCacheDir(context, ImageCompress.DEFAULT_DISK_CACHE_DIR);
    }

    /**
     * 获取指定的压缩文件目录
     *
     * @param context A context.
     * @param cacheName The name of the subdirectory in which to store the cache.
     * @see #getPhotoCacheDir(Context)
     */
    private static File getPhotoCacheDir(Context context, String cacheName) {
        File cacheDir = FileUtil.getDiskCacheDirs(context, cacheName);
        if (cacheDir != null) {
            return cacheDir;
        }
        if (Log.isLoggable(TAG, Log.ERROR)) {
            Log.e(TAG, "default disk cache dir is null");
        }
        return null;
    }

    public static ImageCompress get(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ImageCompress(ImageCompress.getPhotoCacheDir(context));
        }
        return INSTANCE;
    }

    public ImageCompress launch() {
        //checkNotNull(mFile, "the image file cannot be null, please call .load() before this method!");
        if (compressListener != null) {
            compressListener.onStart();
        }

        Observable.just(images)
            .map(new Function<List<ImageInterface>, List<ImageInterface>>() {
                @Override
                public List<ImageInterface> apply(
                    @io.reactivex.annotations.NonNull List<ImageInterface> circleImages)
                    throws Exception {
                    List<ImageInterface> images = new ArrayList<>();
                    for (ImageInterface image : circleImages) {

                        thirdCompress(image);
                        images.add(image);

                    }
                    return images;
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(new Consumer<Throwable>() {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Throwable throwable)
                    throws Exception {
                    if (compressListener != null) {
                        compressListener.onError(throwable);
                    }
                }
            })
            .onErrorResumeNext(Observable.<List<ImageInterface>>empty())
            .subscribe(new Consumer<List<ImageInterface>>() {
                @Override
                public void accept(
                    @io.reactivex.annotations.NonNull List<ImageInterface> circleImages)
                    throws Exception {
                    filename = null;
                    if (compressListener != null) {
                        compressListener.onSuccess(circleImages);
                    }
                }
            });
        return this;
    }


    public ImageCompress load(List<ImageInterface> images) {
        this.images = images;
        return this;
    }

    public ImageCompress setCompressListener(OnCompressListener listener) {
        compressListener = listener;
        return this;
    }


    public ImageCompress setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    private static final String MSG_TYPE_SECRET = "<$>SECRET</$>";

    private ImageInterface thirdCompress(@NonNull ImageInterface message) {
        String filePath =
            message.getPath().endsWith(MSG_TYPE_SECRET) ? message.getPath()
                .replace(MSG_TYPE_SECRET, "") : message.getPath();
        String thumb;
        if (ContextHelper.isGif(filePath)) {
            message.setThumb(message.getPath());
            return message;
        } else {
            thumb = mCacheDir.getAbsolutePath() + File.separator +
                (TextUtils.isEmpty(filename) ? System.currentTimeMillis() : filename);
        }
        double size;

        L.d("压缩的文件路径:" + filePath);
        File file = new File(filePath);
        int angle = getImageSpinAngle(filePath);
        int width = getImageSize(filePath)[0];
        int height = getImageSize(filePath)[1];
        L.d("压缩的文件宽:" + width);
        L.d("压缩的文件高:" + height);

        int thumbW = width % 2 == 1 ? width + 1 : width;
        int thumbH = height % 2 == 1 ? height + 1 : height;

        width = thumbW > thumbH ? thumbH : thumbW;//取宽高中较小值
        height = thumbW > thumbH ? thumbW : thumbH;//高度去宽高中的较大值

        double scale = ((double) width / height); //取得压缩比

        if (scale <= 1 && scale > 0.5625) {
            if (height < 1664) {
                if (file.length() / 1024 < 150) {
                    message.setThumb(filePath);
                    return message;
                }
                size = (width * height) / Math.pow(1664, 2) * 300;
                size = size < 60 ? 60 : size;
            } else if (height >= 1664 && height < 4980) {
                thumbW = width / 4;
                thumbH = height / 4;
                size = (thumbW * thumbH) / Math.pow(1245, 2) * 150;
                size = size < 30 ? 30 : size;
            } else if (height >= 4980 && height < 10240) {
                thumbW = width / 8;
                thumbH = height / 8;
                size = (thumbW * thumbH) / Math.pow(2560, 2) * 300;
                size = (thumbW * thumbH) / Math.pow(1280, 2) * 150;
                size = size < 50 ? 50 : size;

            } else {
                int multiple = height / 1280 == 0 ? 1 : height / 1280;
                thumbW = width / multiple;
                thumbH = height / multiple;

                size = (thumbW * thumbH) / Math.pow(2560, 2) * 600;
                size = size < 100 ? 100 : size;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            if (height < 1280 && file.length() / 1024 < 75) {
                message.setThumb(filePath);
                return message;
            }

            int multiple = height / 1280 == 0 ? 1 : height / 1280;
            thumbW = width / multiple;
            thumbH = height / multiple;

            size = (thumbW * thumbH) / (1440.0 * 2560.0) * 800;
            size = size < 100 ? 100 : size;
        } else {
            int multiple = (int) Math.ceil(height / (1280.0 / scale));
            thumbW = width / multiple;
            thumbH = height / multiple;
            size = ((thumbW * thumbH) / (1280.0 * (1280 / scale))) * 1000;
            size = size < 80 ? 80 : size;
        }
        L.d("压缩的期望文件宽:" + thumbW);
        L.d("压缩的期望文件高:" + thumbH);
        File compress = compress(filePath, thumb, thumbW, thumbH, angle, (long) size);
        message.setThumb(compress.getAbsolutePath());
        return message;
    }

    private File firstCompress(@NonNull File file) {
        int minSize = 60;
        int longSide = 720;
        int shortSide = 1280;

        String filePath = file.getAbsolutePath();
        String thumbFilePath =
            mCacheDir.getAbsolutePath() + File.separator + (TextUtils.isEmpty(filename) ? System
                .currentTimeMillis() : filename);

        long size = 0;
        long maxSize = file.length() / 5;

        int angle = getImageSpinAngle(filePath);
        int[] imgSize = getImageSize(filePath);
        int width = 0, height = 0;
        if (imgSize[0] <= imgSize[1]) {
            double scale = (double) imgSize[0] / (double) imgSize[1];
            if (scale <= 1.0 && scale > 0.5625) {
                width = imgSize[0] > shortSide ? shortSide : imgSize[0];
                height = width * imgSize[1] / imgSize[0];
                size = minSize;
            } else if (scale <= 0.5625) {
                height = imgSize[1] > longSide ? longSide : imgSize[1];
                width = height * imgSize[0] / imgSize[1];
                size = maxSize;
            }
        } else {
            double scale = (double) imgSize[1] / (double) imgSize[0];
            if (scale <= 1.0 && scale > 0.5625) {
                height = imgSize[1] > shortSide ? shortSide : imgSize[1];
                width = height * imgSize[0] / imgSize[1];
                size = minSize;
            } else if (scale <= 0.5625) {
                width = imgSize[0] > longSide ? longSide : imgSize[0];
                height = width * imgSize[1] / imgSize[0];
                size = maxSize;
            }
        }
        return compress(filePath, thumbFilePath, width, height, angle, size);
    }

    /**
     * obtain the image's width and height
     * @param imagePath the path of image
     */
    public int[] getImageSize(String imagePath) {
        int[] res = new int[2];

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;
        BitmapFactory.decodeFile(imagePath, options);

        res[0] = options.outWidth;
        res[1] = options.outHeight;

        return res;
    }

    /**
     * obtain the thumbnail that specify the size
     *
     * @param imagePath the target image path
     * @param width the width of thumbnail
     * @param height the height of thumbnail
     * @return {@link Bitmap}
     */
    private Bitmap compress(String imagePath, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        int outH = options.outHeight;
        int outW = options.outWidth;
        int inSampleSize = 1;

        if (outH > height || outW > width) {
            int halfH = outH / 2;
            int halfW = outW / 2;

            while ((halfH / inSampleSize) > height && (halfW / inSampleSize) > width) {
                inSampleSize *= 2;
            }
        }

        options.inSampleSize = inSampleSize;

        options.inJustDecodeBounds = false;

        int heightRatio = (int) Math.ceil(options.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(options.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                options.inSampleSize = heightRatio;
            } else {
                options.inSampleSize = widthRatio;
            }
        }
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(imagePath, options);
    }

    /**
     * obtain the image rotation angle
     *
     * @param path path of target image
     */
    private int getImageSpinAngle(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface
                .getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 指定参数压缩图片
     * create the thumbnail with the true rotate angle
     *
     * @param largeImagePath the big image path
     * @param thumbFilePath the thumbnail path
     * @param width width of thumbnail
     * @param height height of thumbnail
     * @param angle rotation angle of thumbnail
     * @param size the file size of image
     */
    private File compress(String largeImagePath, String thumbFilePath, int width, int height,
        int angle, long size) {
        Bitmap thbBitmap = compress(largeImagePath, width, height);
        L.d("尺寸压缩后的文件宽:" + thbBitmap.getWidth());
        L.d("尺寸压缩后的文件高:" + thbBitmap.getHeight());
        thbBitmap = rotatingImage(angle, thbBitmap);

        return saveImage(thumbFilePath, thbBitmap, size);
    }

    /**
     * 旋转图片
     * rotate the image with specified angle
     *
     * @param angle the angle will be rotating 旋转的角度
     * @param bitmap target image               目标图片
     */
    private static Bitmap rotatingImage(int angle, Bitmap bitmap) {
        //rotate image
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        //create a new image
        return Bitmap
            .createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 保存图片到指定路径
     * Save image with specified size
     *
     * @param filePath the image file save path 储存路径
     * @param bitmap the image what be save   目标图片
     * @param size the file size of image   期望大小
     */
    private File saveImage(String filePath, Bitmap bitmap, long size) {
        checkNotNull(bitmap, TAG + "bitmap cannot be null");
        L.d("保存文件大小不超过：" + size);
        File result = new File(filePath.substring(0, filePath.lastIndexOf("/")));

        if (!result.exists() && !result.mkdirs()) {
            return null;
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, stream);

        while (stream.toByteArray().length / 1024 > size && options > 6) {
            L.d("正在压缩：" + stream.toByteArray().length);
            stream.reset();
            options -= 6;
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, stream);
        }

        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(stream.toByteArray());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new File(filePath);
    }

}