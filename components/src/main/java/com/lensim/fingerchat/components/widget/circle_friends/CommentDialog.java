package com.lensim.fingerchat.components.widget.circle_friends;


import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.lensim.fingerchat.components.R;
import com.lensim.fingerchat.components.dialog.BaseDialog;



/**
 * author yiw
 * ClassName: CommentDialog
 * Description: 评论长按对话框，保护复制和删除
 * date 2015-12-28 下午3:36:39
 */
public class CommentDialog extends BaseDialog {

    private Context mContext;
    //true 显示deleteTv, false 隐藏deleteTv
    private boolean isDisplayDeleteTv;



    public CommentDialog(Context context, boolean isDisplayDeleteTv) {
        super(context, R.style.CommentDialog);
        this.mContext = context;
        this.isDisplayDeleteTv = isDisplayDeleteTv;
    }


    @SuppressWarnings("deprecation")
    private void initWindowParams() {
        Window dialogWindow = getWindow();
        // 获取屏幕宽、高用
        WindowManager wm = (WindowManager) mContext
            .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (display.getWidth() * 0.65); // 宽度设置为屏幕的0.65

        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void initView() {
        setContentView(R.layout.dialog_comment);
        initWindowParams();

        TextView copyTv = findViewById(R.id.copyTv);
        copyTv.setOnClickListener(this);

        TextView deleteTv = findViewById(R.id.deleteTv);
        ImageView devider = findViewById(R.id.divider_dialog);

        if (isDisplayDeleteTv) {
            devider.setVisibility(View.VISIBLE);
            deleteTv.setVisibility(View.VISIBLE);
        } else {
            devider.setVisibility(View.GONE);
            deleteTv.setVisibility(View.GONE);
        }

        deleteTv.setOnClickListener(this);
    }

    @Override
    public void processClick(View view) {
        if (view.getId() == R.id.copyTv && copyClickListener != null) {
            copyClickListener.copyClick();
        } else if (view.getId() == R.id.deleteTv && deleteClickListener != null) {
            deleteClickListener.deletClick();
        }
        dismiss();
    }



    private DeleteClickListener deleteClickListener;
    private CopyClickListener copyClickListener;

    public void setCopyClickListener(CopyClickListener listener) {
        copyClickListener = listener;
    }

    public void setDeleteClickListener(DeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public interface DeleteClickListener {
        void deletClick();
    }

    public interface CopyClickListener {
        void copyClick();
    }

}
