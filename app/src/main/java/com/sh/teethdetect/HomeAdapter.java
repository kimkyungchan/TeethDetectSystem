package com.sh.teethdetect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeItemView> {
    private ArrayList<Home_ItemData> itemData = new ArrayList<>();
    private MovieCase sel_type;

    public HomeAdapter(MovieCase sel_type){
        this.sel_type = sel_type;
    }

    HomeAdapter(ArrayList<Home_ItemData>itemData) {
        this.itemData=itemData;
    }


    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    // LayoutInflater - XML에 정의된 Resource(자원) 들을 View의 형태로 반환.
    @Override
    public HomeItemView onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        //MovieCase의 TOP, DOWN을 가져옴.
        if(sel_type == MovieCase.TOP){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_multi_image_item, parent, false);
            return new ViewHolderMovieTop(view);
        } else if(sel_type == MovieCase.DOWN){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.down_multi_image_item, parent, false);
            return new ViewHolderMovieDown(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeItemView holder, int position) {
        if(holder instanceof ViewHolderMovieTop){
            ViewHolderMovieTop viewHolderMovieLarge = (ViewHolderMovieTop) holder;
            viewHolderMovieLarge.onBind(itemData.get(position));
        } else if(holder instanceof  ViewHolderMovieDown) {
            ViewHolderMovieDown viewHolderMovieSmall = (ViewHolderMovieDown) holder;
            viewHolderMovieSmall.onBind(itemData.get(position));
        }
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return itemData.size();
    }
    void addItem(HomeItem data){
        itemData.add((Home_ItemData) data);
    }

}

