package com.cars.social.contract;

import com.cars.social.service.FacebookService;
import com.cars.social.service.TwitterService;
import com.cars.social.vo.PublishAdVO;
import com.cars.social.vo.SocialPublishingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by balaajiparthasarathy on 2/15/17.
 */
@Component
public class SocialPublishingContractImpl implements SocialPublishingContract{

    @Autowired
    FacebookService facebookService;
    @Autowired
    TwitterService twitterService;
    @Override
    public String pushToSocial(SocialPublishingVO socialPublishingVO) throws Exception {
        /*Call To Facebook Service*/
        facebookService.pushToFacebook(socialPublishingVO);
        /*Call To Twitter Service*/
        twitterService.pushToTwitter(socialPublishingVO);
        return null;
    }


    @Override
    public String postAdOnFacebook(PublishAdVO publishAdVO) throws Exception {
        /*Call To Facebook Service*/
        facebookService.postAdOnFacebook(publishAdVO);

        return null;
    }
}
