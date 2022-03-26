package com.sh.teethdetect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class ItemData {
Bitmap dataimage;// 충치사진
String datainfo; // 충치인덱스
String visittext; // 병원방문텍스트

public ItemData(Bitmap dataimage, String datainfo, String visittext) {

    this.dataimage = dataimage;
    this.datainfo = datainfo;
    this.visittext = visittext;

}


public Bitmap getDataImage() {
    return dataimage;
}

public void setDataImage(Bitmap dataimage) {
    this.dataimage = dataimage;
}

public String getDatainfo() {
    return datainfo;
}

public void setDatainfo(String datainfo) {
    this.datainfo = datainfo;
}


public String getDatapercent() {
    return visittext;
}

public void setDatapercent(String datapercent) {
    this.visittext = datapercent;
}







public static Bitmap StringToBitmap(String encodedString) {
    try {
        byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        return bitmap;
    }
    catch (Exception e) {
        e.getMessage();
        return null;
    }
}



}
