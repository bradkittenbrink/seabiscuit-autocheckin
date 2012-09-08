package com.coffeeandpower.utils;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.UserResume;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.location.LocationDetectionService;
import com.coffeeandpower.location.LocationDetectionStateMachine;
import com.coffeeandpower.views.CustomDialog;
import com.google.android.maps.GeoPoint;

public class Executor {

    public static final int HANDLE_GET_USER_RESUME = 1600;
    public static final int HANDLE_SENDING_PROP = 1601;
    public static final int HANDLE_GET_VENUES_AND_USERS_IN_BOUNDS = 1602;
    public static final int HANDLE_GET_USER_DATA = 1603;
    public static final int HANDLE_VENUES_CLOSE_TO_LOCATION = 1604;
    public static final int HANDLE_SEND_FRIEND_REQUEST = 1605;
    public static final int HANDLE_ADD_PLACE = 1606;
    public static final int HANDLE_GET_CONTACTS_LIST = 1607;
    public static final int HANDLE_ONE_ON_ONE_CHAT_HISTORY = 1608;
    public static final int HANDLE_SEND_CHAT_MESSAGE = 1609;
    public static final int HANDLE_CHECK_IN = 1610;
    public static final int HANDLE_GET_CHECHED_USERS_IN_FOURSQUARE = 1611;
    public static final int HANDLE_ENTER_INVITATION_CODE = 1612;
    public static final int HANDLE_GET_INVITATION_CODE = 1613;
    public static final int HANDLE_SAVE_USER_JOB_CATEGORY = 1614;
    public static final int HANDLE_SET_USER_PROFILE_DATA = 1615;
    public static final int HANDLE_UPLOAD_USER_PROFILE_PHOTO = 1616;
    public static final int HANDLE_GET_USER_TRANSACTION_DATA = 1617;
    public static final int HANDLE_VENUE_FEED = 1620;
    public static final int HANDLE_SEND_VENUE_FEED = 1621;
    public static final int HANDLE_GET_SKILLS = 1622;
    public static final int HANDLE_SET_SKILL = 1623;
    public static final int HANDLE_GET_PROFILE_VISIBILITY = 1624;
    public static final int HANDLE_SET_PROFILE_VISIBILITY = 1625;
    public static final int HANDLE_ACCOUNT_DELETE_SUCCEEDED = 1626;
    public static final int HANDLE_ACCOUNT_DELETE_FAILED = 1627;
    public static final int HANDLE_GET_POSTABLE_VENUES = 1628;
    public static final int HANDLE_SENDING_PLUS_ONE = 1629;
    public static final int HANDLE_USER_LINKEDIN_SKILLS = 1630;
    public static final int HANDLE_USER_LINKEDIN_SKILL_UPDATE = 1631;
    

    private DataHolder result;

    private ProgressDialog progress;

    private Context context;

    public interface ExecutorInterface {
        public void onActionFinished(int action);

        public void onErrorReceived();
    };

    ExecutorInterface exeInter = new ExecutorInterface() {
        @Override
        public void onActionFinished(int action) {
            Log.d("Executor", "onActionFinished...");
        }

        @Override
        public void onErrorReceived() {
        }
    };

    public void setExecutorListener(ExecutorInterface exeInter) {
        this.exeInter = exeInter;
    }

