package com.samsung.android.hardware.context;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;

public class SemContextCallMotion extends SemContextEventContext {
    public static final int ACTION = 1;
    public static final Creator<SemContextCallMotion> CREATOR = new C01501();
    public static final int NONE = 0;
    private Bundle mContext;

    static class C01501 implements Creator<SemContextCallMotion> {
        C01501() {
        }

        public SemContextCallMotion createFromParcel(Parcel parcel) {
            return new SemContextCallMotion(parcel);
        }

        public SemContextCallMotion[] newArray(int i) {
            return new SemContextCallMotion[i];
        }
    }

    SemContextCallMotion() {
        this.mContext = new Bundle();
    }

    SemContextCallMotion(Parcel parcel) {
        readFromParcel(parcel);
    }

    private void readFromParcel(Parcel parcel) {
        this.mContext = parcel.readBundle(getClass().getClassLoader());
    }

    public int getAction() {
        return this.mContext.getInt("Action");
    }

    public void setValues(Bundle bundle) {
        this.mContext = bundle;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBundle(this.mContext);
    }
}
