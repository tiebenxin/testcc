package com.lensim.fingerchat.commons.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import java.io.File;
import org.junit.Test;

/**
 * Created by LL130386 on 2018/8/23.
 */
public class BitmapUtilTest {

    @Test
    public void getBitmap() throws Exception {
        String file = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ic_screen.jpg";
        File image = new File(file);
        if (!image.exists()) {
            return;
        }
        ContentResolver cr = ContextHelper.getContext().getContentResolver();
        Uri uri = Uri.fromFile(image);
        Bitmap crBitmap = Media.getBitmap(cr, uri);
        System.out.println(
            "ContentResolver:" + crBitmap.getWidth() + "***" + crBitmap.getHeight() + "--"
                + crBitmap.getByteCount());

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        //避免出现内存溢出的情况，进行相应的属性设置。
        options.inPreferredConfig = Config.ARGB_8888;
//                options.inDither = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file, options);
        System.out.println(
            "BitmapFactory:" + bitmap.getWidth() + "***" + bitmap.getHeight() + "--"
                + bitmap.getByteCount());
    }

}