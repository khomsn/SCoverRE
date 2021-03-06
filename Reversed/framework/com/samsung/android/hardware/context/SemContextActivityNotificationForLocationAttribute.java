package com.samsung.android.hardware.context;

import android.os.BaseBundle;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.Log;
import java.util.ArrayList;

public class SemContextActivityNotificationForLocationAttribute extends SemContextAttribute {
    public static final Creator<SemContextActivityNotificationForLocationAttribute> CREATOR = new C01371();
    private static final int STATUS_MAX = 5;
    private static final String TAG = "SemContextActivityNotificationForLocationAttribute";
    private int[] mActivityFilter = new int[]{4};
    private int mDuration = 30;

    static class C01371 implements Creator<SemContextActivityNotificationForLocationAttribute> {
        C01371() {
        }

        public SemContextActivityNotificationForLocationAttribute createFromParcel(Parcel parcel) {
            return new SemContextActivityNotificationForLocationAttribute(parcel);
        }

        public SemContextActivityNotificationForLocationAttribute[] newArray(int i) {
            return new SemContextActivityNotificationForLocationAttribute[i];
        }
    }

    SemContextActivityNotificationForLocationAttribute() {
        setAttribute();
    }

    SemContextActivityNotificationForLocationAttribute(Parcel parcel) {
        super(parcel);
    }

    public SemContextActivityNotificationForLocationAttribute(int[] iArr, int i) {
        this.mActivityFilter = iArr;
        this.mDuration = i;
        setAttribute();
    }

    private void setAttribute() {
        BaseBundle bundle = new Bundle();
        bundle.putIntArray("activity_filter", this.mActivityFilter);
        bundle.putInt("duration", this.mDuration);
        super.setAttribute(30, bundle);
    }

    public boolean checkAttribute() {
        if (this.mActivityFilter == null) {
            return false;
        }
        ArrayList arrayList = new ArrayList();
        int i = 0;
        while (i < this.mActivityFilter.length) {
            if ((this.mActivityFilter[i] < 0 || this.mActivityFilter[i] > 5) && this.mActivityFilter[i] != 30) {
                Log.e(TAG, "The activity status is wrong.");
                return false;
            }
            arrayList.add(Integer.valueOf(this.mActivityFilter[i]));
            for (int i2 = 0; i2 < i; i2++) {
                if (((Integer) arrayList.get(i)).equals(arrayList.get(i2))) {
                    Log.e(TAG, "This activity status cannot have duplicated status.");
                    return false;
                }
            }
            i++;
        }
        if (this.mDuration >= 0) {
            return true;
        }
        Log.e(TAG, "The duration is wrong.");
        return false;
    }
}
