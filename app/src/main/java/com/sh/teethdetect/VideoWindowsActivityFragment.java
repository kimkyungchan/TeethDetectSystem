package com.sh.teethdetect;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class VideoWindowsActivityFragment extends Fragment {

Button takepicture, takepicture2;
Dialog dialog1;
Button Dialogbtn;
TextView DialogText;
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View v = inflater.inflate(R.layout.fragment_video_windows_activity, container, false);
    takepicture= v.findViewById(R.id.takepicture);
    takepicture2 = v.findViewById(R.id.takepicture2);

    takepicture.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ShowDialog4();
        }
    });

    takepicture2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ShowDialog3();
        }
    });

    return v;
}

    public void ShowDialog3(){
        dialog1 = new Dialog(getActivity());
        dialog1.setContentView(R.layout.cariesdialog);
        dialog1.show();

        Dialogbtn = dialog1.findViewById(R.id.CariesDialogButton);
        DialogText = dialog1.findViewById(R.id.CariesDialogText);
        DialogText.setText("확인버튼을 누르면 카메라파이 앱으로 이동되며 \n 메뉴얼대로 촬영해주세요.");
        Dialogbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1;
                intent1 = getActivity().getPackageManager().getLaunchIntentForPackage("com.vaultmicro.camerafi2");
                startActivity(intent1);
                dialog1.dismiss();
            }
        });
    }

    public void ShowDialog4(){
        dialog1 = new Dialog(getActivity());
        dialog1.setContentView(R.layout.cariesdialog);
        dialog1.show();

        Dialogbtn = dialog1.findViewById(R.id.CariesDialogButton);
        DialogText = dialog1.findViewById(R.id.CariesDialogText);
        DialogText.setText("확인버튼을 누르면 기본카메라 앱으로 이동되며 \n 메뉴얼대로 촬영해주세요.");
        Dialogbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1;
                intent1 = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(intent1);
                dialog1.dismiss();
            }
            });
        }

    }