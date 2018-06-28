package com.lensim.fingerchat.hexmeet.conf;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import com.hexmeet.sdk.HexmeetCallState;
import com.hexmeet.sdk.HexmeetReason;
import com.hexmeet.sdk.HexmeetRegistrationState;
import com.hexmeet.sdk.HexmeetSdkListener;
import com.lensim.fingerchat.hexmeet.App;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.api.ApiClient;
import com.lensim.fingerchat.hexmeet.api.model.RestParticipant;
import com.lensim.fingerchat.hexmeet.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;

public class ConferenceParticipantList extends FragmentActivity {

  private Activity context;

  private TextView video_participant_count;
  private TextView audio_participant_count;

  private ListView videoPartListView;
  private ParticipantAdapter videoPartAdapter;
  private final ArrayList<RestParticipant> adapterVideoParts = new ArrayList<RestParticipant>();

  private ListView audioPartListView;
  private ParticipantAdapter audioPartAdapter;
  private final ArrayList<RestParticipant> adapterAudioParts = new ArrayList<RestParticipant>();

  private View close;

  private ParticipantCountScanner participantCountScanner;

  private HexmeetSdkListener mListener;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);

    setContentView(R.layout.conference_participant_list);
    context = this;

    boolean isVideoCall = getIntent().getBooleanExtra("isVideoCall", false);
    if (!isVideoCall) {
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    close = findViewById(R.id.close);
    close.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    video_participant_count = (TextView) findViewById(R.id.video_participant_count);
    audio_participant_count = (TextView) findViewById(R.id.audio_participant_count);
    videoPartListView = (ListView) findViewById(R.id.video_participant_list);
    audioPartListView = (ListView) findViewById(R.id.audio_participant_list);

    videoPartAdapter = new ParticipantAdapter(context, R.layout.participant_item, adapterVideoParts);
    videoPartListView.setAdapter(videoPartAdapter);

    audioPartAdapter = new ParticipantAdapter(context, R.layout.participant_item, adapterAudioParts);
    audioPartListView.setAdapter(audioPartAdapter);

    video_participant_count.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        findViewById(R.id.video_bottom_bar).setVisibility(View.VISIBLE);
        findViewById(R.id.audio_bottom_bar).setVisibility(View.INVISIBLE);
        videoPartListView.setVisibility(View.VISIBLE);
        audioPartListView.setVisibility(View.GONE);
        video_participant_count.setTextColor(Color.parseColor("#313131"));
        audio_participant_count.setTextColor(Color.parseColor("#919191"));
      }
    });

    audio_participant_count.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        findViewById(R.id.audio_bottom_bar).setVisibility(View.VISIBLE);
        findViewById(R.id.video_bottom_bar).setVisibility(View.INVISIBLE);
        audioPartListView.setVisibility(View.VISIBLE);
        videoPartListView.setVisibility(View.GONE);
        audio_participant_count.setTextColor(Color.parseColor("#313131"));
        video_participant_count.setTextColor(Color.parseColor("#919191"));
      }
    });

    participantCountScanner = new ParticipantCountScanner();
    participantCountScanner.sendEmptyMessage(1);

    mListener = new HexmeetSdkListener() {
      @Override
      public void globalState() {

      }

      @Override
      public void registrationState(HexmeetRegistrationState hexmeetRegistrationState) {

      }

      @Override
      public void callState(HexmeetCallState state, HexmeetReason reason) {
        Log.e("TEST_STATUS", state.toString());
        if (!App.getHexmeetSdkInstance().hasOngoingCall()) {
          ConferenceParticipantList.this.finish();
          return;
        }

        if (state == HexmeetCallState.Connected) {
          ConferenceParticipantList.this.finish();
        }
      }
    };

    if (App.getHexmeetSdkInstance() == null) {
      Log.e("", "Trying to reinviteWithVideo while not in call: doing nothing");
      return;
    }

    App.getHexmeetSdkInstance().addHexmeetSdkListener(mListener);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    App.getHexmeetSdkInstance().removeHexmeetSdkListener(mListener);

    participantCountScanner.removeMessages(1);
  }

  public class ParticipantCountScanner extends Handler {

    public ParticipantCountScanner() {
    }

    public ParticipantCountScanner(Looper looper) {
      super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
      if (!App.getHexmeetSdkInstance().hasOngoingCall()) {
        return;
      }

      String numericId = App.getHexmeetSdkInstance().getFarendInfo().getCallNumber();
      String numericIdWithoutPassword = numericId;
      if (numericId.contains("*")) {
        numericIdWithoutPassword = numericId.split("\\*")[0];
      }
      try {
        ApiClient.getParticipants(Integer.parseInt(numericIdWithoutPassword),
            new retrofit2.Callback<List<RestParticipant>>() {
              @Override
              public void onResponse(Call<List<RestParticipant>> call, Response<List<RestParticipant>> response) {
                if (!response.isSuccessful()) {
                  Utils.showToast(context, ApiClient.fromErrorResponse(response));

                  finish();
                  return;
                }

                List<RestParticipant> parts = response.body();
                adapterVideoParts.clear();
                adapterAudioParts.clear();
                for (RestParticipant rest : parts) {
                  if (rest.isVideoMode()) {
                    adapterVideoParts.add(rest);
                  } else {
                    adapterAudioParts.add(rest);
                  }
                }

                video_participant_count.setText(getString(R.string.video) + "(" + adapterVideoParts.size() + ")");
                audio_participant_count.setText(getString(R.string.audio) + "(" + adapterAudioParts.size() + ")");
                videoPartAdapter.notifyDataSetChanged();
                audioPartAdapter.notifyDataSetChanged();
              }

              @Override
              public void onFailure(Call<List<RestParticipant>> call, Throwable e) {
              }
            });
      } catch (Exception e) {
        e.printStackTrace();
      }
      participantCountScanner.sendEmptyMessageDelayed(1, 2000);
    }
  }
}
