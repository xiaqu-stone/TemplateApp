//package com.stone.templateapp.http.download;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Build;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.os.SystemClock;
//import android.support.v4.app.NotificationCompat;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.jiedai.loan.R;
//import com.jiedai.loan.ui.dialog.DialogMD;
//import com.jiedai.loan.ui.view.HorizontalProgressBarWithNumber;
//import com.jiedai.loan.util.InstallApkUtils;
//import com.jiedai.loan.util.Logs;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//
//import io.reactivex.Observer;
//import io.reactivex.annotations.NonNull;
//import io.reactivex.disposables.Disposable;
//
///**
// * Created by Wen
// * Email: wenpeng@qianshengqian.com
// * Date: 16/5/31
// * Description：下载更新服务类
// */
//public class UpdateDownloadUtil {
//
//    // 是否强更新
//    private boolean isForceUpdate;
//
//    private int requestCode = (int) SystemClock.uptimeMillis();
//
//    //下载地址
//    private String mDownloadUrl;
//
//    private Activity mContext;
//
//    //notification
//    private NotificationManager mNotificationManager;
//    private NotificationCompat.Builder mNotificationCompatBuilder;
//
//    private File destFile = null;
//
//    private String versionName;
//
//    private static final int DOWNLOADING = 3;
//
//    public UpdateDownloadUtil(Activity context, String downloadUrl, String versionName, boolean isForce) {
//        mContext = context;
//        mNotificationManager = (NotificationManager) mContext.getSystemService(Activity.NOTIFICATION_SERVICE);
//        initChannels();
//        mNotificationCompatBuilder = new NotificationCompat.Builder(mContext, "default");
//        mDownloadUrl = downloadUrl;
//        isForceUpdate = isForce;
//        this.versionName = versionName;
//    }
//
//    /**
//     * 兼容8.0发送通知
//     */
//    private void initChannels() {
//        if (Build.VERSION.SDK_INT < 26) {
//            return;
//        }
//        NotificationChannel channel = new NotificationChannel("default",
//                "Channel name",
//                NotificationManager.IMPORTANCE_DEFAULT);
//        channel.setDescription("Channel description");
//        mNotificationManager.createNotificationChannel(channel);
//
//    }
//
//    private static final String TAG = "UpdateDownloadUtil";
//
//    /**
//     * 检测下载目录以及apk是否已下载，每次apk版本的不同都以后台返回的版本号做区别
//     *
//     * false：阻断后续下载；true：已存在apk，不再执行下载
//     */
//    public void checkApkIfDownload() {
//        //获取私有外部存储，可以避免关于的权限的适配问题
//        File destFileDir = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
//        if (destFileDir == null) {//获取不到外部存储目录,使用内部存储
//            destFileDir = mContext.getFilesDir();
//        }
//
//        String fileName = String.format("ryl_%s.apk", versionName);
//        destFile = new File(destFileDir, fileName);
//        Logs.Companion.w(TAG, "checkDestinationFile: fileName = " + destFile.getAbsolutePath());
//        if (destFile.exists() && destFile.isFile() && InstallApkUtils.INSTANCE.checkApkFile(mContext, destFile.getAbsolutePath())) {//已存在
//            final DialogMD dialogMD = new DialogMD(mContext, R.string.apk_already_exists)
//                    .setBtnPositive(R.string.click_install)
//                    .setBtnNegative(R.string.download_again)
//                    .setNegativeListener((dialog, which) -> {
//                        //noinspection ResultOfMethodCallIgnored
//                        destFile.delete();
//                        startDownload();
//                    })
//                    .setCancelable(!isForceUpdate)
//                    .show();
//            dialogMD.getButton(-1).setOnClickListener(v -> InstallApkUtils.INSTANCE.installAll(mContext, destFile));
//
//        } else {//执行下载
//            //删除之前版本的apk
//            deleteOtherApks(destFileDir);
//            startDownload();
//        }
//    }
//
//    private void deleteOtherApks(File dir) {
//        File[] listFiles = dir.listFiles();
//        for (File file :
//                listFiles) {
//            if (file.isFile() && file.getName().endsWith(".apk")) {
//                //noinspection ResultOfMethodCallIgnored
//                file.delete();
//            }
//        }
//    }
//
//    /**
//     * 执行下载
//     */
//    private void startDownload() {
//
//        DownloadProgressListener listener = new DownloadProgressListener() {
//            int curProgress;
//
//            @Override
//            public void update(long bytesRead, long contentLength, boolean done) {
//                int showProgress = (int) (bytesRead * 100 / contentLength);
//                if (showProgress > curProgress) {
//                    Logs.Companion.d(TAG, "update() called with: " + "bytesRead = [" + bytesRead + "], contentLength = [" + contentLength + "], done = [" + done + "]");
//                    Message msg = mHandler.obtainMessage();
//                    msg.what = DOWNLOADING;
//                    msg.arg1 = showProgress;
//                    mHandler.sendMessage(msg);
//                    curProgress = showProgress;
//                }
//
//                if (done) Logs.Companion.e(TAG, "update: done");
//            }
//        };
//
//        Observer<InputStream> observer = new Observer<InputStream>() {
//            @Override
//            public void onSubscribe(@NonNull Disposable d) {
//            }
//
//            @Override
//            public void onNext(@NonNull InputStream inputStream) {
//                Logs.Companion.i(TAG, "onNext() called with: " + "inputStream = [" + inputStream + "]");
//                if (inputStream != null) {
//                    try {
//                        inputStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onError(@NonNull Throwable e) {
//                e.printStackTrace();
//                //下载失败
//                Toast.makeText(mContext, mContext.getResources().getString(R.string.downlod_fauile), Toast.LENGTH_SHORT).show();
//                clear();
//            }
//
//            @Override
//            public void onComplete() {
//                Logs.Companion.e(TAG, "onComplete: download");
//                horizontalProgressBarWithNumber.setProgress(100);
//                mProgressDialog.getButton(-1).setVisibility(View.VISIBLE);
//                //下载成功
//                updateFinishNotification();
//                sent();
//                install();
//            }
//        };
//
//        createNotification();
//        createProgressDialog();
//
//        // 执行下载请求
//        new DownloadApi(mDownloadUrl, destFile, listener, observer).startDownload();
//
//    }
//
//    @SuppressLint("HandlerLeak")
//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case DOWNLOADING:
//                    //下载中
//                    int showProgress = msg.arg1;
//
//                    horizontalProgressBarWithNumber.setProgress(showProgress);
//
//                    mNotificationCompatBuilder.setContentTitle(mContext.getResources().getString(R.string.apk_download));
//                    mNotificationCompatBuilder.setContentText(mContext.getResources().getString(R.string.apk_have_download) + showProgress + "%");
//                    mNotificationCompatBuilder.setProgress(100, showProgress, false);
//                    sent();
//                    break;
//                default:
//                    break;
//            }
//        }
//
//    };
//
//    private DialogMD mProgressDialog;
//
//    private HorizontalProgressBarWithNumber horizontalProgressBarWithNumber;
////    private MDButton btn_install;
//
//    /**
//     * 创建 progress - dialog
//     */
//    private void createProgressDialog() {
//
//        mProgressDialog = new DialogMD(mContext)
//                .setTitle(R.string.apk_is_update)
//                .setCustomView(R.layout.dlg_update_progress)
//                .setBtnPositive(R.string.click_install)
////                .setPositiveListener(new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
////                        install();
////                    }
////                })
//                .setCancelable(!isForceUpdate).show();
//
//        Button button = mProgressDialog.getButton(-1);
//        button.setOnClickListener(v -> install());
//        button.setVisibility(View.GONE);
//
//        View view = mProgressDialog.getCustomView();
//        horizontalProgressBarWithNumber = view.findViewById(R.id.progress_bar);
//        horizontalProgressBarWithNumber.setMax(100);
//
//    }
//
//    private void install() {
//        if (InstallApkUtils.INSTANCE.checkApkFile(mContext, destFile.getAbsolutePath())) {
//            InstallApkUtils.INSTANCE.installAll(mContext, destFile);
//        }
//    }
//
//    /**
//     * 构建progress Notification
//     */
//    private void createNotification() {
//        Intent intent = new Intent();
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent rightPendIntent = PendingIntent.getActivity(mContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        int smallIcon = android.R.drawable.stat_sys_download;
//        int bigIcon = R.mipmap.ic_launcher;
//        String ticker = mContext.getResources().getString(R.string.updating);
//        setCompatBuilder(rightPendIntent, smallIcon, bigIcon, ticker, mContext.getResources().getString(R.string.apk_update), mContext.getResources().getString(R.string.downloading), false, false, false);
//    }
//
//    /**
//     * 下载完之后 更新 notification 信息
//     */
//    private void updateFinishNotification() {
//        Intent intent = InstallApkUtils.INSTANCE.getInstallIntent(mContext, destFile);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        mNotificationCompatBuilder.setTicker(mContext.getResources().getString(R.string.downlod_finish));
//        mNotificationCompatBuilder.setContentTitle(mContext.getResources().getString(R.string.click_install));
//        mNotificationCompatBuilder.setContentText(mContext.getResources().getString(R.string.apk_down_finish));
//        mNotificationCompatBuilder.setProgress(0, 0, false);
//        mNotificationCompatBuilder.setContentIntent(pendingIntent);
//    }
//
//    /**
//     * 通知栏构造
//     */
//    private void setCompatBuilder(PendingIntent pendingIntent, int smallIcon, int bigIcon, String ticker,
//                                  String title, String content, boolean sound, boolean vibrate, boolean lights) {
//
//        mNotificationCompatBuilder.setContentIntent(pendingIntent);// 该通知要启动的Intent
//        mNotificationCompatBuilder.setSmallIcon(smallIcon);// 设置顶部状态栏的小图标
//
//        BitmapFactory.Options opt = new BitmapFactory.Options();
//        opt.inPreferredConfig = Bitmap.Config.RGB_565;
//        opt.inPurgeable = true;
//        opt.inInputShareable = true;
//
////        InputStream is = getResources().openRawResource(bigIcon);
////        Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
//
//        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), bigIcon);
//
//        mNotificationCompatBuilder.setLargeIcon(bitmap);
//        mNotificationCompatBuilder.setTicker(ticker);// 在顶部状态栏中的提示信息
//
//        mNotificationCompatBuilder.setContentTitle(title);// 设置通知中心的标题
//        mNotificationCompatBuilder.setContentText(content);// 设置通知中心中的内容
//        mNotificationCompatBuilder.setWhen(System.currentTimeMillis());
//
//		/*
//         * 将AutoCancel设为true后，当你点击通知栏的notification后，它会自动被取消消失,
//		 * 不设置的话点击消息后也不清除，但可以滑动删除
//		 */
//        mNotificationCompatBuilder.setAutoCancel(true);
//        // 将Ongoing设为true 那么notification将不能滑动删除
//        // notifyBuilder.setOngoing(true);
//        /*
//         * 从Android4.1开始，可以通过以下方法，设置notification的优先级，
//		 * 优先级越高的，通知排的越靠前，优先级低的，不会在手机最顶部的状态栏显示图标
//		 */
//        mNotificationCompatBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
//        int defaults = 0;
//
//        if (sound) {
//            defaults |= Notification.DEFAULT_SOUND;
//        }
//        if (vibrate) {
//            defaults |= Notification.DEFAULT_VIBRATE;
//        }
//        if (lights) {
//            defaults |= Notification.DEFAULT_LIGHTS;
//        }
//
//        mNotificationCompatBuilder.setDefaults(defaults);
//    }
//
//    /**
//     * 发送通知
//     */
//    private void sent() {
//        Notification mNotification = mNotificationCompatBuilder.build();
//        // 发送该通知
//        mNotificationManager.notify(7, mNotification);
//    }
//
//    /**
//     * 清除通知
//     */
//    private void clear() {
//        mNotificationManager.cancelAll();
//    }
//}
