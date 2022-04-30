package com.sh.teethdetect;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class MyPageNoFragment extends Fragment {

//비회원 마이페이지
    Button MyInfoLoginBt;
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View v = inflater.inflate(R.layout.fragment_mypageno, container, false);

    MyInfoLoginBt = v.findViewById(R.id.MyinfoLoginBt);


    MyInfoLoginBt.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(),LoginActivity.class);
            startActivity(intent);
        }
    });


    return v;
}
}