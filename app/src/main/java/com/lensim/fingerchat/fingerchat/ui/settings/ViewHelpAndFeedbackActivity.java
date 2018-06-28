package com.lensim.fingerchat.fingerchat.ui.settings;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.webview.sonic.SonicRuntimeImpl;
import com.example.webview.sonic.SonicSessionClientImpl;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.dialog.NiftyDialogBuilder;
import com.lensim.fingerchat.commons.dialog.effect.Effectstype;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.components.widget.FloatMenu;
import com.lensim.fingerchat.fingerchat.R;
import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicEngine;
import com.tencent.sonic.sdk.SonicSession;
import com.tencent.sonic.sdk.SonicSessionConfig;

/**
 * Created by LY305512 on 2018/1/2.
 */

public class ViewHelpAndFeedbackActivity extends BaseActivity {

    private static final int FILECHOOSER_RESULTCODE = 1;
    private static final int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 5;
    private WebView mWebView;
    private View mWebErrorRoot;
    private FloatMenu floatMenu;
    private String user;
    private TextView mErrorDesc;
    private ImageView mWebError;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadMessageForAndroid5;
    private String title;
    private SonicSession sonicSession;

    protected FGToolbar mToolbar;
    private int hasNav;

    @Override
    public void initView() {
        setContentView(R.layout.activity_viewhelpandfeedback);
//        wb_view = findViewById(R.id.wb_view);
//        //显示web数据
//        wb_view.loadUrl(url);
        title = getIntent().getStringExtra("title");
        user = getIntent().getStringExtra("user");
        hasNav = getIntent().getIntExtra("hasNav", 1);
        String url = getIntent().getDataString();
        mWebView = ((WebView) findViewById(R.id.recommend_web));
        mWebErrorRoot = findViewById(R.id.mWebErrorRoot);
        mWebError = ((ImageView) findViewById(R.id.mWebError));
        floatMenu = (FloatMenu) findViewById(R.id.floatMenu);
        mErrorDesc = (TextView) findViewById(R.id.mErrorDesc);
        mToolbar = findViewById(R.id.fg_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleText(title);
        initBackButton(mToolbar, true);
        int networkState = TDevice.getNetworkState(this);
        if (networkState == TDevice.NETTYPE_NONE) {
            mWebView.setVisibility(View.GONE);
            mErrorDesc.setText(R.string.no_network);
            mWebErrorRoot.setVisibility(View.VISIBLE);

        } else if (StringUtils.isEmpty(url)) {
            mWebView.setVisibility(View.GONE);
            mWebErrorRoot.setVisibility(View.VISIBLE);
            mWebError.setImageResource(R.drawable.system_no_function);
        } else {
            if (!SonicEngine.isGetInstanceAllowed()) {
                SonicEngine.createInstance(new SonicRuntimeImpl(getApplication()), new SonicConfig.Builder().build());
            }
            SonicSessionClientImpl sonicSessionClient = null;
            SonicSessionConfig.Builder sessionConfigBuilder = new SonicSessionConfig.Builder();
            sessionConfigBuilder.setSupportLocalServer(true);

            sonicSession = SonicEngine.getInstance().createSession(url, sessionConfigBuilder.build());
            if (null != sonicSession) {
                sonicSession.bindClient(sonicSessionClient = new SonicSessionClientImpl());
            } else {
                // this only happen when a same sonic session is already running,
                // u can comment following codes to feedback as a default mode.
                // throw new UnknownError("create session fail!");
//                Toast.makeText(this, "create sonic session fail!", Toast.LENGTH_LONG).show();
            }
            mWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
//                    progress.setProgress(newProgress);
//                    if(newProgress == 100){
//                        progress.setVisibility(View.GONE);
//                    }
                }


                //扩展支持alert事件
                @Override
                public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                    final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(ViewHelpAndFeedbackActivity.this);
                    builder.withTitle("提示")
                        .withMessage(message)
                        .withEffect(Effectstype.Newspager)
                        .withButton1Text("取消")
                        .withButton2Text("确定")
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                builder.dismiss();
                            }
                        }).setButton2Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            builder.dismiss();
                        }
                    }).show();

//                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
//                builder.setTitle("提示").setMessage(message).setPositiveButton("确定", null);
//                builder.setCancelable(false);
//                builder.setIcon(R.drawable.icon_60);
//                AlertDialog dialog = builder.create();
//                dialog.show();
                    result.confirm();
                    return true;
                }

                //扩展浏览器上传文件
                //3.0++版本
                public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                    openFileChooserImpl(uploadMsg);
                }

                public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                    openFileChooserImpl(uploadMsg);
                }

                // For Android > 5.0
                public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg, WebChromeClient.FileChooserParams fileChooserParams) {
                    openFileChooserImplForAndroid5(uploadMsg);
                    return true;
                }
            });


            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (sonicSession != null) {
                        sonicSession.getSessionClient().pageFinish(url);
                    }
                }

                @TargetApi(21)
                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
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
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    view.setVisibility(View.GONE);
                    mWebErrorRoot.setVisibility(View.VISIBLE);
                    //  view.loadUrl("file:///android_asset/error.html");
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    view.setVisibility(View.GONE);
                    mWebErrorRoot.setVisibility(View.VISIBLE);
                    //  L.e("加载失败了");
                    //  view.loadUrl("file:///android_asset/error.html");
//                    L.i("出错了");
//                    String errorHtml = "<html><body><h1>页面出错了!</h1></body></html>";
//                    view.loadData(errorHtml,"text/html", "UTF-8");
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
            });

            WebSettings webSettings = mWebView.getSettings();

            // add java script interface
            // note:if api level lower than 17(android 4.2), addJavascriptInterface has security
            // issue, please use x5 or see https://developer.android.com/reference/android/webkit/
            // WebView.html#addJavascriptInterface(java.lang.Object, java.lang.String)
            webSettings.setJavaScriptEnabled(true);
            mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
            // intent.putExtra(SonicJavaScriptInterface.PARAM_LOAD_URL_TIME, System.currentTimeMillis());
            //webView.addJavascriptInterface(new SonicJavaScriptInterface(sonicSessionClient, intent), "sonic");

            // init webview settings
            webSettings.setAllowContentAccess(true);
            webSettings.setDatabaseEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setAppCacheEnabled(true);
            webSettings.setSavePassword(false);
            webSettings.setSaveFormData(false);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);


            // webview is ready now, just tell session client to bind
            if (sonicSessionClient != null) {
                sonicSessionClient.bindWebView(mWebView);
                sonicSessionClient.clientReady();
            } else { // default mode
                mWebView.loadUrl(url);
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
                // parserMethod(method);
                break;
        }
    }

    private void hideToolbar() {
        if (mToolbar != null && mToolbar.getVisibility() != View.GONE) {
            mToolbar.setVisibility(View.GONE);

        }
    }

    private void showToolbar() {
        if (mToolbar != null && mToolbar.getVisibility() != View.VISIBLE) {
            mToolbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.removeAllViews();
        mWebView.destroy();
        mWebView = null;
    }
}
