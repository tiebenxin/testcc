package com.example.webview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.example.webview.databinding.ActivityRecommendBinding;
import com.example.webview.sonic.SonicRuntimeImpl;
import com.example.webview.sonic.SonicSessionClientImpl;
import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.base.BaseObserver;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.components.dialog.nifty_dialog.Effectstype;
import com.lensim.fingerchat.components.dialog.nifty_dialog.NiftyDialogBuilder;
import com.lensim.fingerchat.data.hrcs.HRCS;
import com.lensim.fingerchat.data.response.ret.RetArrayResponse;
import com.lensim.fingerchat.data.work_center.identify.UserIdentify;
import com.lensim.fingerchat.data.work_center.identify.UserIdentifyResponse;

import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicEngine;
import com.tencent.sonic.sdk.SonicSession;
import com.tencent.sonic.sdk.SonicSessionConfig;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LY309313 on 2017/12/19.
 */

public class BrowserActivity extends BaseActivity {

    public static final String TITLE = "title";
    public static final String USER = "user";

    private static final int FILECHOOSER_RESULTCODE = 1;
    private static final int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 5;
    private String user;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadMessageForAndroid5;
    private String title;
    private String url;
    private SonicSession sonicSession;
    private ActivityRecommendBinding ui;
    private Context mContext;

    @Override
    public void initView() {
        mContext = this;
        ui = DataBindingUtil.setContentView(this, R.layout.activity_recommend);

        title = getIntent().getStringExtra(TITLE);
        user = getIntent().getStringExtra(USER);
        url = getIntent().getDataString();

        initBackButton(ui.mRecommendToolbar, true);

        ui.mRecommendToolbar.setBtSearchDrawable(R.drawable.ic_delete);
        ui.mRecommendToolbar.setTitleText(title);
        if (title.equals("事项跟踪")) {
            ui.mRecommendToolbar.setBtImageDrawable(0);
        }
        setVebView();
        initListener();
    }


