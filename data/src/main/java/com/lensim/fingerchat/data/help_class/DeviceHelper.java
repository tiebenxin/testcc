package com.lensim.fingerchat.data.help_class;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

/**
 * Created by LL130386 on 2017/8/23.
 */

public class DeviceHelper {

  public static int getNumCores() {
    try {
      File dir = new File("/sys/devices/system/cpu/");
      File[] files = dir.listFiles(new CpuFilter());
      return files.length;
    } catch (Exception ex) {
//      Log.e(ex);
      System.out.println(DeviceHelper.class.getSimpleName() + "-" + ex.getMessage());
      return 4;
    }
  }

  private static class CpuFilter implements FileFilter {

    @Override
    public boolean accept(File pathname) {
      return Pattern.matches("cpu[0-9]+", pathname.getName());
    }
  }

}
