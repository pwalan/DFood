package github.com.pwalan.dfood.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.tencent.download.Downloader;
import com.tencent.download.core.DownloadResult;
import com.tencent.upload.Const;
import com.tencent.upload.UploadManager;
import com.tencent.upload.task.ITask;
import com.tencent.upload.task.IUploadTaskListener;
import com.tencent.upload.task.data.FileInfo;
import com.tencent.upload.task.impl.PhotoUploadTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class QCloud {
    //本地广播,通知上传完成
    static LocalBroadcastManager localBroadcastManager;
    //腾讯云上传管理类
    static UploadManager photoUploadMgr;
    //下载管理类
    static Downloader mDownloader = null;

    static String bucket;
    static String sign;
    static String response;
    public static String resultUrl;
    public static Bitmap bmp;

    private static Handler mMainHandler = null;

    /**
     * 上传下载初始化
     * @param con 上下文
     */
    public static void init(Context con){
        localBroadcastManager=LocalBroadcastManager.getInstance(con.getApplicationContext());
        resultUrl="http://pwalan-10035979.image.myqcloud.com/test_fileId_3119a3d1-b799-4400-b65e-48c92ba7aebd";
        bucket="pwalan";
        //获取APP签名
        getUploadImageSign("http://pwalan.cn/QCloudServer/getSign");
        // 实例化Photo业务上传管理类
        photoUploadMgr = new UploadManager(con, "10035979",
                Const.FileType.Photo, "qcloudphoto");

        //实例化下载管理类
        mMainHandler = new Handler(Looper.getMainLooper());
        mDownloader = new Downloader(con, "Downloader","10035979");
    }

    /**
     * 上传图片
     * @param picPath 图片地址
     * @param con 上下文（用来显示结果）
     */
    public static void UploadPic(String picPath, final Context con){
        PhotoUploadTask task = new PhotoUploadTask(picPath,
                new IUploadTaskListener() {
                    @Override
                    public void onUploadSucceed(final FileInfo result) {
                        Log.i("Demo", "upload succeed: " + result.url);
                        resultUrl=result.url;
                        Toast.makeText(con,"图片上传成功！",Toast.LENGTH_SHORT).show();
                        //发出广播
                        localBroadcastManager.sendBroadcast(new Intent("github.com.pwalan.dfood.LOCAL_BROADCAST"));
                    }
                    @Override
                    public void onUploadStateChange(ITask.TaskState state) {
                    }
                    @Override
                    public void onUploadProgress(long totalSize, long sendSize){
                        long p = (long) ((sendSize * 100) / (totalSize * 1.0f));
                        Log.i("Demo", "上传进度: " + p + "%");
                    }
                    @Override
                    public void onUploadFailed(final int errorCode, final String errorMsg){
                        Log.i("Demo", "上传结果:失败! ret:" + errorCode + " msg:" + errorMsg);
                        Toast.makeText(con,"上传失败！",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        task.setBucket(bucket); // 设置 Bucket(可选)
        task.setFileId("test_fileId_" + UUID.randomUUID()); // 为图片自定义 FileID(可选)
        task.setAuth(sign);
        Toast.makeText(con,"图片上传中，请稍后...",Toast.LENGTH_SHORT).show();
        photoUploadMgr.upload(task); // 开始上传
    }

    /**
     * 下载图片
     * @param imgUrl
     */
    public static void downloadPic(String imgUrl){
        mDownloader.download(imgUrl,new Downloader.DownloadListener() {

            @Override
            public void onDownloadSucceed(String arg0, final DownloadResult arg1) {
                // TODO Auto-generated method stub

                Log.i("dfood",  "下载成功: " + arg1.getPath());

                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        //Toast.makeText(con, "图片下载成功!", Toast.LENGTH_SHORT).show();
                        String file_path = arg1.getPath();
                        try
                        {
                            bmp = Utils.decodeSampledBitmap(file_path, 2);
                            //发出广播
                            localBroadcastManager.sendBroadcast(new Intent("github.com.pwalan.dfood.LOCAL_BROADCAST"));
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });

            }

            @Override
            public void onDownloadProgress(String url, long totalSize, float progress) {
                // TODO Auto-generated method stub
                long nProgress = (int) (progress * 100);
                Log.i("dfood", "下载进度: " + nProgress + "%");


            }

            @Override
            public void onDownloadFailed(String url, DownloadResult result) {
                // TODO Auto-generated method stub
                Log.i("dfood", "下载失败: " + result. getErrorCode());
            }

            @Override
            public void onDownloadCanceled(String url) {
                // TODO Auto-generated method stub
                Log.i("dfood", "下载任务被取消");
            }
        });
    }

    // 获取app 的签名
    private static void getUploadImageSign(final String s) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    Log.d("Demo", "Start getSign");
                    URL url = new URL(s);
                    HttpURLConnection urlConnection = (HttpURLConnection) url
                            .openConnection();
                    InputStreamReader in = new InputStreamReader(urlConnection
                            .getInputStream());
                    BufferedReader buffer = new BufferedReader(in);
                    String inpuLine = null;
                    while ((inpuLine = buffer.readLine()) != null) {
                        response = inpuLine + "\n";
                    }
                    JSONObject jsonData = new JSONObject(response);
                    sign = jsonData.getString("sign");
                    Log.i("Sign", "SIGN: " +sign);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }).start();
    }
}
