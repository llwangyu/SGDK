package com.android.fisher.sgface.entity;

/**
 * Created by Fisher on 2018-04-12.
 */

public class FZdMupdate {
    private String id;
    private int versioncode;
    private String versionname;
    private String url;
    private String description;

    public FZdMupdate() {
    }

    public FZdMupdate(String id, int versioncode, String versionname, String url, String description) {
        this.id = id;
        this.versioncode = versioncode;
        this.versionname = versionname;
        this.url = url;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public int getVersioncode() {
        return versioncode;
    }

    public String getVersionname() {
        return versionname;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVersioncode(int versioncode) {
        this.versioncode = versioncode;
    }

    public void setVersionname(String versionname) {
        this.versionname = versionname;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
