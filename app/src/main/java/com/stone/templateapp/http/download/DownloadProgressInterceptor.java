package com.stone.templateapp.http.download;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created By: sqq
 * Created Time: 17/6/12 下午1:39.
 */

public class DownloadProgressInterceptor implements Interceptor {
    private DownloadProgressListener progressListener;

    public DownloadProgressInterceptor(DownloadProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
                .body(new DownloadProgressResponseBody(originalResponse.body(), progressListener))
                .build();
    }
}
