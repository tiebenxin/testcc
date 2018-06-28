package com.lensim.fingerchat.fingerchat.ui.search;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.commons.base.BaseFragment;
import com.lensim.fingerchat.fingerchat.ui.settings.ControllerSettingItem;
import com.lens.chatmodel.interf.ISearchTypeListener;
import com.lensim.fingerchat.commons.interf.OnControllerClickListenter;

/**
 * Created by LL130386 on 2017/12/4.
 */

public class FragmentSearchType extends BaseFragment {

  public static final int ACCOUT = 0;
  public static final int PHONE_NUM = 1;
  public static final int NICK = 2;
  public static final int REAL_NAME = 3;
  public static final int DEPARTMENT = 4;

  private ControllerSettingItem viewAccout;
  private ControllerSettingItem viewPhoneNum;
  private ControllerSettingItem viewNick;
  private ControllerSettingItem viewRealName;
  private ControllerSettingItem viewDepartment;
  private ISearchTypeListener listener;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_search_type, null);
  }

  @Override
  protected void initView() {
    viewAccout = new ControllerSettingItem(getView().findViewById(R.id.viewAccout));
    viewAccout.setIconAndText(getDrawable(ACCOUT), getName(ACCOUT));
    viewAccout.setOnClickListener(new OnControllerClickListenter() {
      @Override
      public void onClick() {
        if (listener != null) {
          listener.clickAccout();
        }
      }
    });

    viewPhoneNum = new ControllerSettingItem(getView().findViewById(R.id.viewPhoneNum));
    viewPhoneNum.setIconAndText(getDrawable(PHONE_NUM), getName(PHONE_NUM));
    viewPhoneNum.setOnClickListener(new OnControllerClickListenter() {
      @Override
      public void onClick() {
        if (listener != null) {
          listener.clickPhoneNum();
        }
      }
    });

    viewNick = new ControllerSettingItem(getView().findViewById(R.id.viewNick));
    viewNick.setIconAndText(getDrawable(NICK), getName(NICK));
    viewNick.setOnClickListener(new OnControllerClickListenter() {
      @Override
      public void onClick() {
        if (listener != null) {
          listener.clickNick();
        }
      }
    });

    viewRealName = new ControllerSettingItem(getView().findViewById(R.id.viewRealName));
    viewRealName.setIconAndText(getDrawable(REAL_NAME), getName(REAL_NAME));
    viewRealName.setOnClickListener(new OnControllerClickListenter() {
      @Override
      public void onClick() {
        if (listener != null) {
          listener.clickRealName();
        }
      }
    });

    viewDepartment = new ControllerSettingItem(getView().findViewById(R.id.viewDepartment));
    viewDepartment.setIconAndText(getDrawable(DEPARTMENT), getName(DEPARTMENT));
    viewDepartment.setOnClickListener(new OnControllerClickListenter() {
      @Override
      public void onClick() {
        if (listener != null) {
          listener.clickDepartment();
        }
      }
    });

  }


  private String getName(int type) {
    switch (type) {
      case ACCOUT:
        return ContextHelper.getString(R.string.accout);
      case PHONE_NUM:
        return ContextHelper.getString(R.string.phone_num);
      case NICK:
        return ContextHelper.getString(R.string.nick);
      case REAL_NAME:
        return ContextHelper.getString(R.string.real_name);
      case DEPARTMENT:
        return ContextHelper.getString(R.string.department);
      default:
        return "";
    }
  }

  private Drawable getDrawable(int type) {
    switch (type) {
      case ACCOUT:
        return ContextHelper.getDrawable(R.drawable.search_account);
      case PHONE_NUM:
        return ContextHelper.getDrawable(R.drawable.search_phone_number);
      case NICK:
        return ContextHelper.getDrawable(R.drawable.search_nickname);
      case REAL_NAME:
        return ContextHelper.getDrawable(R.drawable.search_name);
      case DEPARTMENT:
        return ContextHelper.getDrawable(R.drawable.search_department);
      default:
        return null;
    }
  }

  public void setOnClickListenr(ISearchTypeListener l) {
    listener = l;
  }

}
