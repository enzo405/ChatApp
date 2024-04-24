package fr.sio.chat.app.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import fr.sio.chat.app.websocket.requests.*;

public class RequestCodec {

    private static final Gson gson = new GsonBuilder()
            .setDateFormat("MMM dd, yyyy, hh:mm:ss a") // permettre la discussion entre plusieur pays
            .create();

    public static String encode(Request request) {
        return gson.toJson(request);
    }

    public static Request decode(String json) {
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        if (!jsonObject.has("type")) {
            throw new IllegalStateException("JSON is missing the 'type' property.");
        }
        String type = jsonObject.get("type").getAsString();

        return switch (type) {
            case "LOGIN" -> gson.fromJson(json, RequestLogin.class);
            case "LOGOUT" -> gson.fromJson(json, RequestLogout.class);
            case "MESSAGE_NEW" -> gson.fromJson(json, RequestMessageNew.class);
            case "MESSAGE_EDIT" -> gson.fromJson(json, RequestMessageEdit.class);
            case "MESSAGE_DELETE" -> gson.fromJson(json, RequestMessageDelete.class);
            case "FRIEND_REQUEST" -> gson.fromJson(json, RequestFriendRequest.class);
            case "MESSAGE_SYSTEM" -> gson.fromJson(json, RequestMessageSystem.class);
            case "MEMBER_ADD_DISCUSSION" -> gson.fromJson(json, RequestMemberAddDiscussion.class);
            case "MEMBER_REMOVE_DISCUSSION" -> gson.fromJson(json, RequestMemberRemoveDiscussion.class);
            case "USER_UPDATE_PROFILE" -> gson.fromJson(json, RequestUserUpdateProfile.class);
            default -> null;
        };
    }
}