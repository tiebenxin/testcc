package com.lensim.fingerchat.commons.dialog.permission;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.lensim.fingerchat.commons.R;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import java.util.HashMap;

public class DialogView extends RelativeLayout implements View.OnClickListener {

    public enum EColorScheme {
        Default,
        Dark,
        Transparent,
        WhiteVocabs,
        Light
    }

    public enum EButtonsOrientation {
        Vertical,
        Horizontal
    }

    public enum EButtonSize {
        Default,
        Large
    }

    public static final int BTN_NEGATIVE = 1;
    public static final int BTN_POSITIVE = 2;
    public static final int BTN_CUSTOM_GREEN = 3;
    public static final int BTN_CUSTOM_RED = 5;
    public static final float BTN_LARGE_COEF = 1.5f;
    private final FrameLayout _container;

    private EButtonsOrientation _btnsOrientation = EButtonsOrientation.Horizontal;
    private TextView _txtTitle;
    private TableLayout _tblButtons;
    private LinearLayout _llButtons;
    private final LinearLayout _root;
    private TableRow _rowButtons;
    private final LinearLayout _pnlPlace;
    private final LinearLayout _pnlHeader;

    private Dialog _dialog;
    private OnClickListener _emptyListener;
    private int _id;
    private boolean _isContainsButtons;
    private boolean _isContainsTitle;

    private HashMap<Integer, Pair<Integer, OnClickListener>> _buttons;

    @SuppressLint("WrongViewCast")
    public DialogView(Context context) {
        super(context);

        _root = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.control_alert_dialog, null);
        this.addView(_root, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        _txtTitle = (TextView) findViewById(R.id.txt_title);
        _pnlHeader = (LinearLayout) findViewById(R.id.pnlHeader);
        _tblButtons = (TableLayout) findViewById(R.id.tbl_buttons);
        _rowButtons = (TableRow) findViewById(R.id.btns_row);
        _rowButtons = (TableRow) findViewById(R.id.btns_row);
        _container = (FrameLayout) findViewById(R.id.container);
        _llButtons = (LinearLayout) findViewById(R.id.ll_buttons);
        _pnlPlace = (LinearLayout) findViewById(R.id.pnlPlace);

        _id = 1;
        _emptyListener = new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
        _buttons = new HashMap<Integer, Pair<Integer, OnClickListener>>();
    }

    public void setAlertDialog(Dialog dialog) {
        _dialog = dialog;
    }

    public void setColorScheme(EColorScheme colorScheme) {
        int colorText, containerResource, placeResource;

        switch (colorScheme) {
            case Transparent:
                placeResource = -1;
                containerResource = -1;
                colorText = ContextHelper.getColor(R.color.lrp_gray3);
                _root.setPadding(0, 0, 0, 0);
                _container.setPadding(0, 0, 0, 0);
                break;
            case Dark:
                placeResource = R.drawable.dlg_back_black2;
                containerResource = R.drawable.dlg_back_black1;
                colorText = ContextHelper.getColor(R.color.lrp_gray3);
                break;
            case WhiteVocabs:
                placeResource = R.drawable.dlg_back_place_vocab;
                containerResource = R.drawable.dlg_back_content_vocab;
                colorText = ContextHelper.getColor(R.color.lrp_gray2);
                _container.setPadding(0, 0, 0, 0);
                break;
            case Light:
                placeResource = R.drawable.dlg_back_content_light;
                containerResource = R.drawable.dlg_back_content_light;
                colorText = ContextHelper.getColor(R.color.lrp_black1);
                break;
            default:
                placeResource = R.drawable.dlg_back_place_light;
                containerResource = R.drawable.dlg_back_content_light;
                colorText = ContextHelper.getColor(R.color.lrp_gray2);
                break;
        }
        if (placeResource == -1) {
            ContextHelper.setBackground(_pnlPlace, null);
        } else {
            _pnlPlace.setBackgroundResource(placeResource);
        }

        if (containerResource == -1) {
            ContextHelper.setBackground(_container, null);
        } else {
            _container.setBackgroundResource(containerResource);
        }

        _txtTitle.setTextColor(colorText);
    }

    public void setGravity(int gravity) {
        _root.setGravity(gravity);
    }

    public void setOutlinePadding(int outlinePadding) {
        _root.setPadding(outlinePadding, outlinePadding, outlinePadding, outlinePadding);
    }

    public void setContainerPadding(int outlinePadding) {
        _container.setPadding(outlinePadding, outlinePadding, outlinePadding, outlinePadding);
    }

