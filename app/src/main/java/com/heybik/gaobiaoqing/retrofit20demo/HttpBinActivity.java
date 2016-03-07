package com.heybik.gaobiaoqing.retrofit20demo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.io.Files;
import com.heybik.gaobiaoqing.retrofit20demo.model.HttpBinResponse;
import com.heybik.gaobiaoqing.retrofit20demo.model.JsonData;
import com.heybik.gaobiaoqing.retrofit20demo.service.HttpBinService;
import com.squareup.okhttp.ResponseBody;

import retrofit.Callback;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by gaobiaoqing on 16-2-25.
 */
public class HttpBinActivity extends AppCompatActivity {

    public static final String API_URL = "http://httpbin.org";
    Retrofit mRetrofit;
    HttpBinService mHttpbinSrv;

    Button httppost;

    Button httpimage;

    Button httppostjson;

    TextView retTxt ;
    ImageView retImg ;


    long lastId  = -1 ;
    Handler mWorkHandler;
    HandlerThread handlerThread = new HandlerThread("getImage");

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            doHttpbinRequest(view);
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_httpbin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        httppost = (Button) this.findViewById(R.id.http_post);
        httpimage = (Button) this.findViewById(R.id.http_image);
        httppostjson = (Button) this.findViewById(R.id.http_post_json);
        httppost.setOnClickListener(clickListener);
        httpimage.setOnClickListener(clickListener);
        httppostjson.setOnClickListener(clickListener);

        retTxt = (TextView) this.findViewById(R.id.http_ret_txt);
        retImg = (ImageView) this.findViewById(R.id.http_ret_img);

        handlerThread.start();
        mWorkHandler = new Handler(handlerThread.getLooper());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mRetrofit == null) {
            initRetrofit();
        }
        if (mHttpbinSrv == null) {
            initService();
        }

    }

    private void doHttpbinRequest(View view) {
        //initRetrofitByView(view);
        switch (view.getId()) {
            case R.id.http_post:
                Call<HttpBinResponse> postinfo = mHttpbinSrv.httpbinPost("textstring", "123");
                postinfo.enqueue(initCallBack());
                break;
            case R.id.http_image:
                Call<ResponseBody> imgget = mHttpbinSrv.httpbinImage();
                imgget.enqueue(initImageCallBack());

                break;
            case R.id.http_post_json:
                Call<HttpBinResponse> jsoninfo = mHttpbinSrv.httpbinPostJson(new JsonData("New York", 9527, "david"));
                jsoninfo.enqueue(initCallBack());
                break;
        }
    }

    private void initRetrofit() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mRetrofit.client().setReadTimeout(10, TimeUnit.SECONDS);
        mRetrofit.client().setWriteTimeout(10, TimeUnit.SECONDS);
        mRetrofit.client().setConnectTimeout(10, TimeUnit.SECONDS);

    }

    private void initRetrofitByView(View view) {
        if(lastId == view.getId()){
            return ;
        }else{
            lastId = view.getId();
        }
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(API_URL);
        switch (view.getId()){
            case R.id.http_post:
            case R.id.http_post_json:
                builder.addConverterFactory(GsonConverterFactory.create());
                break;
            case R.id.http_image:
                builder.addConverterFactory(GsonConverterFactory.create());
                break;

        }
        mRetrofit = builder.build();
        initService();
    }

    private void initService() {
        mHttpbinSrv = mRetrofit.create(HttpBinService.class);
    }

    private Callback<HttpBinResponse> initCallBack() {
        Callback<HttpBinResponse> postCallBack = new Callback<HttpBinResponse>() {
            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onResponse(Response<HttpBinResponse> response, Retrofit retrofit) {
                // http response status code + headers
                System.out.println("Response status code: " + response.code());

                // isSuccess is true if response code => 200 and <= 300
                if (!response.isSuccess()) {
                    // print response body if unsuccessful
                    try {
                        String err = response.errorBody().string();
                        System.out.println(err);
                    } catch (IOException e) {
                        // do nothing
                        e.printStackTrace();
                    }
                    return;
                }
                HttpBinResponse decodedResponse = response.body();
                if (decodedResponse == null) return;
                System.out.println("Response (contains request infos):");
                final StringBuffer retBuf = new StringBuffer();
                retBuf.append("- url:        ").append(decodedResponse.getUrl()).append("\n");
                retBuf.append("- ip:         ").append(decodedResponse.getOrigin()).append("\n");
                retBuf.append("- headers:    ").append(decodedResponse.getHeaders()).append("\n");
                retBuf.append("- args:       ").append(decodedResponse.getArgs()).append("\n");
                retBuf.append("- form params:").append(decodedResponse.getForm()).append("\n");
                retBuf.append("- json params:").append(decodedResponse.getJson()).append("\n");

                HttpBinActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        retImg.setVisibility(View.GONE);
                        retTxt.setVisibility(View.VISIBLE);
                        retTxt.setText(retBuf.toString());
                    }
                });

                System.out.println("- url:         " + decodedResponse.getUrl());
                System.out.println("- ip:          " + decodedResponse.getOrigin());
                System.out.println("- headers:     " + decodedResponse.getHeaders());
                System.out.println("- args:        " + decodedResponse.getArgs());
                System.out.println("- form params: " + decodedResponse.getForm());
                System.out.println("- json params: " + decodedResponse.getJson());
            }
        };

        return postCallBack;
    }

    private Callback<ResponseBody> initImageCallBack() {
        Callback<ResponseBody> imageCallBack = new Callback<ResponseBody>() {
            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onResponse(final Response<ResponseBody> response, Retrofit retrofit) {
                // http response status code + headers
                System.out.println("Response status code: " + response.code());

                // isSuccess is true if response code => 200 and <= 300
                if (!response.isSuccess()) {
                    // print response body if unsuccessful
                    try {
                        String err = response.errorBody().string();
                        System.out.println(err);
                    } catch (IOException e) {
                        // do nothing
                        e.printStackTrace();
                    }
                    return;
                }
                mWorkHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String fileName = "http-img.png";

                        File path = Environment.getExternalStorageDirectory();
                        File imgFile = new File(path, fileName);
                        try {
                            imgFile.createNewFile();
                            Files.asByteSink(imgFile).write(response.body().bytes());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                HttpBinActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String fileName = "http-img.png";

                        File path = Environment.getExternalStorageDirectory();
                        File imgFile = new File(path, fileName);
                        if (imgFile.exists() && imgFile.length() >0) {
                            Bitmap bm = BitmapFactory.decodeFile(imgFile.getPath());
                            retImg.setVisibility(View.VISIBLE);
                            retTxt.setVisibility(View.GONE);
                            retImg.setImageBitmap(bm);
                        }
                    }
                });

                /*
                try {

                    InputStream input = response.body().byteStream();
                    File path = Environment.getExternalStorageDirectory();
                    File imgFile = new File(path,fileName);

                    Log.d("HttpBin", "img path:" + imgFile.getPath());

                    BufferedOutputStream oos = new BufferedOutputStream(new FileOutputStream(imgFile));
                    byte[] buff = new byte[1024];
                    long total = 0;
                    int len = 0;
                    while((len = input.read(buff))!= -1){
                        oos.write(buff,0,len);
                        total +=len;
                    }
                    oos.flush();
                    oos.close();
                    input.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }catch (IOException e1){
                    e1.printStackTrace();;
                }
                */

            }
        };

        return imageCallBack;
    }


}
