package com.coffeeandpower.cont;

import java.util.ArrayList;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.coffeeandpower.Constants;

public class Feed implements Parcelable {

    public static final String FEED_TYPE_UPDATE = "update";
    public static final String FEED_TYPE_QUESTION = "question";
    public static final String FEED_TYPE_LOVE = "love";

    private int id;
    private String date;
    private String entryText;
    private int authorId;
    private int receiverId;
    private String authorNickName;
    private String receiverNickName;
    private String authorPhotoUrl;
    private String receiverPhotoUrl;
    private int skillId;
    private String skillName;
    private String entryType;
    private int venueId;
    private int originalPostId;
    private int likeCount;
    private int userHasLiked;
    private ArrayList<Feed> replyFeeds;


    public Feed(int id, String date, String entryText, int authorId,
            int receiverId, String authorNickName, String receiverNickName,
            String authorPhotoUrl, String receiverPhotoUrl, int skillId,
            String entryType, int venueId, int originalPostId, int likeCount,
            int userHasLiked) {
        this.id = id;
        this.date = date;
        this.entryText = entryText;
        this.authorId = authorId;
        this.receiverId = receiverId;
        this.authorNickName = authorNickName;
        this.receiverNickName = receiverNickName;
        this.authorPhotoUrl = authorPhotoUrl;
        this.receiverPhotoUrl = receiverPhotoUrl;
        this.skillId = skillId;
        this.entryType = entryType;
        this.venueId = venueId;
        this.originalPostId = originalPostId;
        this.likeCount = likeCount;
        this.userHasLiked = userHasLiked;
        this.replyFeeds = new ArrayList<Feed>();
    }

    
    public Feed(JSONObject objFeed) throws Exception
    {
        super();        
        this.id = objFeed.optInt("id");
        this.date = objFeed.optString("date");
        this.entryText = objFeed.optString("entry");
        JSONObject author = objFeed.optJSONObject("author");
        if (author != null) {
            this.authorId = author.optInt("id");
            this.authorNickName = author.optString("nickname");
            this.authorPhotoUrl = author.optString("filename");
        }
        JSONObject receiver = objFeed.optJSONObject("receiver");
        if (receiver != null) {
            this.receiverId = receiver.optInt("id");
            this.receiverNickName = receiver.optString("nickname");
            this.receiverPhotoUrl = receiver.optString("filename");
        }

        JSONObject skill = objFeed.optJSONObject("skill");
        if (skill != null) {
            this.skillId = skill.optInt("id");
            this.skillName = skill.optString("name");
        }
        
        
        this.entryType = objFeed.optString("type");
        this.venueId = objFeed.optInt("venueId");
        this.originalPostId = objFeed.optInt("original_post_id");
        this.likeCount = objFeed.optInt("like_count");
        this.userHasLiked = objFeed.optInt("user_has_liked");
        this.replyFeeds = new ArrayList<Feed>();
    }

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date = date;
    }

    public String getFormattedEntryText() {
        String mes;
        if (entryType.contentEquals(Feed.FEED_TYPE_QUESTION)) {
            mes = "Question from " + authorNickName + ": " + entryText;
        } else {
            mes = entryText;
        }
        return mes;
    }

    public String getEntryText() {
        return entryText;
    }


    public void setEntryText(String entryText) {
        this.entryText = entryText;
    }


    public int getAuthorId() {
        return authorId;
    }


    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }


    public int getReceiverId() {
        return receiverId;
    }


    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }


    public String getAuthorNickName() {
        return authorNickName;
    }


    public void setAuthorNickName(String authorNickName) {
        this.authorNickName = authorNickName;
    }


    public String getReceiverNickName() {
        return receiverNickName;
    }


    public void setReceiverNickName(String receiverNickName) {
        this.receiverNickName = receiverNickName;
    }


    public String getAuthorPhotoUrl() {
        if (authorPhotoUrl.contentEquals("images/no_picture.jpg") == true) {
            authorPhotoUrl = "";
        }
        return authorPhotoUrl;
    }


    public void setAuthorPhotoUrl(String authorPhotoUrl) {
        if (authorPhotoUrl.contentEquals("images/no_picture.jpg") == true) {
            authorPhotoUrl = "";
        }
        this.authorPhotoUrl = authorPhotoUrl;
    }


    public String getReceiverPhotoUrl() {
        return receiverPhotoUrl;
    }


    public void setReceiverPhotoUrl(String receiverPhotoUrl) {
        this.receiverPhotoUrl = receiverPhotoUrl;
    }


    public int getSkillId() {
        return skillId;
    }


    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }


    public String getEntryType() {
        return entryType;
    }


    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }


    public int getVenueId() {
        return venueId;
    }


    public void setVenueId(int venueId) {
        this.venueId = venueId;
    }


    public int getOriginalPostId() {
        return originalPostId;
    }


    public void setOriginalPostId(int originalPostId) {
        this.originalPostId = originalPostId;
    }


    public int getLikeCount() {
        return likeCount;
    }


    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }


    public int getUserHasLiked() {
        return userHasLiked;
    }


    public void setUserHasLiked(int userHasLiked) {
        this.userHasLiked = userHasLiked;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }
    
    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.id);
        out.writeInt(this.authorId);
        out.writeInt(this.receiverId);
        out.writeInt(this.skillId);
        out.writeInt(this.venueId);
        out.writeInt(this.originalPostId);
        out.writeInt(this.likeCount);
        out.writeInt(this.userHasLiked);
        out.writeString(this.date);
        out.writeString(this.entryText);
        out.writeString(this.authorNickName);
        out.writeString(this.receiverNickName);
        out.writeString(this.authorPhotoUrl);
        out.writeString(this.receiverPhotoUrl);
        out.writeString(this.entryType);
    }

    public static final Parcelable.Creator<Feed> CREATOR = new Parcelable.Creator<Feed>() {
            public Feed createFromParcel(Parcel in) {
                return new Feed(in);
            }

            public Feed[] newArray(int size) {
                return new Feed[size];
            }
    };

    private Feed(Parcel in) {
        this.id = in.readInt();
        this.authorId = in.readInt();
        this.receiverId = in.readInt();
        this.skillId = in.readInt();
        this.venueId = in.readInt();
        this.originalPostId = in.readInt();
        this.likeCount = in.readInt();
        this.userHasLiked = in.readInt();
        this.date = in.readString();
        this.entryText = in.readString();
        this.authorNickName = in.readString();
        this.receiverNickName = in.readString();
        this.authorPhotoUrl = in.readString();
        this.receiverPhotoUrl = in.readString();
        this.entryType = in.readString();
        this.replyFeeds = new ArrayList<Feed>();
    }


    public ArrayList<Feed> getReplyFeeds() {
        return replyFeeds;
    }


    public void setReplyFeeds(ArrayList<Feed> replyFeeds) {
        this.replyFeeds = replyFeeds;
    }


    public void attachToFeedsArray(ArrayList<Feed> feedsArray) {
        if (originalPostId != 0) {
            Feed originalFeed = searchFeedInFeedsArray(feedsArray, originalPostId);
            if (originalFeed != null) {
                ArrayList<Feed> replyFeeds = originalFeed.getReplyFeeds();
                replyFeeds.add(this);
            } else {
                Log.d("Feed", "Feed id not found in attachToFeedsArray: "
                        + originalPostId);
            }
        }
        
    }


    private Feed searchFeedInFeedsArray(ArrayList<Feed> feedsArray,
            int originalPostId2) {
        Feed returnedFeed = null;
        for (Feed currFeed : feedsArray) {
            if (currFeed.getId() == originalPostId2) {
                returnedFeed = currFeed;
                break;
            }
        }
        return returnedFeed;
    }

}