    public void setTitle(String title) {
        _isContainsTitle = title != null && !title.isEmpty();
        _txtTitle.setText(title);
    }

    public DialogView setContentView(View view) {
        _container.addView(view);
        return this;
    }

    public void clearContentView() {
        _container.removeAllViews();
    }

    // Only before buttons add
    public void setButtonsOrientation(EButtonsOrientation buttonsOrientation) {
        _btnsOrientation = buttonsOrientation;
    }

    public void addButtonHeader(int drawable, OnClickListener listener) {
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(drawable);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setOnClickListener(listener);

        int pixelSize = getContext().getResources().getDimensionPixelSize(R.dimen.img5);
        int margin = getContext().getResources().getDimensionPixelSize(R.dimen.indent_10);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(pixelSize, pixelSize);
        params.setMargins(margin, 0, margin, 0);

        _txtTitle.setPadding(margin + pixelSize, 0, 0, 0);
        _pnlHeader.addView(imageView, params);
    }

    public int addButton(int type, String text, OnClickListener listener) {
        return addButton(type, text, listener, EButtonSize.Default);
    }

    public int addButton(int type, String text, OnClickListener listener, EButtonSize buttonSize) {
        _isContainsButtons = true;

        TextView btn = new TextView(getContext());
        btn.setGravity(Gravity.CENTER);
//        btn.setFont(FontHelper.EFont.ROBOTO_LIGHT);
        btn.setBackgroundResource(getButtonBackgroundResource(type));
        btn.setId(_id++);
        btn.setTextColor(ContextHelper.getColor(R.color.white));
        btn.setOnClickListener(this);
        btn.setText(text);
        _buttons.put(btn.getId(), new Pair<Integer, OnClickListener>(type, listener != null ? listener : _emptyListener));

        if (_btnsOrientation == EButtonsOrientation.Horizontal) {
            btn.setHeight(buttonSize == EButtonSize.Default ? (int) ContextHelper.getContext().getResources().getDimension(R.dimen.default_btn_height) : (int) ((ContextHelper.getContext().getResources().getDimension(R.dimen.default_btn_height) * 1.5f)));
            _rowButtons.addView(btn, new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            _llButtons.setVisibility(GONE);
        } else {
            _tblButtons.setVisibility(GONE);
            btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, buttonSize == EButtonSize.Default ? (int) ContextHelper.getContext().getResources().getDimension(R.dimen.default_btn_height) : (int) ((ContextHelper.getContext().getResources().getDimension(R.dimen.default_btn_height) * BTN_LARGE_COEF))));
            _llButtons.addView(btn);
        }

        return btn.getId();
    }

    private int getButtonBackgroundResource(int type) {
        switch (type) {
            case BTN_CUSTOM_GREEN:
            case BTN_POSITIVE:
                return R.drawable.skin_btn_green;
            case BTN_CUSTOM_RED:
            case BTN_NEGATIVE:
                return R.drawable.skin_btn_red;
            default:
                return R.drawable.skin_btn_green;
        }
    }

    public void prepareLayout() {
        if (!_isContainsTitle) {
            _pnlHeader.setVisibility(GONE);
        }

        if (!_isContainsButtons) {
            _tblButtons.setVisibility(GONE);
            _llButtons.setVisibility(GONE);
        } else {
            if (_rowButtons.getChildCount() > 1 && _btnsOrientation == EButtonsOrientation.Horizontal) {
                for (int i = 0; i < _rowButtons.getChildCount(); i++) {
                    View childButton = _rowButtons.getChildAt(i);

                    TableRow.LayoutParams layoutParams = (TableRow.LayoutParams) childButton.getLayoutParams();

                    int spaceBetween = (int) getContext().getResources().getDimension(R.dimen.indent_4);

                    if (i == 0) {
                        layoutParams.setMargins(0, 0, spaceBetween, 0);
                    } else if (i + 1 == _rowButtons.getChildCount()) {
                        layoutParams.setMargins(spaceBetween, 0, 0, 0);
                    } else {
                        layoutParams.setMargins(spaceBetween, 0, spaceBetween, 0);
                    }
                    childButton.setLayoutParams(layoutParams);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (_buttons.containsKey(v.getId())) {
            Pair<Integer, OnClickListener> pair = _buttons.get(v.getId());
            if (pair.second != null) {
                pair.second.onClick(v);
            }
            if (_dialog != null && pair.first == BTN_NEGATIVE || pair.first == BTN_POSITIVE) {
                _dialog.cancel();
            }
        }
    }
}