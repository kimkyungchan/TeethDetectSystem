package com.sh.teethdetect;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

        Home_ItemData data = new Home_ItemData(R.drawable.teeth_detect, "치아사진을 통해 충치검진", "(미리 찍어놓은 사진 필요)");
        adapter.addItem(data);
        data = new Home_ItemData(R.drawable.teeth_camera, "내장카메라 이용한 실시간 충치검진","(앱 내에서 영상촬영 가능)");
        adapter.addItem(data);
        data = new Home_ItemData(R.drawable.teeth_home, "홈으로 이동","");
        adapter.addItem(data);
        data = new Home_ItemData(R.drawable.teeth_map, "주변치과 검색기능","(사용자가 거리지정)");
        adapter.addItem(data);
        data = new Home_ItemData(R.drawable.teeth_info, "내 정보 기능","(회원가입, 로그인, 정보수정, 회원탈퇴)");
        adapter.addItem(data);

        //교육용 가이드
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        home_RecyclerView2.setLayoutManager(linearLayoutManager1);

        adapter2 = new HomeAdapter(MovieCase.DOWN);
        home_RecyclerView2.setAdapter(adapter2);

        Home_ItemData data2 = new Home_ItemData(R.drawable.guide1, "-올바른 형식-", "치아 전체가 보이게 찍어주세요.");
        adapter2.addItem(data2);
        data2 = new Home_ItemData(R.drawable.guide3, "-올바른 형식-","충치 의심부분을 가까이 찍어주세요.");
        adapter2.addItem(data2);
        data2 = new Home_ItemData(R.drawable.guide2, "-올바르지 않은 형식-","음식물이 끼어있는\n 치아사진.");
        adapter2.addItem(data2);
        data2 = new Home_ItemData(R.drawable.guide4, "-올바르지 않은 형식-","어두워서 치아가\n 보이지 않는 사진.");
        adapter2.addItem(data2);

        return v;
    }

}