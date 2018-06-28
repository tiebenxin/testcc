package com.lensim.fingerchat.commons.utils;


import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.support.annotation.NonNull;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.L;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {

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


}
