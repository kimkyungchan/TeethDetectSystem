package com.sh.teethdetect;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class VideoWindowsActivityFragment extends Fragment {

Button detectgo;
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View v = inflater.inflate(R.layout.fragment_video_windows_activity, container, false);
    detectgo= v.findViewById(R.id.detectgo);

    detectgo.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(),VideodetectActivity.class);
            startActivity(intent);
        }
    });
    return v;
}
}