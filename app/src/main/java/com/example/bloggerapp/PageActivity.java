package com.example.bloggerapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
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

public class PageActivity extends AppCompatActivity {

    private ActionBar actionBar;

    private RecyclerView pagesRv;

    private ArrayList <ModelPage> pageArrayList;

    private AdapterPage adapterPage;

    private static  final String TAG="PAGES_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);

        actionBar = getSupportActionBar();
        actionBar.setTitle("All Pages");
        actionBar.setSubtitle("Pages");

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        pagesRv = findViewById(R.id.pagesRv);

        loadPages();

    }

    private void loadPages() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading Pages");
        progressDialog.show();

        String url = "https://www.googleapis.com/blogger/v3/blogs/"+Constants.BLOG_ID+"/pages?key="+Constants.API_KEY;
        Log.d(TAG, "loadPages: "+url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: "+response);
                progressDialog.dismiss();

                try {

                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonArray = jsonObject.getJSONArray("items");

                    pageArrayList = new ArrayList<>();
                    pageArrayList.clear();

                    for (int i= 0; i<jsonArray.length(); i++){

                        try {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            String id = jsonObject1.getString("id");
                            String title = jsonObject1.getString("title");
                            String content = jsonObject1.getString("content");
                            String published = jsonObject1.getString("published");
                            String updated = jsonObject1.getString("updated");
                            String url = jsonObject1.getString("url");
                            String selfLink = jsonObject1.getString("selfLink");
                            String displayName = jsonObject1.getJSONObject("author").getString("displayName");
                            String image = jsonObject1.getJSONObject("author").getJSONObject("image").getString("url");

                            ModelPage model = new ModelPage(
                                    ""+displayName,
                                    ""+content,
                                    ""+id,
                                    ""+published,
                                    ""+selfLink,
                                    ""+title,
                                    ""+updated,
                                    ""+url);


                            pageArrayList.add(model);



                        }catch (Exception e){
                            Log.d(TAG, "onResponse: "+e.getMessage());
                        }
                    }
                    adapterPage = new AdapterPage(PageActivity.this, pageArrayList);
                    pagesRv.setAdapter(adapterPage);

                }
                catch (Exception e){
                    Log.d(TAG, "onResponse: "+e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: "+error.getMessage());
                progressDialog.dismiss();
                Toast.makeText(PageActivity.this,""+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();
        return super.onSupportNavigateUp();
    }
}