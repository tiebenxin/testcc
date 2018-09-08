package com.lensim.fingerchat.commons.utils;


import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import com.lensim.fingerchat.commons.R;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {

    //    public static final int DEFAULT_WIDTH = 1080;
//    public static final int DEFAULT_HEIGHT = 1920;
    public static final int DEFAULT_WIDTH = 720;
    public static final int DEFAULT_HEIGHT = 1280;

    public static final int MAX_SIZE = 512;//压缩目标文件大小

    public static Bitmap specifyRatio(Bitmap bitmap, float w, float h) {
        L.d("设置的宽：" + w + "高：" + h);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        if (os.toByteArray().length / 1024 > 1024) {
            os.reset();
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, os);
        }
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;
        op.inPreferredConfig = Config.RGB_565;
        Bitmap bm = BitmapFactory.decodeStream(is, null, op);
        op.inJustDecodeBounds = false;
        int width = op.outWidth;
        int height = op.outHeight;
        L.d("压缩前图片宽：" + width + "高：" + height);
        int wRatio = 1;
        int hRatio = 1;
        if (width > w) {
            wRatio = (int) (width / w);
        }

        if (height > h) {
            hRatio = (int) (height / h);
        }
        L.d("压缩比例：" + Math.max(wRatio, hRatio));
        op.inSampleSize = Math.max(wRatio, hRatio);
        is = new ByteArrayInputStream(os.toByteArray());
        bm = BitmapFactory.decodeStream(is, null, op);
        L.d("压缩后图片宽：" + bitmap.getWidth() + "高：" + bitmap.getHeight());
        return bm;
    }

    public static File compress(String s) {
        File file = new File(s);
        if (!file.exists()) {
            throw new IllegalArgumentException("需要正确的文件路径");
        }
        return firstCompress(file);
    }

    public static File firstCompress(@NonNull File file) {
        int minSize = 60;
        int longSide = 720;
        int shortSide = 1280;

        String filePath = file.getAbsolutePath();

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

        return compress(filePath, width, height, angle, size);
    }


    /**
     * obtain the image's width and height
     *
     * @param imagePath the path of image
     */
    public static int[] getImageSize(String imagePath) {
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
     * obtain the image rotation angle
     *
     * @param path path of target image
     */
    public static int getImageSpinAngle(String path) {
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


    public static File compress(String largeImagePath, int width, int height, int angle,
        long size) {
        Bitmap thbBitmap = compress(largeImagePath, width, height);
        L.d("尺寸压缩后的文件宽:" + thbBitmap.getWidth());
        L.d("尺寸压缩后的文件高:" + thbBitmap.getHeight());
        thbBitmap = rotatingImage(angle, thbBitmap);

        return saveImage(largeImagePath, thbBitmap, size);
    }

    /**
     * obtain the thumbnail that specify the size
     *
     * @param imagePath the target image path
     * @param width the width of thumbnail
     * @param height the height of thumbnail
     * @return {@link Bitmap}
     */
    public static Bitmap compress(String imagePath, int width, int height) {
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
     * 旋转图片
     * rotate the image with specified angle
     *
     * @param angle the angle will be rotating 旋转的角度
     * @param bitmap target image               目标图片
     */
    public static Bitmap rotatingImage(int angle, Bitmap bitmap) {
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
    public static File saveImage(String filePath, Bitmap bitmap, long size) {
        if (bitmap == null) {
            throw new NullPointerException("bitmap is null");
        }
        L.d("保存文件大小不超过：" + size);
//		File result = new File(filePath.substring(0, filePath.lastIndexOf("/")));
//
//		if (!result.exists() && !result.mkdirs()) return null;

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

    public static Bitmap getBitmapFromDrawable(Drawable d) {
        // 取 drawable 的长宽
        int w = d.getIntrinsicWidth();
        int h = d.getIntrinsicHeight();
        // 取 drawable 的颜色格式
        Config config = d.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
            : Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        d.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        d.draw(canvas);

        return bitmap;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bw = (BitmapDrawable) drawable;
            bitmap = bw.getBitmap();
        } else {
            bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
                    : Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
        }
        return bitmap;
    }

    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        return new BitmapDrawable(ContextHelper.getResources(), bitmap);
    }


    //根据采样路压缩
    public static Bitmap decodeBitmap(String filePath, int reqWidth, int reqHeight) {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(filePath, options);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return null;
    }

    protected static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
        int reqHeight) {
        return calculateInSampleSize(options.outWidth, options.outHeight, reqWidth, reqHeight);
    }

    protected static int calculateInSampleSize(int rawWidth, int rawHeight, int reqWidth,
        int reqHeight) {
        int inSampleSize = 1;
        if (rawHeight > reqHeight || rawWidth > reqWidth) {
            final int heightRatio = Math.round((float) rawHeight / (float) reqHeight);
            final int widthRatio = Math.round((float) rawWidth / (float) reqWidth);
            inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }


    static {
        System.loadLibrary("jpeg");// libjpeg
        System.loadLibrary("imagerar");// 我们自己的库
    }


    private static String saveBitmap(Bitmap bitmap, int quality, String fileName,
        boolean optimize) {
        return compressBitmapByNative(bitmap, bitmap.getWidth(), bitmap.getHeight(), quality,
            fileName.getBytes(), optimize);
    }

    /**
     * 本地方法 JNI处理图片
     *
     * @param bitmap bitmap
     * @param width 宽度
     * @param height 高度
     * @param quality 图片质量 100表示不变 越小就压缩越严重
     * @param fileName 文件路径的byte数组
     * @param optimize 是否采用哈弗曼表数据计算
     * @return "0"失败, "1"成功
     */
    public static native String compressBitmapByNative(Bitmap bitmap, int width,
        int height, int quality, byte[] fileName, boolean optimize);

    /**
     * 计算缩放比
     *
     * @param bitWidth 图片宽度
     * @param bitHeight 图片高度
     * @return 比例
     */
    public static int getRatioSize(int bitWidth, int bitHeight) {
        // 图片最大分辨率
        int imageHeight = DEFAULT_HEIGHT;
        int imageWidth = DEFAULT_WIDTH;
        // 缩放比
        int ratio = 1;
        // 缩放比,由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        if (bitWidth > bitHeight && bitWidth > imageWidth) {
            // 如果图片宽度比高度大,以宽度为基准
            ratio = bitWidth / imageWidth;
        } else if (bitWidth <= bitHeight && bitHeight > imageHeight) {
            // 如果图片高度比宽度大，以高度为基准
            ratio = bitHeight / imageHeight;
        }
        // 最小比率为1
        if (ratio <= 0) {
            ratio = 1;
        }
        return ratio;
    }

    /*
    * @param uri 图片路径
    * @param file 压缩结果文件路径
    * 尺寸压缩，质量压缩  // 最大图片大小 500k
    * */
    public static Bitmap compressBitmap(String uri) {
        Bitmap bitmap = decodeFile(uri);
        if (bitmap == null) {
            return null;
        }
        // 根据设定的最大分辨率获取压缩比例
        int ratio = getRatioSize(bitmap.getWidth(), bitmap.getHeight());

        int afterWidth = bitmap.getWidth() / ratio;
        int afterHeight = bitmap.getHeight() / ratio;
        // 根据比例压缩Bitmap到对应尺寸
        Bitmap result = Bitmap.createBitmap(afterWidth, afterHeight,
            Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, afterWidth, afterHeight);
        canvas.drawBitmap(bitmap, null, rect, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        result.compress(Bitmap.CompressFormat.JPEG, options, baos);
        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > MAX_SIZE) {
            // 重置baos
            baos.reset();
            options -= 10;
            result.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        return result;
    }

    public static String saveBitmap(Bitmap bitmap, String file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > MAX_SIZE) {
            // 重置baos
            baos.reset();
            options -= 10;
        }
        if (options < 1) {
            return FileUtil.saveToPicDir(bitmap, file);
        } else {
            String code = saveBitmap(bitmap, options, file, true);
            if (!TextUtils.isEmpty(code) && code.equals("1")) {
                return file;
            }
        }
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        return "";
    }

    /*
  * @param uri 图片路径
  * @param file 压缩结果文件路径
  * 尺寸压缩，质量压缩，jni无损压缩  最大图片大小 512k
  * */
    public static String checkAndCompressBitmap(Bitmap bitmap, String file) {
        String code = "";
        if (bitmap == null) {
            return "";
        }
        // 根据设定的最大分辨率获取压缩比例
        int ratio = getRatioSize(bitmap.getWidth(), bitmap.getHeight());

        int afterWidth = bitmap.getWidth() / ratio;
        int afterHeight = bitmap.getHeight() / ratio;
        // 根据比例压缩Bitmap到对应尺寸
        Bitmap result = Bitmap.createBitmap(afterWidth, afterHeight,
            Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, afterWidth, afterHeight);
        canvas.drawBitmap(bitmap, null, rect, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        result.compress(Bitmap.CompressFormat.JPEG, options, baos);
        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > MAX_SIZE) {
            // 重置baos
            baos.reset();
            options -= 10;
            result.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }

        Log.e("ImageUtils", "save image " + options);

        // 保存图片 true表示使用哈夫曼算法
        code = saveBitmap(result, options, file, true);

        if (result != null && !result.isRecycled()) {
            result.recycle();
            result = null;
        }
        return code;
    }

    public static Bitmap createWaterBitmap(Bitmap resource, String userId) {
        int w = resource.getWidth();
        int h = resource.getHeight();
        Bitmap temp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(temp);
        Paint paint = new Paint();
        String familyName = "宋体";
        Typeface font = Typeface.create(familyName, Typeface.NORMAL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(font);
        paint.setTextSize(DensityUtil.sp2px(ContextHelper.getContext(), 6));
        canvas.drawBitmap(resource, 0, 0, null);
        String waterText = ContextHelper.getContext()
            .getString(R.string.pic_water_content, userId,
                StringUtils.getWaterMarkTime());
        paint.setAlpha(100);//设置字体透明度，0-255
        canvas.drawText(waterText, 10, h - 10, paint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return temp;
    }

    //为避免oom，先采样率压缩
    public static Bitmap decodeFile(String path) {
        Bitmap bitmap = null;
        try {
            File file = new File(path);
            if (file.exists()) {

                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;//不获取bitmap。
                BitmapFactory.decodeFile(path, options);
                options.inSampleSize = getRatioSize(options.outWidth, options.outHeight);
                options.inJustDecodeBounds = false; //获取bitmap。
                options.inPreferredConfig = Config.ARGB_8888;
                options.inDither = true;//抖动解码
                return BitmapFactory.decodeFile(path, options);
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inJustDecodeBounds = false;
//                //避免出现内存溢出的情况，进行相应的属性设置。
//                options.inPreferredConfig = Config.ARGB_8888;
//                options.inDither = true;//抖动解码
//                return BitmapFactory.decodeFile(path, options);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
