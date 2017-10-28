package com.cars.social.service;

import com.cars.social.vo.SocialPublishingVO;
import org.springframework.stereotype.Service;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Created by balaajiparthasarathy on 2/21/17.
 */
@Service
public class TwitterServiceImpl implements TwitterService{

    @Override
    public String pushToTwitter(SocialPublishingVO socialPublishingVO){
        // The factory instance is re-useable and thread safe.
        System.err.println("pushToTwitter");
        TwitterFactory factory = new TwitterFactory();
        AccessToken accessToken = loadAccessToken();
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer("","");
        twitter.setOAuthAccessToken(accessToken);
        Status status = null;
        try {
            status = twitter.updateStatus(socialPublishingVO.getTitle() +" "+ socialPublishingVO.getDescription());
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        System.out.println("Successfully updated twitter status to [" + status.getText() + "].");

        return null;
    }


    private static AccessToken loadAccessToken(){
        String token = "";
        String tokenSecret = "";
        return new AccessToken(token, tokenSecret);
    }






}
