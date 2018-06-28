package com.lens.chatmodel.view.spannable;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import com.lens.chatmodel.R;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ly309313 on 2016/10/21.
 *
 */
public class SpannableUtil {


    public static Spannable getAtText(Spannable spannable){

        String username = UserInfoRepository.getUserName();
        String nick = UserInfoRepository.getUsernick();
        if(StringUtils.isEmpty(username) || TextUtils.isEmpty(spannable)){
            return null;
        }

        Pattern pattern1 = Pattern.compile(Pattern.quote("@" +username));
        Matcher matcher1 = pattern1.matcher(spannable);
        if(matcher1.find()){
            spannable.setSpan(new ForegroundColorSpan(Color.RED),matcher1.start(),matcher1.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannable;
        }

        Pattern pattern2 = Pattern.compile(Pattern.quote("@" +nick));
        Matcher matcher2 = pattern2.matcher(spannable);
        if(matcher2.find()){
            spannable.setSpan(new ForegroundColorSpan(Color.RED),matcher2.start(),matcher2.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannable;
        }

        return spannable;

    }
    public static Spannable somebodyAtMe(Spannable spannable){

        Pattern pattern3 = Pattern.compile(Pattern.quote("[有人@我]"));
        Matcher matcher3 = pattern3.matcher(spannable);
        if(matcher3.find()){
            spannable.setSpan(new ForegroundColorSpan(Color.RED),matcher3.start(),matcher3.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannable;
        }
        return spannable;
    }

    public static Spannable traftText(Spannable spannable){
        Pattern pattern = Pattern.compile(Pattern.quote("[草稿]"));
        Matcher matcher = pattern.matcher(spannable);
        if(matcher.find()){
            spannable.setSpan(new ForegroundColorSpan(Color.RED),matcher.start(),matcher.end(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        return spannable;
    }

    public static Spannable sending(Context context,Spannable spannable, int size){
        Pattern pattern = Pattern.compile(Pattern.quote("[发送中]"));
        Matcher matcher = pattern.matcher(spannable);
        if(matcher.find()){
            Drawable drawable = context.getResources().getDrawable(R.drawable.indicator_input_error);
//
            if (drawable != null) {
                drawable.setBounds(0,0,size,size);
//
            }
            MyImageSpan localImageSpan = new MyImageSpan(
                    drawable, ImageSpan.ALIGN_BOTTOM);
            spannable.setSpan(localImageSpan,
                    matcher.start(), matcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        return spannable;
    }

    public static Spannable getVoteHint(String name,String titile) {
        int length = name.length();
        int titleLength = titile.length();
        SpannableString spannableString = new SpannableString(name + "参与了投票" + titile);

        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#0e8fdb")),0,length,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#0e8fdb")),spannableString.length() - titleLength,spannableString.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    /*
    * OA消息
    * */
    public static Spannable generateFCSpannable(String text,int color){
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new ForegroundColorSpan(color),0,text.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
