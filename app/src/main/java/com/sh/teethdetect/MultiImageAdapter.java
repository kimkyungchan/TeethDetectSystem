package com.sh.teethdetect;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MultiImageAdapter extends RecyclerView.Adapter<MultiImageAdapter.ViewHolder>{
private ArrayList<ItemData> itemData;
//private Context mContext = null ;

public interface OnItemClickListener
{
    void onItemClick(View v, int pos);
}

// 리스너 객체 참조를 저장하는 변수
private OnItemClickListener mListener = null;

// OnItemClickListener 객체 참조를 어댑터에 전달하는 메서드
public void setOnItemClickListener(OnItemClickListener listener)
{
    this.mListener = listener;
}

// 생성자에서 데이터 리스트 객체, Context를 전달받음.
MultiImageAdapter(ArrayList<ItemData>itemData) {
    this.itemData=itemData;
}

// 아이템 뷰를 저장하는 뷰홀더 클래스.
public class ViewHolder extends RecyclerView.ViewHolder {
    TextView datatime;
    ImageView dataimage;
    TextView datainfo;
    //TextView datadetect;
    TextView detect;


    ViewHolder(View itemView) {
        super(itemView) ;
        // 뷰 객체에 대한 참조.

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = getAdapterPosition();
                if(pos!=RecyclerView.NO_POSITION){
                    mListener.onItemClick(view,pos);
                }
            }
        });

        datatime = itemView.findViewById(R.id.teethtime);
        dataimage = itemView.findViewById(R.id.teethimageview);
        datainfo = itemView.findViewById(R.id.teethInfoText);
        //datadetect= itemView.findViewById(R.id.teethDetectText);
        detect = itemView.findViewById(R.id.teethDetect2);

    }
}


// onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
// LayoutInflater - XML에 정의된 Resource(자원) 들을 View의 형태로 반환.
@Override
public MultiImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Context context = parent.getContext() ;
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;    // context에서 LayoutInflater 객체를 얻는다.
    View view = inflater.inflate(R.layout.multi_image_item, parent, false) ;   // 리사이클러뷰에 들어갈 아이템뷰의 레이아웃을 inflate.
    MultiImageAdapter.ViewHolder vh = new MultiImageAdapter.ViewHolder(view) ;
    return vh ;
}


// onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
@Override
public void onBindViewHolder(MultiImageAdapter.ViewHolder holder, int position) {
    ItemData item = itemData.get(position);
    String uri = item.getUri();
    holder.datatime.setText("검진시각:"+item.getCurrentTime());
    holder.dataimage.setImageBitmap(item.getDataImage());
    holder.datainfo.setText("충치개수 : "+item.getDatainfo());
    //holder.datadetect.setText(item.getDatapercent());
    holder.detect.setText(item.getDetect());
}

// getItemCount() - 전체 데이터 갯수 리턴.
@Override
public int getItemCount() {
    return
            itemData.size();
}
}