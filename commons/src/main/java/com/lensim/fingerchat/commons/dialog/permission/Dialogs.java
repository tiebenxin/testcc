package com.lensim.fingerchat.commons.dialog.permission;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import com.lensim.fingerchat.commons.R;
import com.lensim.fingerchat.commons.helper.ContextHelper;

public class Dialogs {

    private static final int DEFAULT_STREAM = AudioManager.STREAM_MUSIC;

    public static DialogContainer showBottomSheetDialog(Activity activity, View customView) {
        final Dialog dialog = new Dialog(activity, R.style.MaterialDialogSheet);
//        final Dialog dialog = new Dialog(activity, R.style.dialogCustomPositionStyle);
        dialog.setContentView(customView);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

        DialogContainer result = new DialogContainer(dialog);

        Window window = result.getWindow();
        window.setVolumeControlStream(DEFAULT_STREAM);
        DecoratorNotificationBar.updateNotificationBar(activity, window, ContextHelper.getColor(R.color.lrp_orange7));
        return result;
    }

    public static DialogContainer showAlertDialog(Activity activity, String title, String message, String positiveButton, DialogInterface.OnClickListener positiveListener, String neutralButton, DialogInterface.OnClickListener neutralListener, String negativeButton, DialogInterface.OnClickListener negativeListener) {
        return showAlertDialog(activity, title, message, positiveButton, positiveListener, neutralButton, neutralListener, negativeButton, negativeListener, DEFAULT_STREAM);
    }

