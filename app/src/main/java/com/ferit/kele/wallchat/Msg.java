package com.ferit.kele.wallchat;

/**
 * Created by Kele on 14.7.2017..
 */

class Msg {
    String mNickname, mTime, mMessage, mLocation;
    int mType;

    public Msg(String nickname, String message, String time, String location, int type) {
        mNickname = nickname;
        mTime = time;
        mMessage = message;
        mLocation = location;
        mType = type;

    }

    public String getNickname() {
        return mNickname;
    }

    public String getTime() {
        return mTime;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getLocation(){
        return mLocation;
    }

    public int getType() {
        return mType;
    }


}
