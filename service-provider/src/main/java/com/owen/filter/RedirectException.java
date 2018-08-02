package com.owen.filter;

/**
 * @desc: TODO describe what you do
 * Created by huang_b on 2017/7/19.
 */
public class RedirectException extends RuntimeException {


    public RedirectException(String url) {
        this.url = url;
    }

    public RedirectException(String url, int code) {
        this.url = url;
        this.code = code;
    }

    public RedirectException(String url, String msg) {
        super(msg);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getCode() {
        return code;
    }

    private String url;
    private int code = -1;
}
