package com.sh.teethdetect;


import android.graphics.drawable.Drawable;

public class Home_ItemData extends HomeItem{

    int dataimage; //받아올 이미지
    String datainfo; // 내용 설명
    String visittext; // 내용 설명2



    public Home_ItemData(int dataimage, String datainfo, String visittext) {

        this.dataimage = dataimage;
        this.datainfo = datainfo;
        this.visittext = visittext;

    }

    public int getDataImage() {
        return dataimage;
    }

    public void setDataImage(int dataimage) {
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

    public void setDatapercent(String visittext) {
        this.visittext = visittext;
    }

}
