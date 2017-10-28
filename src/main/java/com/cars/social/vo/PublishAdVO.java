package com.cars.social.vo;

import org.springframework.stereotype.Component;

/**
 * Created by balaajiparthasarathy on 2/15/17.
 */
@Component
public class PublishAdVO {

    private String title;

    private String vdpLink;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getVdpLink() {
        return vdpLink;
    }

    public void setVdpLink(String vdpLink) {
        this.vdpLink = vdpLink;
    }


}
