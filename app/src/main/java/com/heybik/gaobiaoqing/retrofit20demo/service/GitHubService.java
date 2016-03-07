package com.heybik.gaobiaoqing.retrofit20demo.service;

import com.heybik.gaobiaoqing.retrofit20demo.model.Repo;

import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * Created by gaobiaoqing on 16-1-12.
 */
public interface GitHubService {

    @GET("users/{user}/repos")
    Call<List<Repo>> listRepos(@Path("user") String user);

    @GET("users/{user}/repos")
    Call<List<Repo>> listReposPerPage(@Path("user")String user, @QueryMap Map<String,String> options);
}
