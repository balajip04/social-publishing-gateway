package com.cars.social.vo;

import org.springframework.stereotype.Component;

/**
 * Created by balaajiparthasarathy on 2/22/17.
 */
@Component
public class InputSocial {

    private String title;

    private String description;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
