package com.needyamin.bloggerapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.needyamin.bloggerapp.R;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterPage extends RecyclerView.Adapter<AdapterPage.HolderPage> {

    private Context context;
    private ArrayList<ModelPage> pageArrayList;

    public AdapterPage(Context context, ArrayList<ModelPage> pageArrayList) {
        this.context = context;
        this.pageArrayList = pageArrayList;
    }

    @NonNull
    @Override
    public HolderPage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_page, parent, false);
        return new HolderPage(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPage holder, int position) {

        ModelPage model = pageArrayList.get(position);
        String authorName = model.getAuthorName();
        String content = model.getContent();
        String id = model.getId();
        String published = model.getPublished();
        String selfLink = model.getSelfLink();
        String title = model.getTitle();
        String url = model.getUrl();
        String updated = model.getUpdated();


        Document document = Jsoup.parse(content);
        try {

            Elements elements = document.select("img");
            String image =elements.get(0).attr("src");

            Picasso.get().load(image).placeholder(R.drawable.ic_image_black).into(holder.imageIv);

        }
        catch (Exception e){

            holder.imageIv.setImageResource(R.drawable.ic_image_black);
        }

        String gmtDate = published;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy K:mm a");
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
        //needyamin
        //holder.publishInfoTv.setText("By "+authorName+" "+formattedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start page
                Intent intent = new Intent(context, PageDetailsActivity.class);
                intent.putExtra("pageId",id);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return pageArrayList.size();
    }

    class HolderPage extends RecyclerView.ViewHolder{

        private TextView titleTv, publishInfoTv, descriptionTv;
        private ImageView imageIv;

        public HolderPage(@NonNull View itemView) {
            super(itemView);

            titleTv = itemView.findViewById(R.id.titleTv);
            publishInfoTv = itemView.findViewById(R.id.publishInfoTv);
            imageIv = itemView.findViewById(R.id.imageIv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
        }
    }
}
