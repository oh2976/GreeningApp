package com.example.greeningapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
public class SearchActivity extends AppCompatActivity {

    //    private FirebaseDatabase database;
    RecyclerView recview;
    SearchAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목 삭제.
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        recview = findViewById(R.id.searchRecyclerView);
        recview.setLayoutManager(new LinearLayoutManager(this));

        //뒤로가기시 리사이클러뷰 에러 나는 것 수정

//        RecycleError layoutManager = new RecycleError(this);
//        recview.setLayoutManager(layoutManager);

        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Product").orderByChild("pname"), Product.class)
                        .build();

        adapter = new SearchAdapter(options);
        recview.setAdapter(adapter);

    }

    @Override    protected void onStart() {
        super.onStart();
        //어뎁터 갱신 시작
        adapter.startListening();
    }



    @Override
    protected void onStop() {
        super.onStop();
        //어뎁터 갱신 중지
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchmenu,menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView)item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                processsearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                processsearch(s);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) { //뒤로가기
            onBackPressed();
            return true;
        }
        else if (itemId == R.id.action_cart) {
            Intent intent = new Intent(SearchActivity.this, MyPageActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void processsearch(String s) {
        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Product").orderByChild("psearch").startAt(s).endAt(s + "\uf8ff"), Product.class)
                        .build();

        adapter = new SearchAdapter(options);
        adapter.startListening();
        recview.setAdapter(adapter);
    }

}