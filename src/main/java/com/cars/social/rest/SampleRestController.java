package com.cars.social.rest;

import com.cars.social.contract.SocialPublishingContract;
import com.cars.social.utility.Encoder;
import com.cars.social.vo.PublishAdVO;
import com.cars.social.vo.SocialPublishingVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.codec.binary.Base64;

import com.cars.framework.config.ConfigService;
import com.cars.framework.config.feature.FeatureFlag;
import org.springframework.web.multipart.MultipartFile;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.io.*;

/**
 * Sample REST service using Spring's REST API
 */
// @Restcontroller = @Controller + @ResponseBody
@RestController
public class SampleRestController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ConfigService configService;

	@Autowired
	private FeatureFlag featureFlag;

    @Autowired
	SocialPublishingContract socialPublishingContract;

	@Autowired
    Encoder encoder;

	@RequestMapping(value = "/info", method = RequestMethod.GET, produces = "text/plain")
	public String info() throws UnsupportedEncodingException, TwitterException {
		if (logger.isDebugEnabled()) {
			logger.debug("/info called on " + getClass().getName());
		}

        return "This is the rest interface for the social-publishing-gateway1.0 application";
	}

	@RequestMapping(value = "/social", method = RequestMethod.POST, produces = "text/plain")
	public String postSocial(@RequestBody SocialPublishingVO request) {
		if (logger.isDebugEnabled()) {
			logger.debug("/info called on " + getClass().getName());
		}
		System.out.println("In Rest-"+ request.getTitle() +"----"+request.getDescription());
		try {
			socialPublishingContract.pushToSocial(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "This is the postSocial operation in the rest interface for the social-publishing-gateway1.0 application";
	}

	@RequestMapping(value = "/feature/{feature:.+}", method = RequestMethod.GET, produces = "text/plain")
	public ResponseEntity<String> feature(@PathVariable("feature") String feature) {
		if (logger.isDebugEnabled()) {
			logger.debug("/feature/" + feature + " called on " + getClass().getName());
		}
		if (featureFlag.isFeature(feature)) {
			return new ResponseEntity<String>(feature + " is enabled", HttpStatus.OK);
		} else {
			return new ResponseEntity<String>(feature + " is not enabled", HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/config/{config:.+}", method = RequestMethod.GET, produces = "text/plain")
	public ResponseEntity<String> config(@PathVariable("config") String config) {
		if (logger.isDebugEnabled()) {
			logger.debug("/config/" + config + " called on " + getClass().getName());
		}
		Object obj = configService.get(config);
		if (obj == null) {
			return new ResponseEntity<String>("Unable to find config property " + config, HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<String>(obj.toString(), HttpStatus.OK);
		}
	}


	private void token() throws TwitterException {
        // The factory instance is re-useable and thread safe.
        TwitterFactory factory = new TwitterFactory();
        AccessToken accessToken = loadAccessToken();
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer("","");
        twitter.setOAuthAccessToken(accessToken);
        Status status = twitter.updateStatus("Check this out https://www.google.com");
        System.out.println("Successfully updated the status to [" + status.getText() + "].");

    }


	@RequestMapping(value = "/marketing", method = RequestMethod.POST, produces = "text/plain",consumes = {"multipart/form-data"})
	public String postAdOnFacebook(@RequestPart("title") String title,@RequestPart("vdpLink") String vdpLink,@RequestPart("file") MultipartFile image) {
		if (logger.isDebugEnabled()) {
			logger.debug("/info called on " + getClass().getName());
		}
		OutputStream outputStream = null;
		OutputStream originalOutputStream = null;
		try {
			System.out.println("Title - "+ title + vdpLink);
			File convFile = new File(image.getOriginalFilename());
			convFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(image.getBytes());
			fos.close();
			PublishAdVO request = new PublishAdVO();
			request.setTitle(title);
			request.setVdpLink(vdpLink);
			socialPublishingContract.postAdOnFacebook(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "This is the postSocial operation in the rest interface for the social-publishing-gateway1.0 application";
	}

    private static AccessToken loadAccessToken(){
        String token = "";
        String tokenSecret = "";
        return new AccessToken(token, tokenSecret);
    }



}