    public static DialogContainer showAlertDialog(Activity activity, String title, String message, String positiveButton, DialogInterface.OnClickListener positiveListener, String neutralButton, DialogInterface.OnClickListener neutralListener, String negativeButton, final DialogInterface.OnClickListener negativeListener, int streamType) {
        final DialogContainer.Builder alert = new DialogContainer.Builder(activity);
        alert.setMessage(message);
        alert.setTitle(title);
        if (positiveButton != null) {
            alert.setPositiveButton(positiveButton, positiveListener);
        }
        if (neutralButton != null) {
            alert.setNeutralButton(neutralButton, neutralListener);
        }
        if (negativeButton != null) {
            alert.setNegativeButton(negativeButton, negativeListener);
        }
        DialogContainer dialog = alert.showDialog();
        if (streamType > 0) {
            dialog.getWindow().setVolumeControlStream(streamType);
        }
        DecoratorNotificationBar.updateNotificationBar(activity, dialog.getWindow(), ContextHelper.getColor(R.color.btn_gray_normal));
        return dialog;
    }

//    public static DialogContainer showAlertDialogInputText(Activity activity, String title, String message, String hint, String positiveButton, IInputTextDialogListener positiveListener, String negativeButton, final DialogInterface.OnClickListener negativeListener, Integer inputType, Integer maxLength) {
//        return showAlertDialogInputText(activity, title, message, hint, positiveButton, positiveListener, negativeButton, negativeListener, DEFAULT_STREAM, inputType == null ? InputType.TYPE_CLASS_TEXT : inputType, maxLength);
//    }

//    public static DialogContainer showAlertDialogInputText(Activity activity, String title, String message, String hint, String positiveButton, IInputTextDialogListener positiveListener, String negativeButton, final DialogInterface.OnClickListener negativeListener) {
//        return showAlertDialogInputText(activity, title, message, hint, positiveButton, positiveListener, negativeButton, negativeListener, DEFAULT_STREAM);
//    }

//    public static DialogContainer showAlertDialogInputText(Activity activity, String title, String message, String hint, final String positiveButton, final IInputTextDialogListener positiveListener, String negativeButton, final DialogInterface.OnClickListener negativeListener, int streamType) {
//        return showAlertDialogInputText(activity, title, message, hint, positiveButton, positiveListener, negativeButton, negativeListener, streamType, InputType.TYPE_CLASS_TEXT, null);
//    }

//    public static DialogContainer showAlertDialogInputText(Activity activity, String title, String message, String hint, final String positiveButton, final IInputTextDialogListener positiveListener, String negativeButton, final DialogInterface.OnClickListener negativeListener, int streamType, int inputType, Integer maxLength) {
//        final EditText txt = new EditText(activity);
//        txt.setHint(hint);
//        txt.setBackgroundResource(R.drawable.skin_back_input_text);
//        txt.setTextColor(ContextHelper.getColor(R.color.lrp_black1));
//        int padding = activity.getResources().getDimensionPixelSize(R.dimen.input_text_padding);
//        txt.setPadding(padding, padding, padding, padding);
//        txt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});
//        txt.setInputType(inputType);
//        if (maxLength != null) {
//            txt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
//        }
//
//        final DialogContainer.Builder alert = new DialogContainer.Builder(activity);
//        alert.setMessage(message);
//        alert.setTitle(title);
//        alert.setView(txt);
//        if (positiveButton != null) {
//            alert.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    if (positiveListener != null) {
//                        positiveListener.accept(txt.getText().toString());
//                    }
//                }
//            });
//        }
//        if (negativeButton != null) {
//            alert.setNegativeButton(negativeButton, negativeListener);
//        }
//        DialogContainer dialog = alert.showDialog();
//        if (streamType > 0) {
//            dialog.getWindow().setVolumeControlStream(streamType);
//        }
//        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        DecoratorNotificationBar.updateNotificationBar(activity, dialog.getWindow(), ContextHelper.getColor(R.color.lrp_orange5));
//        return dialog;
//    }

//    public static DialogContainer showAnchoredDialog(Activity activity, View anchoredView, View view, int leftOffset, int rightOffset) {
//        Rect rectViewAnchor = ViewHelper.getViewRectOnScreen(anchoredView);
//        // CHECK is status bar showed
//        rectViewAnchor.set(rectViewAnchor.left, rectViewAnchor.top - My.Device.getStatusBarHeight(), rectViewAnchor.right, rectViewAnchor.bottom - My.Device.getStatusBarHeight());
//        ViewHelper.Size sizeView = ViewHelper.measureView(view);
//        Point sizeScreen = new Point();
//        activity.getWindowManager().getDefaultDisplay().getSize(sizeScreen);
//
//        int x;
//        int y;
//
//        if (rectViewAnchor.left + sizeView.getWidth() <= sizeScreen.x) {
//            x = rectViewAnchor.left + leftOffset;
//        } else {
//            x = (rectViewAnchor.right - sizeView.getWidth()) - rightOffset;
//        }
//
//        if (rectViewAnchor.bottom + sizeView.getHeight() <= sizeScreen.y) {
//            y = rectViewAnchor.bottom;
//        } else {
//            y = rectViewAnchor.top - sizeView.getHeight();
//        }
//
//        Dialog dialog = new DialogExtended(activity, R.style.dialogCustomPositionStyle);
//        dialog.setContentView(view);
//        dialog.setCanceledOnTouchOutside(true);
//        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
//        params.gravity = Gravity.TOP | Gravity.LEFT;
//        params.x = x;
//        params.y = y;
//        dialog.getWindow().setAttributes(params);
//        DialogContainer result = new DialogContainer(dialog);
//        result.show();
//        result.getWindow().setVolumeControlStream(DEFAULT_STREAM);
//        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        return result;
//    }

    public interface IInputTextDialogListener {
        void accept(String text);
    }

    public static DialogContainer showAlertDialog(Activity activity, View view, String title, String positiveButton, DialogInterface.OnClickListener positiveListener, String negativeButton, DialogInterface.OnClickListener negativeListener) {
        return showAlertDialog(activity, view, title, positiveButton, positiveListener, negativeButton, negativeListener, DEFAULT_STREAM);
    }

