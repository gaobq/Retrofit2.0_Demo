package com.heybik.gaobiaoqing.retrofit20demo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.heybik.gaobiaoqing.retrofit20demo.adapter.GitHubRepoAdapter;
import com.heybik.gaobiaoqing.retrofit20demo.model.Repo;
import com.heybik.gaobiaoqing.retrofit20demo.service.GitHubService;

import retrofit.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class GithubActivity extends AppCompatActivity {

    final int PER_PAGE = 10;
    Retrofit mRetrofit;
    GitHubService mGitHubSrv;

    StaggeredGridLayoutManager staggeredGridLayoutManager;
    SwipeRefreshLayout mSwipeLayout;

    RecyclerView mRecyclerView;

    List<Repo> androidRepos =  new ArrayList<Repo>();
    GitHubRepoAdapter mRepoAdapter;
    boolean isRefreshing;
    boolean isLoadingMore;
    int currPage  = 1;
    boolean isFirstPage ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSwipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mRepoAdapter = new GitHubRepoAdapter(this, androidRepos) ;
        mRecyclerView.setAdapter(mRepoAdapter) ;
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int[] positions = new int[staggeredGridLayoutManager.getSpanCount()];
                staggeredGridLayoutManager.findLastVisibleItemPositions(positions);
                int lastItem = positions[0];
                for (int pos:positions){
                    if(lastItem < pos){
                        lastItem = pos;
                    }
                }
                int totalItemCount = mRepoAdapter.getItemCount();
                Log.d("demo", "lastItem =="+lastItem+",totalItemCount=="+totalItemCount);
                if(lastItem >= totalItemCount -2 && dy >0){
                    Log.d("demo", "do load more");
                    //do load
                    if(isLoadingMore){
                        Toast.makeText(GithubActivity.this,"I am loading",Toast.LENGTH_SHORT).show();
                    }else{
                        currPage ++;
                        isLoadingMore = true;
                        loadByPage(currPage);
                    }

                }
            }
        });

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("demo", "do refresh");
                if (isRefreshing){
                    Toast.makeText(GithubActivity.this,"I am refreshing",Toast.LENGTH_SHORT).show();
                }else{
                    isRefreshing = true;
                    currPage  = 1;
                    loadByPage(currPage);
                }

            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                doRequest();
            }
        });
        fab.hide();

    }

    @Override
    protected void onResume() {
        if (mRetrofit == null) {
            initRetrofit();
        }
        if (mGitHubSrv == null) {
            initService();
        }
        super.onResume();
        isFirstPage = true;
        loadByPage(currPage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
           // Intent intent = new Intent(this,HttpBinActivity.class);
           // this.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initRetrofit() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void initService() {
        mGitHubSrv = mRetrofit.create(GitHubService.class);
    }

    private void doRequest() {
        Call<List<Repo>> repos = mGitHubSrv.listRepos("square");
        repos.enqueue(new Callback<List<Repo>>() {
            @Override
            public void onFailure(Throwable t) {
                //fail
                t.printStackTrace();
            }

            @Override
            public void onResponse(Response<List<Repo>> response, Retrofit retrofit) {
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
                List<Repo> mrepos = response.body();
                if (mrepos == null) return;
                Log.d("demo", "repo's size =" + mrepos.size());

                for (int i=0;i<mrepos.size();i++){
                    Repo tmp = mrepos.get(i);
                    String lang = tmp.getLanguage();
                    if (!TextUtils.isEmpty(lang) && lang.equals("Java")){
                        androidRepos.add(tmp);
                    }
                }
                Log.d("demo", "and-repo's size =" + androidRepos.size());
                mRepoAdapter.notifyDataSetChanged();
            }

        });
    }

    private void loadByPage(int pageNum) {
        //in order to ensure the param's sequence ,you should use a link map
        Map<String,String> params = new LinkedHashMap<String,String>();
        params.put("page",""+pageNum);
        params.put("per_page", "" + PER_PAGE);

        Call<List<Repo>> repos = mGitHubSrv.listReposPerPage("square",params);
        repos.enqueue(new Callback<List<Repo>>() {
            @Override
            public void onFailure(Throwable t) {
                //fail
                t.printStackTrace();
            }

            @Override
            public void onResponse(Response<List<Repo>> response, Retrofit retrofit) {
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
                List<Repo> mrepos = response.body();
                if (mrepos == null) return;
                Log.d("demo", "repo's size =" + mrepos.size());
                if(isRefreshing){
                    androidRepos.clear();
                    androidRepos.addAll(mrepos);
                    isRefreshing = false;
                    mSwipeLayout.setRefreshing(false);
                }else if (isLoadingMore){
                    isLoadingMore = false;
                    androidRepos.addAll(mrepos);
                }else if (isFirstPage){
                    isFirstPage = false;
                    androidRepos.addAll(mrepos);
                }

                Log.d("demo", "and-repo's size =" + androidRepos.size());
                mRepoAdapter.notifyDataSetChanged();
            }

        });
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();


    }
}
