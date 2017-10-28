package com.cars.social.service;

import com.cars.social.vo.SocialPublishingVO;
import org.springframework.stereotype.Component;

/**
 * Created by balaajiparthasarathy on 2/21/17.
 */
@Component
public interface TwitterService {

    public String pushToTwitter(SocialPublishingVO socialPublishingVO)
            throws Exception;
}
