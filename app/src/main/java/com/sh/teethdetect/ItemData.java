package com.sh.teethdetect;

import android.graphics.Bitmap;

public class ItemData {
    String currenttime; // 검진시각
Bitmap dataimage;// 충치사진
String datainfo; // 충치인덱스
String detect;
String uri;

public ItemData(String uri,String currenttime, Bitmap dataimage, String datainfo, String detect) {
    this.uri=uri;
    this.currenttime = currenttime;
    this.dataimage = dataimage;
    this.datainfo = datainfo;
    this.detect = detect;

}
public String getUri() {
    return uri;
}

public void setUri() {
    this.uri = uri;
}

public String getCurrentTime() {
    return currenttime;
}

public void setCurrentTime() {
    this.currenttime = currenttime;
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



public String getDetect(){
    return detect;
}
public void setDetect(String detect){
    this.detect = detect;
}


}