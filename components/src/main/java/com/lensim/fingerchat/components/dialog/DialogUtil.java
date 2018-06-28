package com.lensim.fingerchat.components.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;
import com.lensim.fingerchat.commons.dialog.UserInfoDialog;
import com.lensim.fingerchat.components.R;

public class DialogUtil {

    public static Dialog getUserInfoDialog(Activity context, int resource, String username,
        String avatar, String nick) {
        UserInfoDialog dialog = new UserInfoDialog(context, resource, username, avatar, nick);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

  public static MessageOptionDialog getMsgOptDialog(Context context, int resource, MessageOptionDialog.OptType type) {
    MessageOptionDialog dialog = new MessageOptionDialog(context, R.style.MyDialog, type);
    dialog.setCanceledOnTouchOutside(true);
    return dialog;
  }

  public static Dialog getLoginDialog(Activity context, int resource) {

    final Dialog dialog = new Dialog(context, R.style.MyDialog);
    dialog.setCanceledOnTouchOutside(true);
    dialog.setContentView(R.layout.custom_progress_dialog);

    TextView titleTxtv = (TextView) dialog.findViewById(R.id.dialogText);
    titleTxtv.setText(resource);
    return dialog;
  }

  public static void getAlertDialog(Activity context, String msg, String caption) {

    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setMessage(msg);
    builder.setTitle(caption);
    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });

    builder.create().show();

  }


  public static Dialog getClockInDialog(Activity context, boolean isSuccess, int resource, String time, String address) {
    ClockInDialog dialog = new ClockInDialog(context, isSuccess, resource, time, address);
    dialog.setCanceledOnTouchOutside(true);
    return dialog;
  }

}
