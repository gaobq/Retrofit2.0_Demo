package com.heybik.gaobiaoqing.retrofit20demo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.heybik.gaobiaoqing.retrofit20demo.R;
import com.heybik.gaobiaoqing.retrofit20demo.model.Repo;

import java.util.List;

/**
 * Created by gaobiaoqing on 16-1-25.
 */
public class GitHubRepoAdapter extends RecyclerView.Adapter<GitHubRepoAdapter.RepoHolder>{

    List<Repo> repoList;
    private  LayoutInflater mLayoutInflater;
    private  Context mContext;

    public GitHubRepoAdapter(Context mContext, List<Repo> repoList) {
        this.mContext = mContext;
        this.repoList = repoList;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RepoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RepoHolder(mLayoutInflater.inflate(R.layout.gitrepo_item,parent,false));
    }

    @Override
    public void onBindViewHolder(RepoHolder holder, int position) {
        holder.mDescription.setText(repoList.get(position).getDescription());
        holder.mLanguage.setText(repoList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return repoList ==null ?0:repoList.size();
    }

    public static class RepoHolder extends RecyclerView.ViewHolder{
        TextView mDescription;
        TextView mLanguage;
        public RepoHolder(View itemView) {
            super(itemView);
            mDescription = (TextView) itemView.findViewById(R.id.description);
            mLanguage = (TextView) itemView.findViewById(R.id.language);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("RepoHolder", "onClick--> position = " + getPosition());
                }
            });
        }
    }
}

