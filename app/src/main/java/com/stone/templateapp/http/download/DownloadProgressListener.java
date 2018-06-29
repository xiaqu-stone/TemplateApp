package com.stone.templateapp.http.download;

/**
 * Created By: sqq
 * Created Time: 17/6/12 下午1:40.
 */
public interface DownloadProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
