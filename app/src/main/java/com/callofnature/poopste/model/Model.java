package com.callofnature.poopste.model;

/**
 * Created by vinceurag on 06/03/2017.
 */

public class Model {
    private static String fullName;
    private static int userId;
    private static String googleId;
    private static String profilePic;
    private static String token;

    public static String getFullName() {
        return fullName;
    }

    public static void setFullName(String fullName) {
        Model.fullName = fullName;
    }

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int userId) {
        Model.userId = userId;
    }

    public static String getGoogleId() {
        return googleId;
    }

    public static void setGoogleId(String googleId) {
        Model.googleId = googleId;
    }

    public static String getProfilePic() {
        return profilePic;
    }

    public static void setProfilePic(String profilePic) {
        Model.profilePic = profilePic;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Model.token = token;
    }
}
