package com.example.bloggerapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static javax.xml.transform.OutputKeys.ENCODING;

public class PostDetailsActivity extends AppCompatActivity {

    private TextView titleTv, publishInfoTv;
    private WebView webView;
    private RecyclerView labelsRv, commentsRv;

    private  String postId;
    private static final String TAG = "POST_DETAILS_TAG";
    private static final String TAG_COMMENTS = "POST_COMMENTS_TAG";

    private ArrayList<ModelLabel> labelArrayList;
    private AdapterLabel adapterLabel;

    private ArrayList<ModelComment> commentArrayList;
    private AdapterComment adapterComment;


    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        actionBar = getSupportActionBar();

        actionBar.setTitle("Article Details");

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        titleTv = findViewById(R.id.titleTv);
        publishInfoTv = findViewById(R.id.publishInfoTv);
        webView = findViewById(R.id.webView);
        labelsRv = findViewById(R.id.labelsRv);
        commentsRv = findViewById(R.id.commentsRv);


        postId = getIntent().getStringExtra("postId");
        Log.d(TAG,"onCreate: "+postId);


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        loadPostDetails();



    }

    private void loadPostDetails() {
        String url = "https://www.googleapis.com/blogger/v3/blogs/"+Constants.BLOG_ID
                +"/posts/"+postId
                +"?key="+Constants.API_KEY;

        Log.d(TAG, "loadPostDetails: URL"+url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG,"onResponse: "+response);

                try {

                    JSONObject jsonObject = new JSONObject(response);

                    String title = jsonObject.getString("title");
                    String published = jsonObject.getString("published");
                    String content = jsonObject.getString("content");
                    String url = jsonObject.getString("url");
                    String displayName = jsonObject.getJSONObject("author").getString("displayName");

                    String gmtDate = published;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss");
                    //SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy K:mm a");
                    SimpleDateFormat dateFormat2 = new SimpleDateFormat("EEE, d MMM, yyyy");
                    String formattedDate = "";
                    try {
                        Date date = dateFormat.parse(gmtDate);
                        formattedDate = dateFormat2.format(date);
                    }catch (Exception e){
                        formattedDate = published;
                        e.printStackTrace();
                    }
                    actionBar.setSubtitle(title);
                    titleTv.setText(title);
                    //publishInfoTv.setText("By "+displayName+" "+formattedDate);
                    publishInfoTv.setText("Posted On "+formattedDate);

                    webView.loadDataWithBaseURL(null,content,"text/html", ENCODING,null);

                    try {
                        labelArrayList = new ArrayList<>();
                        labelArrayList.clear();

                        JSONArray jsonArray = jsonObject.getJSONArray("labels");
                        for (int i=0; i<jsonArray.length(); i++){
                            String label = jsonArray.getString(i);
                            ModelLabel modelLabel = new ModelLabel(label);

                            labelArrayList.add(modelLabel);
                        }

                        adapterLabel = new AdapterLabel(PostDetailsActivity.this,labelArrayList);
                        labelsRv.setAdapter(adapterLabel);
                    }
                    catch (Exception e){
                        Log.d(TAG, "onResponse: "+e.getMessage());

                    }

                    loadComments();

                }
                catch (Exception e){
                    Log.d(TAG,"onResponse: "+e.getMessage());
                    Toast.makeText(PostDetailsActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(PostDetailsActivity.this,""+error.getMessage(),Toast.LENGTH_SHORT).show();
                ///////////start No internet////////////
                new AlertDialog.Builder(PostDetailsActivity.this)
                        .setTitle("Error")
                        .setMessage("Internet not available. Cross check your internet connectivity")
                        .setCancelable(false)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();

                            }
                        }).show();
                ///////////End No Internet////////////


            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void loadComments(){
        String url = "https://www.googleapis.com/blogger/v3/blogs/"+Constants.BLOG_ID+"/posts/"+postId+"/comments?key="+Constants.API_KEY;
        Log.d(TAG_COMMENTS,"loadComments: "+url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG_COMMENTS,"onResponse "+response);

                commentArrayList = new ArrayList<>();
                commentArrayList.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonArrayItems = jsonObject.getJSONArray("items");

                    for (int i=0; i<jsonArrayItems.length(); i++){
                        JSONObject jsonObjectComment = jsonArrayItems.getJSONObject(i);


                        String id = jsonObjectComment.getString("id");
                        String published = jsonObjectComment.getString("published");
                        String content = jsonObjectComment.getString("content");
                        String displayName = jsonObjectComment.getJSONObject("author").getString("displayName");
                        String profileImage = "http:" + jsonObjectComment.getJSONObject("author").getJSONObject("image").getString("url");
                        Log.d("TAG_IMAGE_URL", "onResponse: "+profileImage);


                        ModelComment modelComment = new ModelComment(
                                ""+id,
                                ""+displayName,
                                ""+profileImage,
                                ""+published,
                                ""+content
                        );
                        commentArrayList.add(modelComment);
                    }
                    adapterComment = new AdapterComment(PostDetailsActivity.this,commentArrayList);

                    commentsRv.setAdapter(adapterComment);

                }
                catch (Exception e){
                    Log.d(TAG_COMMENTS, "onResponse: "+ e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG_COMMENTS,"onErrorResponse: "+error.getMessage());


            }
        });

        RequestQueue requestQueue = Volley .newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}