    private void setVebView() {
        int networkState = TDevice.getNetworkState(this);
        if (networkState == TDevice.NETTYPE_NONE) {
            ui.recommendWeb.setVisibility(View.GONE);
            ui.mErrorDesc.setText(R.string.no_network);
            ui.mWebErrorRoot.setVisibility(View.VISIBLE);

        } else if (StringUtils.isEmpty(url)) {
            ui.recommendWeb.setVisibility(View.GONE);
            ui.mWebErrorRoot.setVisibility(View.VISIBLE);
            ui.mWebError.setImageResource(R.drawable.system_no_function);
        } else {
            if (!SonicEngine.isGetInstanceAllowed()) {
                SonicEngine.createInstance(new SonicRuntimeImpl(getApplication()),
                    new SonicConfig.Builder().build());
            }
            SonicSessionClientImpl sonicSessionClient = null;
            SonicSessionConfig.Builder sessionConfigBuilder = new SonicSessionConfig.Builder();
            sessionConfigBuilder.setSupportLocalServer(true);

            sonicSession = SonicEngine.getInstance()
                .createSession(url, sessionConfigBuilder.build());
            if (null != sonicSession) {
                sonicSession.bindClient(sonicSessionClient = new SonicSessionClientImpl());
            } else {
                Toast.makeText(this, "create sonic session fail!", Toast.LENGTH_LONG).show();
            }
            ui.recommendWeb.setWebChromeClient(new FGWebChromeClient());
            ui.recommendWeb.setWebViewClient(new FGWebViewClient());

            WebSettings webSettings = ui.recommendWeb.getSettings();
            webSettings.setJavaScriptEnabled(true);
            ui.recommendWeb.removeJavascriptInterface("searchBoxJavaBridge_");

            webSettings.setAllowContentAccess(true);
            webSettings.setDatabaseEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setAppCacheEnabled(true);
            //已废弃
            webSettings.setSavePassword(false);
            webSettings.setSaveFormData(false);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);

            if (sonicSessionClient != null) {
                sonicSessionClient.bindWebView(ui.recommendWeb);
                sonicSessionClient.clientReady();
            } else { // default mode
                ui.recommendWeb.loadUrl(url);
            }
        }
    }

    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
    }

    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        mUploadMessageForAndroid5 = uploadMsg;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
    }

    public void initListener() {
        ui.floatMenu.setOnMenuSelectListener((position) -> {
            switch (position) {
                case 0:
                    contactToHr();
                    break;
                case 1:
                    contactToHr();
                    break;
            }
        });
    }

    private void contactToHr() {
        UserIdentify userIdentify = UserIdentifyResponse.getInstance().getUserIdentify();
        if (userIdentify != null && !StringUtils.isEmpty(userIdentify.EmployeeNO)) {
            final String number = userIdentify.EmployeeNO;
            showNiftyDialog(number);
        } else {
            T.show("没获取到工号信息，可能是没认证");
        }
    }

    private void showNiftyDialog(String number) {
        final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(this);
        builder.withTitle("提示")
            .withMessage("是否以工号:" + number + "开始咨询客服")
            .withButton1Text("取消")
            .withButton2Text("开始咨询")
            .withDuration(200)
            .setButton1Click((v) -> builder.dismiss())
            .setButton2Click((v) -> {
                builder.dismiss();
//                        showProgress("正在查找客服...",true);
                toHr(number);
            })
            .show();
    }

    private void toHr(String num) {
        String url = String.format(Route.URL_GET_HR, num);
        Http.getHrNumber(url)
            .compose(RxSchedulers.compose())
            .subscribe(new BaseObserver<RetArrayResponse<HRCS>>() {
                @Override
                public void onNext(RetArrayResponse<HRCS> response) {
                    if (1 == response.retCode) {
                        HRCS hrcs = response.retData.get(0);
                        String username = hrcs.hrid.toLowerCase();
                        String usernick = hrcs.hrname;
                        startChatActivity(username, usernick);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    T.show("没有在线客服");
                }
            });
    }

    private void startChatActivity(String hrname, String usernick) {
//        AbstractChat chat = MessageManager.getInstance().getOrCreateChat(
//            AccountManager.getInstance().getUserId().getUserId(), username + "@" + ConnectionItem.DEFAULT_SERVER_NAME);
//        chat.setUsernick(usernick);
//        Intent intent = ChatActivity.createSpecificChatIntent(
//            mContext, AccountManager.getInstance().getUserId().getUserId(), username + "@fingerchat.cn");
//        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (null != sonicSession) {
            sonicSession.destroy();
            sonicSession = null;
        }
        super.onDestroy();
    }

    private void exeMethod(String method) {
        switch (method) {
            case "hidebar":
                hideToolbar();
                break;
            case "showbar":
                showToolbar();
                break;
            case "finish":
                finish();
                break;
            default:
                parserMethod(method);
                break;
        }

    }

    private void parserMethod(String method) {
        String[] split = method.split("\\?");
        if (split.length > 1 && split[0].equals("vote")) {
            String[] data = split[1].split("=");
            if (data.length > 1 && data[0].equals("data")) {
                try {
                    JSONObject object = new JSONObject(data[1]);
                    int status = object.getInt("status");
                    switch (status) {
                        case 0:
                            Intent intent = new Intent();
                            intent.putExtra("vote_message", data[1]);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                            break;
                        case 1:
                            //http://mobile.fingerchat.cn:8686/vote/votingdetails.html?id=账号&voteid=投票事项id
//                            String account = AccountManager.getInstance().getUserjid();
//                            MessageManager.getInstance().voteMessage(account,user,data[1]);
//                            String url = LensImUtil.Host + "vote/votingdetails.html?id=%s&&voteid=%s";
//                            //StringBuilder builder = new StringBuilder("http://mobile.fingerchat.cn:8686/vote/index.html");
//                            url = String.format(url, LensImUtil.getUserName(),object.getString("voteid"));
//                            ui.recommendWeb.loadUrl(url);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void hideToolbar() {
        if (ui.mRecommendToolbar.getVisibility() != View.GONE) {
            ui.mRecommendToolbar.setVisibility(View.GONE);

        }
    }

    private void showToolbar() {
        if (ui.mRecommendToolbar.getVisibility() != View.VISIBLE) {
            ui.mRecommendToolbar.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void backPressed() {
        if (ui.recommendWeb.canGoBack()) {
            ui.recommendWeb.goBack();
        } else {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (ui.recommendWeb.canGoBack()) {
                ui.recommendWeb.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (title.equals("事项跟踪") || title.equals("投票")) {
            hideToolbar();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) {
                return;
            }
            Uri result =
                intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            if (null == mUploadMessageForAndroid5) {
                return;
            }
            Uri result =
                (intent == null || resultCode != Activity.RESULT_OK) ? null : intent.getData();
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
            mUploadMessageForAndroid5 = null;
        }
    }


    private class FGWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }


        //扩展支持alert事件
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(mContext);
            builder.withTitle("提示")
                .withMessage(message)
                .withEffect(Effectstype.Newspager)
                .withButton1Text("取消")
                .withButton2Text("确定")
                .setButton1Click((v) -> builder.dismiss())
                .setButton2Click((v) -> builder.dismiss())
                .show();
            result.confirm();
            return true;
        }

        //扩展浏览器上传文件
        //3.0++版本
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            openFileChooserImpl(uploadMsg);
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType,
            String capture) {
            openFileChooserImpl(uploadMsg);
        }

        // For Android > 5.0
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg,
            FileChooserParams fileChooserParams) {
            openFileChooserImplForAndroid5(uploadMsg);
            return true;
        }
    }


    private class FGWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (sonicSession != null) {
                sonicSession.getSessionClient().pageFinish(url);
            }
        }

        @TargetApi(21)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view,
            WebResourceRequest request) {
            return shouldInterceptRequest(view, request.getUrl().toString());
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (sonicSession != null) {
                return (WebResourceResponse) sonicSession.getSessionClient().requestResource(url);
            }
            return null;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
            String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            L.e("这一下呢加载失败了" + failingUrl);
            view.setVisibility(View.GONE);
            ui.mWebErrorRoot.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request,
            WebResourceError error) {
            super.onReceivedError(view, request, error);
            view.setVisibility(View.GONE);
            ui.mWebErrorRoot.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            byte[] decode = URLUtil.decode(url.getBytes());
            String result = new String(decode);
            String protocol = "ltp://mobile";
            if (result.startsWith(protocol)) {
                int index = result.lastIndexOf("/");
                String method = result.substring(index + 1);
                exeMethod(method);
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

}
