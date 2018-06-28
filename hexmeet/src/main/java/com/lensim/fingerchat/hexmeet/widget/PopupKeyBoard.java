package com.lensim.fingerchat.hexmeet.widget;

import android.content.Context;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.PopupWindow;
import com.lensim.fingerchat.hexmeet.R;
import java.lang.reflect.Method;

public class PopupKeyBoard extends PopupWindow {

  private EditText editText;
  private boolean isPassword = false;
  private int navBarHeight = 0;

  public PopupKeyBoard(Context context, EditText editor) {
    this(context, editor, false);
  }

  public PopupKeyBoard(Context context, EditText editor, boolean isPassword) {
    editText = editor;
    this.isPassword = isPassword;

    setFocusable(true);
    setTouchable(true);
    setOutsideTouchable(true);

    setWidth(LayoutParams.MATCH_PARENT);
    setHeight(LayoutParams.MATCH_PARENT);

    setContentView(LayoutInflater.from(context).inflate(R.layout.popup_key_board, null));

    int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
    if (resourceId > 0) {
      navBarHeight = context.getResources().getDimensionPixelSize(resourceId);
    }

    initUI();

    setOnDismissListener(new OnDismissListener() {
      @Override
      public void onDismiss() {
        editText.clearFocus();
      }
    });

    hideSoftInputMethod(editor);
  }

  private void hideSoftInputMethod(EditText editor) {
    int sdkVer = android.os.Build.VERSION.SDK_INT;
    String methodName = null;
    if (sdkVer >= 16) {
      methodName = "setShowSoftInputOnFocus";
    } else if (sdkVer >= 14) {
      methodName = "setSoftInputShownOnFocus";
    }

    if (methodName == null) {
      editor.setInputType(InputType.TYPE_NULL);
    } else {
      try {
        Method setShowSoftInputOnFocus = EditText.class.getMethod(methodName, boolean.class);
        setShowSoftInputOnFocus.setAccessible(true);
        setShowSoftInputOnFocus.invoke(editor, false);
      } catch (NoSuchMethodException e) {
        editor.setInputType(InputType.TYPE_NULL);
//        log.error(e.getMessage());
      } catch (Exception e) {
        e.printStackTrace();
//        log.error(e.getMessage());
      }
    }
  }

  private void initUI() {
    getContentView().findViewById(R.id.background).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });

    getContentView().findViewById(R.id.one).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_1);
      }
    });

    getContentView().findViewById(R.id.two).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_2);
      }
    });

    getContentView().findViewById(R.id.three).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_3);
      }
    });

    getContentView().findViewById(R.id.four).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_4);
      }
    });

    getContentView().findViewById(R.id.five).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_5);
      }
    });

    getContentView().findViewById(R.id.six).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_6);
      }
    });

    getContentView().findViewById(R.id.seven).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_7);
      }
    });

    getContentView().findViewById(R.id.eight).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_8);
      }
    });

    getContentView().findViewById(R.id.nine).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_9);
      }
    });

    if (!isPassword) {
      getContentView().findViewById(R.id.dot).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          sendKey(KeyEvent.KEYCODE_PERIOD);
        }
      });

      getContentView().findViewById(R.id.star).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          sendKey(KeyEvent.KEYCODE_STAR);
        }
      });
    }

    getContentView().findViewById(R.id.zero).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_0);
      }
    });

    getContentView().findViewById(R.id.exit).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });
    getContentView().findViewById(R.id.exit_btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });

    if (!isPassword) {
      getContentView().findViewById(R.id.join_via_video).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          sendKey(KeyEvent.KEYCODE_ENTER);
          dismiss();
        }
      });

      getContentView().findViewById(R.id.call_btn).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          sendKey(KeyEvent.KEYCODE_ENTER);
          dismiss();
        }
      });
      getContentView().findViewById(R.id.enter).setVisibility(View.GONE);
      getContentView().findViewById(R.id.join_via_video).setVisibility(View.VISIBLE);
    } else {
      getContentView().findViewById(R.id.enter).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          sendKey(KeyEvent.KEYCODE_ENTER);
          dismiss();
        }
      });
      getContentView().findViewById(R.id.join_via_video).setVisibility(View.GONE);
      getContentView().findViewById(R.id.enter).setVisibility(View.VISIBLE);
    }

    getContentView().findViewById(R.id.backspace).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_DEL);
      }
    });
    getContentView().findViewById(R.id.backspace_btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_DEL);
      }
    });
  }

  private void sendKey(int keyCode) {
    editText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
    editText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyCode));
  }

  public void show(View view) {
    if (android.os.Build.MODEL.contains("HUAWEI")) {
      int[] pos = new int[2];
      view.getLocationOnScreen(pos);
      showAsDropDown(view, 0, -pos[1] - navBarHeight - view.getHeight());
    } else {
      showAtLocation(view, Gravity.BOTTOM | Gravity.START, 0, 0);
    }
    editText.requestFocus();
  }
}