    public static DialogContainer showAlertDialog(Activity activity, View view, String title, String positiveButton, DialogInterface.OnClickListener positiveListener, String negativeButton, DialogInterface.OnClickListener negativeListener, int streamType) {
        final DialogContainer.Builder alert = new DialogContainer.Builder(activity);
        alert.setView(view);
        alert.setTitle(title);
        if (negativeButton != null) {
            alert.setNegativeButton(negativeButton, negativeListener);
        }
        if (positiveButton != null) {
            alert.setPositiveButton(positiveButton, positiveListener);
        }
        DialogContainer dialog = alert.showDialog();
        if (streamType > 0) {
            dialog.getWindow().setVolumeControlStream(streamType);
        }
        DecoratorNotificationBar.updateNotificationBar(activity, dialog.getWindow(), ContextHelper.getColor(R.color.lrp_green3));
        return dialog;
    }

//    public static DialogContainer showHPProgressSprite(Activity activity, int style, DialogExtended.IOnKeyDownListener onKeyDownListener) {
//        return showHPProgressSprite(activity, DEFAULT_STREAM, style, onKeyDownListener, null);
//    }

//    public static DialogContainer showHPProgressSprite(Activity activity, int streamType, int style, DialogExtended.IOnKeyDownListener onKeyDownListener, View layout) {
//        try {
//            DialogExtended dialog = new DialogExtended(activity, style == -1 ? R.style.dialogBackWhiteTransparency : style);
//            dialog.setOnKeyDownListener(onKeyDownListener);
//
//            if (layout == null) {
//                layout = LayoutInflater.from(activity).inflate(R.layout.tp_layout_progress_sprite, null);
//                ControlSpriteAnimator progress = (ControlSpriteAnimator) layout.findViewById(R.id.progress);
//                View view = layout.findViewById(R.id.view);
//                RotateShowAnimation(view);
//
//              //  progress.setProgressStyle(ProgressStyleArgs.createArgs());
//              //  progress.start();
//            }
//
//            dialog.setContentView(layout);
//            dialog.setCancelable(false);
//            dialog.setCanceledOnTouchOutside(false);
//
//            DialogContainer result = new DialogContainer(dialog);
//            result.show();
//            if (streamType > 0) {
//                result.getWindow().setVolumeControlStream(streamType);
//            }
//            if (style == R.style.dialogBackWhite) {
//                DecoratorNotificationBar.updateNotificationBar(activity, result.getWindow());
//            } else {
//                DecoratorNotificationBar.updateNotificationBar(activity, result.getWindow(), ContextHelper.getColor(R.color.lrp_orange5));
//            }
//            return result;
//        } catch (Exception exc) {
//            L.e(exc);
//        }
//        return null;
//    }

//    public static DialogContainer showAlertDialogCustom(Activity context, DialogView dialogView) {
//        return showAlertDialogCustom(context, dialogView, DEFAULT_STREAM);
//    }

//    public static DialogContainer showAlertDialogCustom(Activity context, DialogView dialogView, int streamType) {
//        return showAlertDialogCustom(context, dialogView, R.style.dialogBackDark, streamType);
//    }

//    public static DialogContainer showAlertDialogCustom(Activity context, DialogView dialogView, int style, int streamType) {
//        DialogExtended dialog = new DialogExtended(context, style);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(dialogView);
//        dialog.setCancelable(true);
//        dialogView.setAlertDialog(dialog);
//        dialogView.prepareLayout();
//        DialogContainer result = new DialogContainer(dialog);
//        result.show();
//        if (streamType > 0) {
//            dialog.getWindow().setVolumeControlStream(streamType);
//        }
//        DecoratorNotificationBar.updateNotificationBar(context, dialog.getWindow(), ContextHelper.getColor(R.color.lrp_orange5));
//        return result;
//    }

//    public static DialogContainer showAlertDialogCustomFromService(Context context, DialogView dialogView) {
//        DialogExtended dialog = new DialogExtended(context, R.style.dialogBackTransparent);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(dialogView);
//        dialog.setCancelable(true);
//        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//        dialogView.setAlertDialog(dialog);
//        dialogView.prepareLayout();
//        DialogContainer result = new DialogContainer(dialog);
//        result.show();
//        return result;
//    }


//    public static Context getFixedDialogContext(Context activity) {
//        // fix Samsung bug in their Lollipop UI implementation
//        Context context = activity;
//        if (My.Application.isBrokenSamsungDevice()) {
//            context = new ContextThemeWrapper(context, android.R.style.Theme_Holo_Light_Dialog);
//        }
//        return context;
//    }

    public static RotateAnimation RotateShowAnimation(View im) {
        RotateAnimation animation = new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lir = new LinearInterpolator();
        animation.setInterpolator(lir);
        animation.setRepeatCount(-1);
        animation.setDuration(1500);
        im.setAnimation(animation);
        return animation;
    }
}
