package com.cars.social.service;


import com.cars.social.vo.PublishAdVO;
import com.cars.social.vo.SocialPublishingVO;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.wink.client.RestClient;
import org.apache.wink.client.Resource;
import org.springframework.stereotype.Service;
import twitter4j.JSONException;
import twitter4j.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;

/**
 * Created by balaajiparthasarathy on 2/15/17.
 */
@Service
public class FacebookServiceImpl implements FacebookService {

    String marketingPageAccessToken = "";

    String pageAccessToken = "";

    String accountId = "";

    @Override
    public String pushToFacebook(SocialPublishingVO socialPublishingVO) {

        //String pageAccessToken = "";
        String payload = "{message:\"" + socialPublishingVO.getTitle() + "\",link:\"" + socialPublishingVO.getDescription() + "\"}";
        System.err.println("payload- " + payload);
        String URL = "https://graph.facebook.com/me/feed?access_token=" + pageAccessToken;
        pushToFacebook(URL, payload);
        return null;
    }

    private String pushToFacebook(String URL, String payload) {
        try {
            System.err.println("pushToFacebook");
            RestClient localRestClient = new RestClient();
            Resource localResource = localRestClient.resource(URL);
            String response = localResource.contentType("application/json").accept("application/json").post(String.class, payload);
            System.err.println("Updated status to facebook, Response- " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public String postAdOnFacebook(PublishAdVO publishAdVO) throws Exception {
        String campaignId = createCampaign();
        defineTargetting();
        Long adsets = budgetBilling(campaignId);
        String[] array = createImage();
        String creativeId = adCreative(array,publishAdVO.getVdpLink(),publishAdVO.getTitle());
        String adId = createAd(adsets,creativeId);

        return null;
    }

    private String createAd(Long adsets, String creativeId) {

        /*
        curl \
          -F 'name=My Ad' \
          -F 'adset_id=<AD_SET_ID>' \
          -F 'creative={"creative_id":"<CREATIVE_ID>"}' \
          -F 'status=PAUSED' \
          -F 'access_token=<ACCESS_TOKEN>' \
          https://graph.facebook.com/v2.8/act_<AD_ACCOUNT_ID>/ads
         */
        System.out.print("createAd");
        String[] command = {"curl", "-F", "name=My Ad mdx",
                "-F","adset_id="+adsets,"-F","creative={\"creative_id\":\""+creativeId+"\"}",
                "-F","status=PAUSED",
                "-F","access_token=" + marketingPageAccessToken, "https://graph.facebook.com/v2.8/"+accountId+"/ads"};
        String result = null;
        String parsed=null;
        try {
            result = fireCurl(command);
            System.out.print("result for createAd - " + result);
            parsed = getParsedString(result);

        } catch (IOException e) {
            System.out.print("error");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.print("error exception");
            e.printStackTrace();
        }

        return parsed;
    }

    private String adCreative(String[] array,String vdpLink,String title) {
        /*
        curl \
          -F 'name=Sample Creative' \
          -F 'object_story_spec={
            "link_data": {
              "image_hash": "<IMAGE_HASH>",
              "link": "<URL>",
              "message": "try it out"
            },
            "page_id": "<PAGE_ID>"
          }' \
          -F 'access_token=<ACCESS_TOKEN>' \
          https://graph.facebook.com/v2.8/act_<AD_ACCOUNT_ID>/adcreatives
         */
        System.out.print("adCreative - "+ array[0] +"----"+array[1]);
        String[] command = {"curl", "-F", "name=Sample Creative","-F",
                "object_story_spec={\"link_data\": {\"image_hash\":\""+array[0]+"\",\"link\":\""+vdpLink+"\",\"message\": \"" + title + "\"},\"page_id\":\"\"}",
                "-F","access_token=" + marketingPageAccessToken, "https://graph.facebook.com/v2.8/"+accountId+"/adcreatives"};
        String result = null;
        String parsed=null;
        try {
            result = fireCurl(command);
            System.out.print("result for adCreative - " + result);
            parsed = getParsedString(result);

        } catch (IOException e) {
            System.out.print("error");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.print("error exception");
            e.printStackTrace();
        }

        return parsed;
    }

    private String[] createImage() {
        /*
        curl \
      -F 'filename=@<IMAGE_PATH>' \
      -F 'access_token=<ACCESS_TOKEN>' \
      https://graph.facebook.com/v2.8/act_<AD_ACCOUNT_ID>/adimages
         */
        System.out.print("createImage");
        String[] command = {"curl", "-F", "filename=@acuraMDX.jpg",
                "-F","access_token=" + marketingPageAccessToken, "https://graph.facebook.com/v2.8/"+accountId+"/adimages"};
        String result = null;
        String hash=null;
        String url=null;
        String[] resultArray = new String[2];
        try {
            result = fireCurl(command);
            System.out.print("result for createImage - " + result);
            hash = getParsedHashString(result);
            url = getParsedUrlString(result);
            resultArray[0] = hash;
            resultArray[1] = url;

        } catch (IOException e) {
            System.out.print("error");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.print("error exception");
            e.printStackTrace();
        }
        return resultArray;

    }


    private long budgetBilling(String campaignId) {
        /*
        curl \
          -F 'name=My Ad Set' \
          -F 'optimization_goal=REACH' \
          -F 'billing_event=IMPRESSIONS' \
          -F 'bid_amount=2' \
          -F 'daily_budget=1000' \
          -F 'campaign_id=<CAMPAIGN_ID>' \
          -F 'targeting={"geo_locations":{"countries":["US"]}}' \
          -F 'start_time=2016-12-19T13:26:14+0000' \
          -F 'end_time=2016-12-26T13:26:14+0000' \
          -F 'status=PAUSED' \
          -F 'access_token=<ACCESS_TOKEN>' \
          https://graph.facebook.com/v2.8/act_<AD_ACCOUNT_ID>/adsets
         */
        System.out.print("result for budgetBilling campaignId - " + campaignId);
        String[] command = {"curl", "-F", "name=Ad Set acura", "-F",
                "optimization_goal=REACH", "-F", "billing_event=IMPRESSIONS",
                "-F", "bid_amount=2", "-F", "daily_budget=100", "-F", "campaign_id=" + campaignId, "-F", "targeting={\"geo_locations\":{\"countries\":[\"US\"]}}",
                "-F", "start_time=2017-12-19T13:26:14+0000", "-F", "end_time=2017-12-26T13:26:14+0000", "-F", "status=PAUSED", "-F",
                "access_token=" + marketingPageAccessToken, "https://graph.facebook.com/v2.8/"+accountId+"/adsets"};

        Long adsets=0l;
        String result = null;
        try {
            //process.redirectOutput(new File("defineTargetting.txt"));
            result = fireCurl(command);
            System.out.print("result for budgetBilling - " + result);
            adsets = Long.parseLong(getParsedString(result));

        } catch (IOException e) {
            System.out.print("error");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.print("error exception");
            e.printStackTrace();
        }
        return adsets;
    }

    private void defineTargetting() {
        /*
        curl -G \
      -d 'type=adinterest' \
      -d 'q=baseball' \
      -d 'access_token=<ACCESS_TOKEN>' \
      https://graph.facebook.com/v2.8/search

      -d 'location_types=["country"]' \
  -d 'type=adgeolocation' \
  -d 'q=un' \

  -d 'location_types=["city"]' \
  -d 'type=adgeolocation' \
  -d 'q=dub' \
         */
        String[] command = {"curl", "-G",
                "-d", "type=adinterest",
                "-d", "q=cars",
                //"-d", "location_types=[\"city\"]",
                //"-d", "type=adgeolocation",
                //"-d", "q="
                "-d", "access_token=" + marketingPageAccessToken, " https://graph.facebook.com/v2.8/search"};

        String result = null;
        try {
            //process.redirectOutput(new File("defineTargetting.txt"));
            result = fireCurl(command);
            System.out.print("result for defineTargetting - " + result);

        } catch (IOException e) {
            System.out.print("error");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.print("error exception");
            e.printStackTrace();
        }
    }


    private String createCampaign() {
       /* curl \
        -F 'name=My campaign' \
        -F 'objective=LINK_CLICKS' \
        -F 'status=PAUSED' \
        -F 'access_token=<ACCESS_TOKEN>' \
        https://graph.facebook.com/v2.8/act_<AD_ACCOUNT_ID>/campaigns
        */
        String[] command = {"curl", "-F", "name=campaign acura new", "-F", "objective=LINK_CLICKS",
                "-F", "status=PAUSED", "-F", "access_token=" + marketingPageAccessToken, "https://graph.facebook.com/v2.8/"+accountId+"/campaigns"};
        ProcessBuilder process = new ProcessBuilder(command);
        Process p;
        String result = null;
        String campaignId = null;
        try {
            //process.redirectOutput(new File("createCampaign.txt"));
            result = fireCurl(command);
            System.out.print("result for createCampaign - " + result);
            campaignId = getParsedString(result);
        } catch (IOException e) {
            System.out.print("error");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.print("error exception");
            e.printStackTrace();
        }
        return campaignId;
    }

    private String getParsedString(String result) {
        String parsedString = null;
        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();

        if(null!= jsonObject.get("id")){
            System.out.println(jsonObject.get("id").getAsString());
            parsedString = jsonObject.get("id").getAsString();
        }
        return parsedString;
    }

    private String getParsedHashString(String result) {
        String parsedString = null;
        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();

        if(result.contains("hash")){
            System.out.println(jsonObject.getAsJsonObject("images").getAsJsonObject("acuraMDX.jpg").get("hash").getAsString());
            parsedString = jsonObject.getAsJsonObject("images").getAsJsonObject("acuraMDX.jpg").get("hash").getAsString();
        }

        return parsedString;
    }

    private String getParsedUrlString(String result) {
        String parsedString = null;
        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();

        if(result.contains("url")){
            System.out.println(jsonObject.getAsJsonObject("images").getAsJsonObject("acuraMDX.jpg").get("url").getAsString());
            parsedString = jsonObject.getAsJsonObject("images").getAsJsonObject("acuraMDX.jpg").get("url").getAsString();
        }
        return parsedString;
    }

    private String fireCurl(String[] command) throws IOException {
        String result;
        ProcessBuilder process = new ProcessBuilder(command);
        Process p;
        //process.redirectOutput(new File("defineTargetting.txt"));
        p = process.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
            builder.append(System.getProperty("line.separator"));
        }
        result=builder.toString();
        return result;
    }

    public static void main(String args[]) throws JSONException {
        String abc ="{\n" +
                "  \"error_count\": 0,\n" +
                "  \"token_expiration_minutes\": 0,\n" +
                "  \"used_vehicles\": {\n" +
                "    \"version\": 1,\n" +
                "    \"data_included\": true,\n" +
                "    \"data_available\": true,\n" +
                "    \"template\": 1,\n" +
                "    \"used_vehicle_list\": [\n" +
                "      {\n" +
                "        \"adjusted_tradein_rough\": 5230,\n" +
                "        \"base_retail_clean\": 10575,\n" +
                "        \"regional_retail_avg\": 0,\n" +
                "        \"model\": \"Accord\",\n" +
                "        \"base_whole_clean\": 7450,\n" +
                "        \"mileage_list\": [\n" +
                "          {\n" +
                "            \"xclean\": 500,\n" +
                "            \"range_begin\": 67001,\n" +
                "            \"finadv\": 625,\n" +
                "            \"clean\": 750,\n" +
                "            \"mileage_cat\": \"C\",\n" +
                "            \"rough\": 1050,\n" +
                "            \"avg\": 975,\n" +
                "            \"model_year\": \"2010\",\n" +
                "            \"range_end\": 75000\n" +
                "          }\n" +
                "        ],\n" +
                "        \"base_whole_xclean\": 8300,\n" +
                "        \"adjusted_whole_avg\": 7025,\n" +
                "        \"gvw\": 3269,\n" +
                "        \"base_private_clean\": 9750,\n" +
                "        \"add_deduct_list\": [\n" +
                "          {\n" +
                "            \"resid30\": 0,\n" +
                "            \"resid24\": 0,\n" +
                "            \"uoc\": \"05\",\n" +
                "            \"clean\": -750,\n" +
                "            \"resid48\": 0,\n" +
                "            \"resid12\": 0,\n" +
                "            \"rough\": -750,\n" +
                "            \"xclean\": -750,\n" +
                "            \"avg\": -750,\n" +
                "            \"resid60\": 0,\n" +
                "            \"auto\": \"N\",\n" +
                "            \"resid36\": 0,\n" +
                "            \"resid42\": 0,\n" +
                "            \"name\": \"w/o Auto Trans\",\n" +
                "            \"resid72\": 0\n" +
                "          }\n" +
                "        ],\n" +
                "        \"basic_warranty\": \"3-year/36,000-mile\",\n" +
                "        \"adjusted_private_clean\": 10500,\n" +
                "        \"transmission\": \"A\",\n" +
                "        \"style\": \"4D Sedan\",\n" +
                "        \"mileage_resid72\": 0,\n" +
                "        \"mileage_retail_xclean\": 500,\n" +
                "        \"adjusted_resid24\": 0,\n" +
                "        \"adjusted_retail_xclean\": 12125,\n" +
                "        \"regional_tradein_avg\": 0,\n" +
                "        \"adjusted_retail_clean\": 11325,\n" +
                "        \"mileage_tradein_clean\": 750,\n" +
                "        \"base_tradein_rough\": 4180,\n" +
                "        \"add_deduct_whole_xclean\": 0,\n" +
                "        \"regional_tradein_clean\": 0,\n" +
                "        \"mileage_finadv\": 625,\n" +
                "        \"ext_doors\": \"4\",\n" +
                "        \"regional_whole_rough\": 0,\n" +
                "        \"airbags\": \"Side Curtain\",\n" +
                "        \"adjusted_resid30\": 0,\n" +
                "        \"msrp\": 21855,\n" +
                "        \"fuel_cap\": \"18.5\",\n" +
                "        \"seat_cap\": \"5\",\n" +
                "        \"engine_displacement\": \"2.4L\",\n" +
                "        \"add_deduct_resid72\": 0,\n" +
                "        \"add_deduct_resid60\": 0,\n" +
                "        \"mileage_resid24\": 0,\n" +
                "        \"add_deduct_tradein_avg\": 0,\n" +
                "        \"regional_whole_xclean\": 0,\n" +
                "        \"base_resid60\": 0,\n" +
                "        \"add_deduct_private_rough\": 0,\n" +
                "        \"country\": \"US\",\n" +
                "        \"regional_private_rough\": 0,\n" +
                "        \"regional_whole_clean\": 0,\n" +
                "        \"retail_equipped\": 23584,\n" +
                "        \"vin\": \"1HGCP2F3A\",\n" +
                "        \"add_deduct_private_clean\": 0,\n" +
                "        \"region3\": 0,\n" +
                "        \"wheel_base\": 110.2,\n" +
                "        \"mileage_resid42\": 0,\n" +
                "        \"region6\": 0,\n" +
                "        \"add_deduct_retail_rough\": 0,\n" +
                "        \"adjusted_tradein_avg\": 7165,\n" +
                "        \"fuel_type\": \"Gas\",\n" +
                "        \"add_deduct_retail_avg\": 0,\n" +
                "        \"base_resid42\": 0,\n" +
                "        \"series\": \"LX\",\n" +
                "        \"add_deduct_retail_clean\": 0,\n" +
                "        \"adjusted_retail_rough\": 7900,\n" +
                "        \"base_whole_rough\": 4400,\n" +
                "        \"adjusted_resid12\": 0,\n" +
                "        \"fuel_delivery\": \"MPI\",\n" +
                "        \"price_includes\": \"AT AC\",\n" +
                "        \"mileage_whole_avg\": 975,\n" +
                "        \"base_retail_avg\": 8775,\n" +
                "        \"taxable_hp\": 18.800000000000001,\n" +
                "        \"num_gears\": \"5\",\n" +
                "        \"base_private_avg\": 7975,\n" +
                "        \"mileage_resid60\": 0,\n" +
                "        \"description_score\": \"\",\n" +
                "        \"mileage_retail_rough\": 1050,\n" +
                "        \"adjusted_whole_rough\": 5450,\n" +
                "        \"regional_whole_avg\": 0,\n" +
                "        \"mileage_tradein_rough\": 1050,\n" +
                "        \"base_resid24\": 0,\n" +
                "        \"drivetrain\": \"FWD\",\n" +
                "        \"regional_private_avg\": 0,\n" +
                "        \"engine_description\": \"2.4L I-4 MPI DOHC \",\n" +
                "        \"base_retail_xclean\": 11625,\n" +
                "        \"tire_size\": \"215/60R16\",\n" +
                "        \"regional_private_clean\": 0,\n" +
                "        \"adjusted_whole_clean\": 8200,\n" +
                "        \"mileage_retail_clean\": 750,\n" +
                "        \"uvc\": \"2010360015\",\n" +
                "        \"state\": \"il\",\n" +
                "        \"regional_retail_xclean\": 0,\n" +
                "        \"adjusted_resid72\": 0,\n" +
                "        \"mileage_resid12\": 0,\n" +
                "        \"base_resid48\": 0,\n" +
                "        \"base_private_rough\": 6075,\n" +
                "        \"regional_retail_clean\": 0,\n" +
                "        \"hwy_mpg\": \"31\",\n" +
                "        \"first_values_flag\": false,\n" +
                "        \"anti_corrosion_warranty\": \"5-year/Unlimited-mile\",\n" +
                "        \"mileage_cat\": \"C\",\n" +
                "        \"mileage_whole_clean\": 750,\n" +
                "        \"base_retail_rough\": 6850,\n" +
                "        \"publish_date\": \"4/20/2017\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"warning_count\": 0,\n" +
                "  \"message_list\": [\n" +
                "  ]\n" +
                "}";
        JsonObject jsonObject = new JsonParser().parse(abc).getAsJsonObject();
        System.out.println(jsonObject.getAsJsonObject("used_vehicles").getAsJsonArray("used_vehicle_list").get(0).getAsJsonObject().get("base_whole_xclean"));


    }

}



