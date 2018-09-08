package com.lensim.fingerchat.fingerchat.ui.login;

import android.graphics.Bitmap;
import android.os.Build.VERSION_CODES;
import android.support.annotation.RequiresApi;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.commons.interf.OnControllerClickListenter;
import com.lensim.fingerchat.commons.utils.DensityUtil;

/**
 * Created by LL130386 on 2017/11/14.
 */

public class ControllerLoginItem {

    private final int default_type = InputType.TYPE_NULL;

    public static final int TYPE_PSW = 0;
    public static final int TYPE_TAKE_PHOTO = 1;
    public static final int TYPE_VERIFICATION_CODE = 1 << 1;

    private ImageView iv_icon;
    private EditText et_input;
    private LinearLayout ll_right;
    private OnControllerClickListenter listenter;
    private int rightType;
    private OnActionListener actionListener;
    private boolean isShowPsw;
    private View viewRight;
    private final View viewRoot;

    public ControllerLoginItem(View v) {
        viewRoot = v;
        init(v);
    }

    private void init(View v) {
        iv_icon = (ImageView) v.findViewById(R.id.iv_icon);
        et_input = (EditText) v.findViewById(R.id.et_input);
        ll_right = (LinearLayout) v.findViewById(R.id.ll_right);
        et_input.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionListener != null) {
                    actionListener.onEditorAction(v, actionId, event);
                }
                return true;
            }
        });
    }

    public void initIconHint(int drawable, int string) {
        iv_icon.setImageDrawable(ContextHelper.getDrawable(drawable));
        et_input.setHint(string);
        ll_right.setVisibility(View.GONE);
    }

    @RequiresApi(api = VERSION_CODES.JELLY_BEAN)
    public void addRight(final int type) {
        rightType = type;
        viewRight = createView(type);
        if (viewRight != null) {
            ll_right.setVisibility(View.VISIBLE);
            ll_right.removeAllViews();
            ll_right.addView(viewRight);

            viewRight.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type == TYPE_PSW || type == TYPE_PSW) {
                        setShowText(!isShowPsw);
                        if (et_input.getText() != null && !TextUtils
                            .isEmpty(et_input.getText().toString())) {
                            et_input.setSelection(et_input.getText().toString().length());
                        }
                    } else {
                        if (listenter != null) {
                            listenter.onClick();
                        }
                    }
                }
            });
        }
    }

    public void addRight(Bitmap bitmap) {
        rightType = TYPE_TAKE_PHOTO;
        ImageView imageView = new ImageView(ContextHelper.getContext());
        imageView.setImageBitmap(bitmap);
        imageView.setSelected(false);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, 0, DensityUtil.dip2px(ContextHelper.getContext(), 10), 0);
        imageView.setLayoutParams(params);

        ll_right.setVisibility(View.VISIBLE);
        ll_right.removeAllViews();
        ll_right.addView(imageView);

        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listenter != null) {
                    listenter.onClick();
                }

            }
        });


    }

    @RequiresApi(api = VERSION_CODES.JELLY_BEAN)
    private View createView(int type) {
        ImageView imageView;
        LayoutParams params;
        switch (type) {
            case TYPE_PSW:
                imageView = new ImageView(ContextHelper.getContext());
                imageView.setImageDrawable(ContextHelper.getDrawable(R.drawable.selector_look_psw));
                imageView.setSelected(false);
                params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(0, 0, DensityUtil.dip2px(ContextHelper.getContext(), 10), 0);
                imageView.setLayoutParams(params);
                return imageView;
            case TYPE_VERIFICATION_CODE:
                Button button = new Button(ContextHelper.getContext());
                button.setText(ContextHelper.getString(R.string.get_identify_code));
                button.setTextColor(ContextHelper.getColor(R.color.gay_identify_code));
//                button.setBackground(ContextHelper.getDrawable(R.drawable.btn_get_identify_code));
                button.setBackground(ContextHelper.getDrawable(R.drawable.btn_forget_psw_code));
                button.setPadding(5, 5, 5, 5);

                params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    DensityUtil.dip2px(ContextHelper.getContext(), 32));
                params.setMargins(0, 0, DensityUtil.dip2px(ContextHelper.getContext(), 10), 0);
                button.setLayoutParams(params);
                return button;
            case TYPE_TAKE_PHOTO:
                imageView = new ImageView(ContextHelper.getContext());
                imageView.setImageDrawable(ContextHelper.getDrawable(R.drawable.upload_image));
                imageView.setSelected(false);
                params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(0, 0, DensityUtil.dip2px(ContextHelper.getContext(), 10), 0);
                imageView.setLayoutParams(params);
                return imageView;
        }
        return null;
    }

    public void initEditType(boolean isNum) {
        int type;
        if (isNum) {
            type = InputType.TYPE_CLASS_NUMBER;
        } else {
            type = InputType.TYPE_CLASS_TEXT;
        }
        et_input.setInputType(type);
    }

    public String getText() {
        if (et_input.getText() != null) {
            return et_input.getText().toString();
        } else {
            return "";
        }
    }

    public void requestFocus() {
        et_input.requestFocus();
    }

    public void setEidtAction(boolean isBottom) {
        if (isBottom) {
            et_input.setImeOptions(EditorInfo.IME_ACTION_DONE);
        } else {
            et_input
                .setImeActionLabel(ContextHelper.getString(R.string.next),
                    EditorInfo.IME_ACTION_NEXT);

        }
    }

    public void setClickable(boolean b) {
        if (viewRight != null) {
            viewRight.setClickable(b);
        }
    }

    public void setButtonText(String text, int drawable) {
        if (rightType == TYPE_VERIFICATION_CODE && viewRight instanceof Button) {
            ((Button) viewRight).setText(text);
            viewRight.setBackground(ContextHelper.getDrawable(drawable));
        }
    }


    public void setForgetButtonText(boolean isClickable, String text) {
        if (rightType == TYPE_VERIFICATION_CODE && viewRight instanceof Button) {
            setClickable(isClickable);
            ((Button) viewRight).setText(text);
//            viewRight.setBackground(ContextHelper.getDrawable(drawable));
            if (viewRight.isSelected() == isClickable) {
                viewRight.setSelected(!isClickable);
            }
        }
    }

    private void setImeOptions(int imeOptions) {
        et_input.setImeOptions(imeOptions);
    }

    //密码输入专用
    public void setShowText(boolean isShow) {
        isShowPsw = isShow;
        View view = ll_right.getChildAt(0);
        if (isShow) {
            et_input.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//显示密码
            if (view != null) {
                view.setSelected(true);
            }
        } else {
            et_input.setTransformationMethod(PasswordTransformationMethod.getInstance());//隐藏密码
            if (view != null) {
                view.setSelected(false);
            }

        }
    }

    public void setText(String s) {
        if (!TextUtils.isEmpty(s)) {
            et_input.setText(s);
        }
    }

    public void setVisible(boolean b) {
        viewRoot.setVisibility(b ? View.VISIBLE : View.GONE);
    }


    public void setOnClickListener(OnControllerClickListenter l) {
        listenter = l;
    }

    public interface OnActionListener {

        void onEditorAction(TextView v, int actionId, KeyEvent event);
    }

    public void setOnEditActionListener(OnActionListener l) {
        actionListener = l;
    }

}
