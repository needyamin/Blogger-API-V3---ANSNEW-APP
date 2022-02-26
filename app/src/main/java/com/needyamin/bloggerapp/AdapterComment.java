package com.needyamin.bloggerapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.needyamin.bloggerapp.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterComment extends  RecyclerView.Adapter<AdapterComment.HolderComment>{

    private Context context;
    private ArrayList<ModelComment> commentArrayList;

    public AdapterComment(Context context, ArrayList<ModelComment> commentArrayList) {
        this.context = context;
        this.commentArrayList = commentArrayList;
    }

    @NonNull
    @Override
    public HolderComment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_comment, parent,false);
        return new HolderComment(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderComment holder, int position) {

        ModelComment modelComment = commentArrayList.get(position);
        String Id =modelComment.getId();
        String name = modelComment.getName();
        String published = modelComment.getPublished();
        String comment = modelComment.getComment();
        String image = modelComment.getProfileImage();

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

        holder.nameTv.setText(name);
        holder.dateTv.setText(formattedDate);
        holder.commentTv.setText(comment);
        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_person_gray).into(holder.profileIv);
        }
        catch (Exception e){
            holder.profileIv.setImageResource(R.drawable.ic_person_gray);
        }
    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    class HolderComment extends RecyclerView.ViewHolder {


        ImageView profileIv;
        TextView nameTv, dateTv,commentTv;

        public HolderComment(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.profileIv);
            nameTv= itemView.findViewById(R.id.nameTv);
            dateTv= itemView.findViewById(R.id.dateTv);
            commentTv= itemView.findViewById(R.id.commentTv);
        }
    }
}
