package com.sh.teethdetect;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    ArrayList<Home_ItemData> datalist = new ArrayList<>();
    HomeAdapter adapter = new HomeAdapter(datalist);
    HomeAdapter adapter2 = new HomeAdapter((datalist));
    RecyclerView home_RecyclerView, home_RecyclerView2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        home_RecyclerView = v.findViewById(R.id.home_Recyclerview);
        home_RecyclerView2 = v.findViewById(R.id.home2_Recyclerview);

        //사용자 가이드
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        home_RecyclerView.setLayoutManager(linearLayoutManager);

        adapter = new HomeAdapter(MovieCase.TOP);
        home_RecyclerView.setAdapter(adapter);

        Home_ItemData data = new Home_ItemData(R.drawable.ic_baseline_file_upload_24, "구현중", "옆으로 미시오");
        adapter.addItem(data);
        data = new Home_ItemData(R.drawable.ex1, "구현중","옆으로 미시오");
        adapter.addItem(data);
        data = new Home_ItemData(R.drawable.ex2, "구현중","옆으로 미시오");
        adapter.addItem(data);

        //교육용 가이드
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        home_RecyclerView2.setLayoutManager(linearLayoutManager1);

        adapter2 = new HomeAdapter(MovieCase.DOWN);
        home_RecyclerView2.setAdapter(adapter2);

        Home_ItemData data2 = new Home_ItemData(R.drawable.ex1, "구현중", "옆으로 미시오");
        adapter2.addItem(data2);
        data2 = new Home_ItemData(R.drawable.ex2, "구현중","옆으로 미시오");
        adapter2.addItem(data2);

        return v;
    }
    
}