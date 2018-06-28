package com.lensim.fingerchat.data.response;

import android.text.TextUtils;
import com.lensim.fingerchat.data.help_class.IUploadListener;
import io.reactivex.annotations.Nullable;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LL130386 on 2017/12/22.
 */

public class ResponseUploadImage extends ResponseBody {

  ResponseBody responseBody;
  private BufferedSource bufferedSource;
  private final IUploadListener listener;


  public ResponseUploadImage(ResponseBody responseBody, IUploadListener l) {
    this.responseBody = responseBody;
    listener = l;
  }

  @Nullable
  @Override
  public MediaType contentType() {
    return responseBody.contentType();
  }

  @Override
  public long contentLength() {
    return responseBody.contentLength();
  }

  @Override
  public BufferedSource source() {
    if (bufferedSource == null) {
      bufferedSource = Okio.buffer(source(responseBody.source()));
    }
    return bufferedSource;
  }

  private Source source(Source source) {
    return new ForwardingSource(source) {
      long totalBytesRead = 0L;

      @Override
      public long read(Buffer sink, long byteCount) throws IOException {
        long bytesRead = super.read(sink, byteCount);
        // read() returns the number of bytes read, or -1 if this source is exhausted.
        totalBytesRead += bytesRead != -1 ? bytesRead : 0;
        listener.onProgress((int) (responseBody.contentLength() * 100 / totalBytesRead));
        return bytesRead;
      }
    };
  }

  public String getUrl() {
    try {
      String body = string();
      if (!TextUtils.isEmpty(body)) {
        JSONObject object = new JSONObject(body);
        if (object != null && object.has("value")) {
          return object.optString("value");
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return "";
  }
}
