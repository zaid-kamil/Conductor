package com.bluelinelabs.conductor;

import android.os.Parcel;
import android.os.Parcelable;

public class CallState implements Parcelable {

    public int changeStartCalls;
    public int changeEndCalls;
    public int createViewCalls;
    public int attachCalls;
    public int destroyViewCalls;
    public int detachCalls;
    public int destroyCalls;
    public int saveInstanceStateCalls;
    public int restoreInstanceStateCalls;
    public int saveViewStateCalls;
    public int restoreViewStateCalls;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CallState callState = (CallState)o;

        if (changeStartCalls != callState.changeStartCalls) {
            return false;
        }
        if (changeEndCalls != callState.changeEndCalls) {
            return false;
        }
        if (createViewCalls != callState.createViewCalls) {
            return false;
        }
        if (attachCalls != callState.attachCalls) {
            return false;
        }
        if (destroyViewCalls != callState.destroyViewCalls) {
            return false;
        }
        if (detachCalls != callState.detachCalls) {
            return false;
        }
        if (destroyCalls != callState.destroyCalls) {
            return false;
        }
        if (saveInstanceStateCalls != callState.saveInstanceStateCalls) {
            return false;
        }
        if (saveViewStateCalls != callState.saveViewStateCalls) {
            return false;
        }
        if (restoreViewStateCalls != callState.restoreViewStateCalls) {
            return false;
        }
        return restoreInstanceStateCalls == callState.restoreInstanceStateCalls;
    }

    @Override
    public int hashCode() {
        int result = changeStartCalls;
        result = 31 * result + changeEndCalls;
        result = 31 * result + createViewCalls;
        result = 31 * result + attachCalls;
        result = 31 * result + destroyViewCalls;
        result = 31 * result + detachCalls;
        result = 31 * result + destroyCalls;
        result = 31 * result + saveInstanceStateCalls;
        result = 31 * result + restoreInstanceStateCalls;
        result = 31 * result + saveViewStateCalls;
        result = 31 * result + restoreViewStateCalls;
        return result;
    }

    @Override
    public String toString() {
        return "\nCallState{" +
                "\n    changeStartCalls=" + changeStartCalls +
                "\n    changeEndCalls=" + changeEndCalls +
                "\n    createViewCalls=" + createViewCalls +
                "\n    attachCalls=" + attachCalls +
                "\n    destroyViewCalls=" + destroyViewCalls +
                "\n    detachCalls=" + detachCalls +
                "\n    destroyCalls=" + destroyCalls +
                "\n    saveInstanceStateCalls=" + saveInstanceStateCalls +
                "\n    restoreInstanceStateCalls=" + restoreInstanceStateCalls +
                "\n    saveViewStateCalls=" + saveViewStateCalls +
                "\n    restoreViewStateCalls=" + restoreViewStateCalls +
                "}\n";
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(changeStartCalls);
        out.writeInt(changeEndCalls);
        out.writeInt(createViewCalls);
        out.writeInt(attachCalls);
        out.writeInt(destroyViewCalls);
        out.writeInt(detachCalls);
        out.writeInt(destroyCalls);
        out.writeInt(saveInstanceStateCalls);
        out.writeInt(restoreInstanceStateCalls);
        out.writeInt(saveViewStateCalls);
        out.writeInt(restoreViewStateCalls);
    }

    public static final Parcelable.Creator<CallState> CREATOR  = new Parcelable.Creator<CallState>() {
        public CallState createFromParcel(Parcel in) {
            CallState state = new CallState();

            state.changeStartCalls = in.readInt();
            state.changeEndCalls = in.readInt();
            state.createViewCalls = in.readInt();
            state.attachCalls = in.readInt();
            state.destroyViewCalls = in.readInt();
            state.detachCalls = in.readInt();
            state.destroyCalls = in.readInt();
            state.saveInstanceStateCalls = in.readInt();
            state.restoreInstanceStateCalls = in.readInt();
            state.saveViewStateCalls = in.readInt();
            state.restoreViewStateCalls = in.readInt();

            return state;
        }

        public CallState[] newArray(int size) {
            return new CallState[size];
        }
    };
}