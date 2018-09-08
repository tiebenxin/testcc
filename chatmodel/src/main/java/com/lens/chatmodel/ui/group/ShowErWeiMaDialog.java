package com.lens.chatmodel.ui.group;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.lens.chatmodel.R;
import com.lens.chatmodel.db.MucInfo;
import com.lensim.fingerchat.commons.widgt.AvatarImageView;
import com.lensim.fingerchat.commons.global.FGEnvironment;


/**
 * Created by xhdl0002 on 2018/1/12.
 */

public class ShowErWeiMaDialog extends Dialog {

    public ShowErWeiMaDialog(@NonNull Context context, String mucId, String mucName) {
        super(context, R.style.MyDialog);
        this.context = context;
        this.mucId = mucId;
        this.mucName = mucName;
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = dm.widthPixels;
        window.setAttributes(lp);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    private AvatarImageView show_img;
    private TextView show_name;
    private ImageView show_erweima;
    private Context context;
    private String mucId;
    private String mucName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.dialog_erweima_show, null);
        setContentView(view);
        show_img = view.findViewById(R.id.show_img);
        show_name = view.findViewById(R.id.show_name);
        show_erweima = view.findViewById(R.id.show_erweima);
        FGEnvironment environment = new FGEnvironment();
        show_name.setText(mucName);
        show_img.setDrawText(MucInfo.selectMucUserNickList(context, mucId));

        Glide.with(context)
            .load(environment.getAcodePath(mucId))
            .into(show_erweima);

    }
}
