package com.example.bloggerapp;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView postsRv;
    private Button loadMorebtn;
    private EditText searchEt;
    private ImageButton searchBtn;

    private  String url = "";
    private String nextToken = "";
    private boolean isSearch = false;
    private ArrayList<ModolPost> postArrayList;
    private AdapterPost adapterPost;
    private ProgressDialog progressDialog;

    private static final String TAG = "MAIN_TAG";

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        actionBar.setTitle("ANSNEW TECH.");
        actionBar.setSubtitle("Posts");

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        postsRv = findViewById(R.id.postsRV);
        loadMorebtn= findViewById(R.id.loadMorebtn);
        searchEt = findViewById(R.id.searchEt);
        searchBtn = findViewById(R.id.searchBtn);

        progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Please wait....");


        postArrayList = new ArrayList<>();
        postArrayList.clear();
        loadPosts();

        loadMorebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String query = searchEt.getText().toString().trim();
                if (TextUtils.isEmpty(query)){
                    loadPosts();
                }
                else {
                    searchPosts(query);
                }
            }
        });


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextToken = "";
                url = "";

                postArrayList= new ArrayList<>();
                postArrayList.clear();


                String query = searchEt.getText().toString().trim();
                if (TextUtils.isEmpty(query)){
                    loadPosts();
                }
                else {
                    searchPosts(query);
                }
            }
        });

    }

    private void searchPosts(String query) {
        isSearch = false;
        Log.d(TAG,"loadPosts: isSearch: "+isSearch);

        progressDialog.show();

        if (nextToken.equals("")){
            Log.d(TAG,"searchPosts: Next Page token is empty , no more posts");
            url = "https://www.googleapis.com/blogger/v3/blogs/"
                    +Constants.BLOG_ID
                    +"/posts/search?q=" + query
                    +"&key="+Constants.API_KEY;
        }
        else if (nextToken.equals("end")){
            Log.d(TAG, "searchPosts: Next Token is empty/end,no more posts");
            Toast.makeText(this,"No more posts...", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }
        else {
            Log.d(TAG,"searchPosts: Next token:"+nextToken);
            url = "https://www.googleapis.com/blogger/v3/blogs/"
                    +Constants.BLOG_ID
                    +"/posts/search?q=" + query
                    +"&pageToken=" +nextToken
                    +"&key="+Constants.API_KEY;
        }
        Log.d(TAG,"searchPosts: URL: "+url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d(TAG, "onResponse:" + response);

                try {
                    JSONObject jsonObject  = new JSONObject(response);

                    try {
                        nextToken = jsonObject.getString("nextPageToken");
                        Log.d(TAG,"onResponse: NextPageToken: "+nextToken);
                    }
                    catch (Exception e){
                        Toast.makeText(MainActivity.this,"Reached end of page...", Toast.LENGTH_SHORT).show();
                        Log.d(TAG,"onResponse: Reached end of page..."+e.getMessage());
                        nextToken = "end";

                    }
                    JSONArray jsonArray =jsonObject.getJSONArray("items");
                    for (int i=0; i<jsonArray.length(); i++){
                        try {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String id = jsonObject1.getString("id");
                            String title = jsonObject1.getString("title");
                            String content = jsonObject1.getString("content");
                            String published = jsonObject1.getString("published");
                            String updated = jsonObject1.getString("updated");
                            String url = jsonObject1.getString("url");
                            String selfLink = jsonObject1.getString("selfLink");
                            String authorName = jsonObject1.getJSONObject("author").getString("displayName");
                            //String image = jsonObject1.getJSONObject("author").getString("image");


                            ModolPost modolPost = new ModolPost(""+authorName,
                                    ""+content,
                                    ""+id,
                                    ""+published,
                                    ""+selfLink,
                                    ""+title,
                                    ""+updated,
                                    ""+url);

                            postArrayList.add(modolPost);


                        }catch (Exception e){
                            Log.d(TAG,"onResponse: 1: "+e.getMessage());
                            Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();


                        }

                    }
                    adapterPost = new AdapterPost(MainActivity.this,postArrayList);

                    postsRv.setAdapter(adapterPost);
                    progressDialog.dismiss();
                }
                catch (Exception e){
                    Log.d(TAG,"onResponse: 2: "+e.getMessage());
                    Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"onErrorResponse:"+error.getMessage());
                Toast.makeText(MainActivity.this,""+error.getMessage(),Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void loadPosts() {

        isSearch = true;
        Log.d(TAG,"searchPosts: isSearch: "+isSearch);

        progressDialog.show();



        if (nextToken.equals("")){
            Log.d(TAG,"loadPosts: Next Page token is empty , no more posts");
            url = "https://www.googleapis.com/blogger/v3/blogs/"
                    +Constants.BLOG_ID
                    +"/posts?maxResults="+Constants.MAX_POST_RESULTS
                    +"&key="+Constants.API_KEY;
        }
        else if (nextToken.equals("end")){
            Log.d(TAG, "loadPosts: Next Token is empty/end,no more posts");
            Toast.makeText(this,"No more posts...", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }
        else {
            Log.d(TAG,"loadPosts: Next token:"+nextToken);
            url = "https://www.googleapis.com/blogger/v3/blogs/"
                    +Constants.BLOG_ID
                    +"/posts?maxResults="+Constants.MAX_POST_RESULTS
                    +"&pageToken=" +nextToken
                    +"&key="+Constants.API_KEY;
        }
        Log.d(TAG,"loadPosts: URL: "+url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d(TAG, "onResponse:" + response);

                try {
                    JSONObject jsonObject  = new JSONObject(response);

                    try {
                        nextToken = jsonObject.getString("nextPageToken");
                        Log.d(TAG,"onResponse: NextPageToken: "+nextToken);
                    }
                    catch (Exception e){
                        Toast.makeText(MainActivity.this,"Reached end of page...", Toast.LENGTH_SHORT).show();
                        Log.d(TAG,"onResponse: Reached end of page..."+e.getMessage());
                        nextToken = "end";

                    }
                    JSONArray jsonArray =jsonObject.getJSONArray("items");
                    for (int i=0; i<jsonArray.length(); i++){
                        try {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String id = jsonObject1.getString("id");
                            String title = jsonObject1.getString("title");
                            String content = jsonObject1.getString("content");
                            String published = jsonObject1.getString("published");
                            String updated = jsonObject1.getString("updated");
                            String url = jsonObject1.getString("url");
                            String selfLink = jsonObject1.getString("selfLink");
                            String authorName = jsonObject1.getJSONObject("author").getString("displayName");
                            //String image = jsonObject1.getJSONObject("author").getString("image");


                            ModolPost modolPost = new ModolPost(""+authorName,
                                    ""+content,
                                    ""+id,
                                    ""+published,
                                    ""+selfLink,
                                    ""+title,
                                    ""+updated,
                                    ""+url);

                            postArrayList.add(modolPost);


                        }catch (Exception e){
                            Log.d(TAG,"onResponse: 1: "+e.getMessage());
                            Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();


                        }

                    }
                    adapterPost = new AdapterPost(MainActivity.this,postArrayList);

                    postsRv.setAdapter(adapterPost);
                    progressDialog.dismiss();
                }
                catch (Exception e){
                    Log.d(TAG,"onResponse: 2: "+e.getMessage());
                    Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"onErrorResponse:"+error.getMessage());
                Toast.makeText(MainActivity.this,""+error.getMessage(),Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.action_pages){
            startActivity(new Intent(this,PageActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}

