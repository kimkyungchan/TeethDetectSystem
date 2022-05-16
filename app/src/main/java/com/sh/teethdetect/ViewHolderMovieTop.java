package com.sh.teethdetect;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class ViewHolderMovieTop extends HomeItemView {

    ImageView iv_movie;
    TextView tv_movie_title;
    TextView tv_movie_title2;

    Home_ItemData data;

    public ViewHolderMovieTop(@NonNull View itemView) {
        super(itemView);
        iv_movie = itemView.findViewById(R.id.teethimageview3);
        tv_movie_title = itemView.findViewById(R.id.teethInfoText3);
        tv_movie_title2 = itemView.findViewById(R.id.teethDetectText3);
    }

    public void onBind(HomeItem data){
        this.data = (Home_ItemData) data;
        iv_movie.setImageResource(this.data.getDataImage());
        tv_movie_title.setText(this.data.getDatainfo());
        tv_movie_title2.setText(this.data.getDatapercent());
    }
}
