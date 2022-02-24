package com.example.bloggerapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Element;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.HolderPost> {

    private Context context;
    private ArrayList<ModolPost> postArrayList;

    public AdapterPost(Context context, ArrayList<ModolPost> postArrayList) {
        this.context = context;
        this.postArrayList = postArrayList;
    }

    @NonNull
    @Override
    public HolderPost onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_post,parent,false);
        return new HolderPost(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPost holder, int position) {

        ModolPost model = postArrayList.get(position);

        String authorName = model.getAuthorName();
        String content = model.getContent();
        String id = model.getId();
        String published = model.getPublished();
        String selfLink = model.getSelfLink();
        String title = model.getTitle();
        String updated = model.getUpdated();
        String url = model.getUrl();


        //
        Document document = Jsoup.parse(content);
        try {
            Elements elements = document.select("img");
            String image = elements.get(0).attr("src");
            Picasso.get().load(image).placeholder(R.drawable.ic_image_black).into(holder.imageIv);
        }catch (Exception e){

            holder.imageIv.setImageResource(R.drawable.ic_image_black);
        }

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
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(document.text());
        //post name and date in mainpage needyamin
        //holder.publishInfoTv.setText("By " + authorName + " "+ formattedDate);
        holder.publishInfoTv.setText("Posted On " + formattedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,PostDetailsActivity.class);
                intent.putExtra("postId",id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    class HolderPost extends RecyclerView.ViewHolder{


        ImageButton moreBtn;
        TextView titleTv,publishInfoTv,descriptionTv;
        ImageView imageIv;


        public HolderPost(@NonNull View itemView) {
            super(itemView);

            titleTv = itemView.findViewById(R.id.titleTv);
            publishInfoTv = itemView.findViewById(R.id.publishInfoTv);
            imageIv = itemView.findViewById(R.id.imageIv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
        }
    }
}
