package com.example.user.hsproject;

/**
 * Created by user on 2015-12-22.
 *
 * 데이터 통신시 필요한 내용의 컬럼을 정의.
 *
 * [온라인팀 손동현]
 *
 * CONTEXT_NO, SUBJECT, REG_DT, VIEW_COUNT, THUMB_URL
 */
public class VoData {

    private String CONTEXT_NO = "";
    private String SUBJECT = "";
    private String REG_DT = "";
    private String VIEW_COUNT = "";
    private String THUMB_URL = "";

    public String getSUBJECT() {
        return SUBJECT;
    }

    public void setSUBJECT(String SUBJECT) {
        this.SUBJECT = SUBJECT;
    }

    public String getREG_DT() {
        return REG_DT;
    }

    public void setREG_DT(String REG_DT) {
        this.REG_DT = REG_DT;
    }

    public String getVIEW_COUNT() {
        return VIEW_COUNT;
    }

    public void setVIEW_COUNT(String VIEW_COUNT) {
        this.VIEW_COUNT = VIEW_COUNT;
    }

    public String getCONTEXT_NO() {
        return CONTEXT_NO;
    }

    public void setCONTEXT_NO(String CONTEXT_NO) {
        this.CONTEXT_NO = CONTEXT_NO;
    }

    public String getTHUMB_URL() {
        return THUMB_URL;
    }

    public void setTHUMB_URL(String THUMB_URL) {
        this.THUMB_URL = THUMB_URL;
    }

    @Override
    public String toString() {
        return "VoData{" +
                "CONTEXT_NO='" + CONTEXT_NO + '\'' +
                ", SUBJECT='" + SUBJECT + '\'' +
                ", REG_DT='" + REG_DT + '\'' +
                ", VIEW_COUNT='" + VIEW_COUNT + '\'' +
                ", THUMB_URL='" + THUMB_URL + '\'' +
                '}';
    }
}
