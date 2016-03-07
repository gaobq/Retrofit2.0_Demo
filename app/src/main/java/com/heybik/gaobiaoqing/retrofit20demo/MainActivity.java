package com.heybik.gaobiaoqing.retrofit20demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView githubReq ;
    TextView httpbinReq ;

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.github_demo:
                    Intent itnent1 = new Intent(MainActivity.this,GithubActivity.class);
                    startActivity(itnent1);
                    break;
                case R.id.httpbin_demo:
                    Intent itnent2 = new Intent(MainActivity.this,HttpBinActivity.class);
                    startActivity(itnent2);
                    break;

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.hide();

        githubReq = (TextView) this.findViewById(R.id.github_demo);
        httpbinReq = (TextView) this.findViewById(R.id.httpbin_demo);

        githubReq.setOnClickListener(onClickListener);
        httpbinReq.setOnClickListener(onClickListener);

    }

}
