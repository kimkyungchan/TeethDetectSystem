package com.sh.teethdetect;

import android.graphics.Bitmap;

public class ItemData {
Bitmap dataimage;// 충치사진
String datainfo; // 충치인덱스
String datadetect; // 병원방문텍스트

public ItemData(Bitmap dataimage, String datainfo, String datadetect) {
    this.dataimage = dataimage;
    this.datainfo = datainfo;
    this.datadetect = datadetect;
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
    return datadetect;
}

public void setDatapercent(String datapercent) {
    this.datadetect = datapercent;
}
}
