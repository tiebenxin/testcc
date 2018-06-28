package com.lensim.fingerchat.hexmeet.widget;

import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import com.lensim.fingerchat.hexmeet.R;
import java.lang.reflect.Method;

public class SipNumberKeyboard {

  private EditText edittext;
  private View keyboard;
  private OnHideListener onHideListener;

  public SipNumberKeyboard(EditText et, View _keyboard) {
    super();
    edittext = et;
    keyboard = _keyboard;

    hideSoftInputMethod(et);

    init();
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

  public void show() {
    keyboard.setVisibility(View.VISIBLE);
  }

  public void show(OnHideListener listener) {
    keyboard.setVisibility(View.VISIBLE);
    onHideListener = listener;
  }

  public boolean isKeyBoardVisible() {
    return keyboard.getVisibility() == View.VISIBLE;
  }

  public void hide() {
    keyboard.setVisibility(View.GONE);
    edittext.clearFocus();
    if (onHideListener != null) {
      onHideListener.onHide();
    }
  }

  private void init() {
    keyboard.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        hide();
      }
    });

    keyboard.findViewById(R.id.one).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_1);
      }
    });

    keyboard.findViewById(R.id.two).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_2);
      }
    });

    keyboard.findViewById(R.id.three).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_3);
      }
    });

    keyboard.findViewById(R.id.four).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_4);
      }
    });

    keyboard.findViewById(R.id.five).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_5);
      }
    });

    keyboard.findViewById(R.id.six).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_6);
      }
    });

    keyboard.findViewById(R.id.seven).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_7);
      }
    });

    keyboard.findViewById(R.id.eight).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_8);
      }
    });

    keyboard.findViewById(R.id.nine).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_9);
      }
    });

    keyboard.findViewById(R.id.star).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_STAR);
      }
    });

    keyboard.findViewById(R.id.zero).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_0);
      }
    });

    keyboard.findViewById(R.id.backspace).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_DEL);
      }
    });
    keyboard.findViewById(R.id.backspace_btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_DEL);
      }
    });

    keyboard.findViewById(R.id.join_via_video).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_ENTER);
        if (edittext.getText().length() > 0) {
          hide();
        }
      }
    });
    keyboard.findViewById(R.id.video_call_btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_ENTER);
        if (edittext.getText().length() > 0) {
          hide();
        }
      }
    });

    keyboard.findViewById(R.id.exit).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        hide();
      }
    });
    keyboard.findViewById(R.id.exit_btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        hide();
      }
    });

    keyboard.findViewById(R.id.join_via_audio).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_CALL);
        if (edittext.getText().length() > 0) {
          hide();
        }
      }
    });
    keyboard.findViewById(R.id.audio_call_btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendKey(KeyEvent.KEYCODE_CALL);
        if (edittext.getText().length() > 0) {
          hide();
        }
      }
    });
  }

  private void sendKey(int keyCode) {
    edittext.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
    edittext.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyCode));
  }

  public static interface OnHideListener {

    public void onHide();
  }
}
