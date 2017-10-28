package com.cars.social.service;

import com.cars.social.vo.PublishAdVO;
import com.cars.social.vo.SocialPublishingVO;
import org.springframework.stereotype.Component;

/**
 * Created by balaajiparthasarathy on 2/15/17.
 */
@Component
public interface FacebookService {

    public String pushToFacebook(SocialPublishingVO socialPublishingVO)
            throws Exception;

    public String postAdOnFacebook(PublishAdVO socialPublishingVO)
            throws Exception;

}