    public Executor(Context context) {
        this.context = context;
        progress = new ProgressDialog(context); 
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Log.d("Executor", "handleMessage()");

            progress.dismiss();

            if (msg.what == AppCAP.HTTP_ERROR) {
                exeInter.onErrorReceived();
                new CustomDialog(context, "Error", result.getResponseMessage())
                        .show();
            } else {
                exeInter.onActionFinished(msg.what);
            }

        }
    };
    private TextView counter;

    public synchronized DataHolder getResult() {
        return result;
    }

    public synchronized void getResumeForUserId(final int userId) {
        progress.setMessage("Loading");
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection().getUserResume(userId);
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.getResumeForUserId").start();
    }

    public synchronized void sendPlusOneForLove(final int post_id) {
        progress.setMessage("Sending...");
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection().sendPlusOneForLove(post_id);
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.sendReview").start();
    }

    public synchronized void sendReview(final UserResume userResume,
            final String review) {
        progress.setMessage("Sending...");
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection().sendReview(userResume, review);
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.sendReview").start();
    }

    public synchronized void getVenuesAndUsersWithCheckinsInBoundsDuringInterval(
            final double[] coords, boolean withProgress) {
        if (withProgress) {
            progress.setMessage("Loading...");
            progress.show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection()
                        .getVenuesAndUsersWithCheckinsInBoundsDuringInterval(
                                coords, 7);
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.getVenuesAndUsersWithCheckinsInBoundsDuringInterval")
                .start();
    }

    public synchronized void getUserData() {
        progress.setMessage("Loading...");
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection().getUserData();
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.getUserData").start();
    }

    public synchronized void getVenuesCloseToLocation(final GeoPoint gp,
            final int number) {
        progress.setMessage("Loading nearby places...");
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection().getVenuesCloseToLocation(gp,
                        number);
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.getVenuesCloseToLocation").start();
    }

    public synchronized void sendFriendRequest(final int userId) {
        progress.setMessage("Sending Request...");
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection().sendContactRequestToUserId(userId);
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.sendFriendRequest").start();
    }

    public synchronized void acceptContactExchangeRequest(final int userId) {
        progress.setMessage("Completing contact exchange...");
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection().sendAcceptContactRequestFromUserId(userId);
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.sendAcceptContactRequestFromUserId").start();
    }

    public synchronized void declineContactExchangeRequest(final int userId) {
        progress.setMessage("Canceling contact exchange...");
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection().sendDeclineContactRequestFromUserId(userId);
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.sendDeclineContactRequestFromUserId").start();
    }

    public synchronized void addPlace(final String name) {
        progress.setMessage("Saving new place...");
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection().addPlace(name,
                        AppCAP.getUserCoordinates());
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.addPlace").start();
    }

    /**
     * public synchronized void getContactsList() {
     * progress.setMessage("Loading..."); progress.show(); new Thread(new
     * Runnable() {
     * 
     * @Override public void run() { result =
     *           AppCAP.getConnection().getContactsList();
     *           handler.sendEmptyMessage(result.getHandlerCode()); }
     *           },"Executor.getContactsList").start(); }
     **/
    public synchronized void getOneOnOneChatHistory(final int userId) {
        progress.setMessage("Loading chat...");
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection().getOneOnOneChatHistory(userId);
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.getOneOnOneChatHistory").start();
    }

    public synchronized void sendOneOnOneChatMessage(final int userId,
            final String mess) {
        progress.setMessage("Loading...");
        // progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection().sendOneOnOneChatMessage(userId,
                        mess);
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.sendOneOnOneChatMessage").start();
    }

    public synchronized void getUsersCheckedInAtFoursquareID(
            final String venueId) {
        progress.setMessage("Loading...");
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection()
                        .getUsersCheckedInAtFoursquareID(venueId);
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.getUsersCheckedInAtFoursquareID").start();
    }

    public synchronized void checkIn(final VenueSmart venue,
            final int checkInTime, final int checkOutTime,
            final String statusText, final boolean userDesiresAutoCheckin,
            final boolean checkinIsAutoCheckin, final Context context) {

        if (!checkinIsAutoCheckin) {

            progress.setMessage("Checking in...");
            progress.show();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection().checkIn(venue, checkInTime,
                        checkOutTime, statusText, checkinIsAutoCheckin);
                AppCAP.addVenueToUserCheckinList(venue.getVenueId());

                // Add current venue to list of venues with user checkins
                int venueId = venue.getVenueId();

                if (venueId == 0) {
                    try {
                        JSONObject json = new JSONObject(
                                result.getResponseMessage());
                        if (json != null) {

                            int newVenueId = json.optInt("venue_id");
                            if (newVenueId != 0) {
                                venueId = newVenueId;
                                venue.setVenueId(venueId);
                            }
                        }
                    } catch (JSONException e) {
                        Log.d("Executor",
                                "JSON Error: Checkin failed to return venue ID, and we didn't have a venue ID cached...");
                    }
                }

                if (venueId != 0) {
                    AppCAP.queueLocalNotificationForVenue(context, venue, checkOutTime);
                    // If user selected auto checkin, save that to preferences
                    if (userDesiresAutoCheckin) {
                        LocationDetectionService
                                .addVenueToAutoCheckinList(venue);
                        if (LocationDetectionStateMachine.stateMachineActive == false) {
                            AppCAP.enableAutoCheckin(context);
                        }
                    }

                    // FIXME
                    // This assumes that the checkin is going to be successful,
                    // it doesn't look like there
                    // is currently any code to verify that the checkin was
                    // successful
                    LocationDetectionStateMachine.checkinCheckoutCOMPLETE();
                }
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.checkIn").start();
    }

    public synchronized void checkOut() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection().checkOut();
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.checkOut").start();
    }

    public synchronized void enterInvitationCode(final String invitationCode,
            final double lat, final double lng) {
        progress.setMessage("Checking...");
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection().enterInvitationCode(
                        invitationCode, lat, lng);
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.enterInvitationCode").start();
    }

    public synchronized void getInvitationCode(final double[] latLong) {
        progress.setMessage("Generating Code...");
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection().getInvitationCodeForLocation(
                        latLong[0], latLong[1]);
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.getInvitationCode").start();

    }

    public synchronized void saveUserJobCategory(final String selectedMajorJob,
            final String selectedMinorJob) {
        progress.setMessage("Uploading...");
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection().saveUserJobCategory(
                        selectedMajorJob, selectedMinorJob);
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.saveUserJobCategory").start();
    }

    public synchronized void setUserProfileData(final UserSmart user,
            final boolean isEmailChanged) {
        progress.setMessage("Uploading...");
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection().setUserProfileData(user,
                        isEmailChanged);
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.setUserProfileData").start();
    }

    public synchronized void uploadUserProfilePhoto() {
        progress.setMessage("Uploading photo...");
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection().uploadUserProfilePhoto();
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.uploadUserProfilePhoto").start();
    }

    public synchronized void getUserTransactionData() {
        progress.setMessage("Loading...");
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection().getUserTransactionData();
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.getUserTransactionData").start();
    }


    public synchronized void deleteAccount() {
        progress.setMessage("Processing...");
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection().deleteUserAccount();
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }).start();
    }

    /**
     * Get/send venue feeds <br>
     * getObject() instance of ArrayList <br>
     * <br>
     * [0]String lastId [1]String firstDate [2]String lastDate
     * [3]ArrayList(VenueChatEntry)
     * 
     * @param venueId
     * @param lastChatIDString
     */
    public synchronized void postableVenues() {
            progress.setMessage("Loading...");
            progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection().postableVenues();
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.postableVenues").start();
    }
   /**
     * Get/send venue feeds <br>
     * getObject() instance of ArrayList <br>
     * <br>
     * [0]String lastId [1]String firstDate [2]String lastDate
     * [3]ArrayList(VenueChatEntry)
     * 
     * @param venueId
     * @param lastChatIDString
     */
    public synchronized void venueFeeds(final int venueId,
            final String venueName, final String lastChatIDString,
            final String message, final boolean isSend, boolean withProgress,
            final String messageType) {
        if (withProgress) {
            progress.setMessage(isSend ? "Sending..." : "Loading...");
            progress.show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection().venueFeedsForVenueWithID(
                        venueId, venueName, lastChatIDString, message, isSend, messageType);
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.venueFeeds").start();
    }
    
    public synchronized void newPost(final int venueId,
            final String venueName, final String lastChatIDString,
            final String message, boolean withProgress,
            final String messageType, final int original_post_id) {
        if (withProgress) {
            progress.setMessage("Sending...");
            progress.show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection().newPost(
                        venueId, venueName, lastChatIDString, message, messageType, original_post_id);
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.venueFeeds").start();
    }

    public TextView getCounter() {
        return counter;
    }
    
    public void setCounter(TextView counter) {
        this.counter = counter;
    }
    
    public void getSkillsForUser() {
        progress.setMessage("Loading linkedin skills ...");
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection().getSkillsForUser();
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.getOneOnOneChatHistory").start();
    }
    
    public synchronized void changeSkillVisibility(final int skill_id, final int visible)  {
        progress.setMessage("Update skill...");
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = AppCAP.getConnection().changeSkillVisibility(skill_id, visible) ;
                handler.sendEmptyMessage(result.getHandlerCode());
            }
        }, "Executor.changeSkillVisibility").start();
    }

}
