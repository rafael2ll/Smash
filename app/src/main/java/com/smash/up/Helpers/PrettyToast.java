package com.smash.up.Helpers;

import android.content.*;
import android.support.annotation.*;
import android.view.*;
import android.widget.*;
import com.smash.up.*;
import android.graphics.drawable.*;
import android.graphics.*;

/*
* Copyright (C) 2016 Alexander Crospenko
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
public class PrettyToast extends Toast {

    private static final int DEFAULT_TEXT_SIZE = 20;
    private static final int DEFAULT_ICON_SIZE = 35;
    private static final int DEFAULT_TOAST_DURATION = LENGTH_LONG;
    @ColorRes
    private static final int DEFAULT_ICON_COLOR = Color.WHITE;
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
    @LayoutRes
    private static final int DEFAULT_LAYOUT_RESOURCE = R.layout.toast_base_correct;
    private boolean mIsCreatedFromCustomResource = false;

    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public PrettyToast(Context context) {
        super(context);
    }

    /**
     * Please consider to call this method if you wanna to use icons
     */
    
    public static void showInfo(Context context, String msg) {
        showToastBase(context, R.drawable.background_toast_blue, msg);
    }

    
    public static void showError(Context context, String msg) {
        showToastBase(context, R.layout.toast_base_wrong, msg);
    }

    
    // Warning default toast methods
    public static void showWarning(Context context, String msg) {
        showToastBase(context, R.drawable.background_toast_yellow, msg);
    }

      // Success toast methods

    public static void showSuccess(Context context, String msg) {
        showToastBase(context, R.layout.toast_base_correct, msg);
    }

     // Dim toast methods

    public static void showDim(Context context, String msg) {
        showToastBase(context, R.drawable.background_toast_gray, msg);
    }

    
    // Base toast methods used by others, for the sake of cleaner code

    public static void showToastBase(Context context, int backgroundResource, String msg) {
        new Builder(context)
                .withBackgroundResource(backgroundResource)
                .withMessage(msg)
                .build().show();
    }

    /**
     * Gravity info class, just for encapsulating all required data for setting toast gravity
     */
    public static class Gravity {
        private int mGravity;
        private int mXOffset;
        private int mYOffset;

        public Gravity(int gravity, int XOffset, int YOffset) {
            mGravity = gravity;
            mXOffset = XOffset;
            mYOffset = YOffset;
        }

        public int getGravity() {
            return mGravity;
        }

        public void setGravity(int gravity) {
            mGravity = gravity;
        }

        public int getXOffset() {
            return mXOffset;
        }

        public void setXOffset(int XOffset) {
            mXOffset = XOffset;
        }

        public int getYOffset() {
            return mYOffset;
        }

        public void setYOffset(int YOffset) {
            mYOffset = YOffset;
        }
    }

    /**
     * Builder class for creating different types of toasts
     */
    public static class Builder {
		private Context mContext;
        private String mMessage;
        private int mShowDuration;
        private int mBackgroundResource;
        private int mTextColor;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder withMessage(String message) {
            this.mMessage = message;
            return this;
        }

        public Builder withDuration(int duration) {
            this.mShowDuration = duration;
            return this;
        }

        public Builder withTextColor(int color) {
            this.mTextColor = color;
            return this;
        }

        public Builder withBackgroundResource(@DrawableRes int backgroundResource) {
            this.mBackgroundResource = backgroundResource;
            return this;
        }

        public PrettyToast build() {
            PrettyToast toast = new PrettyToast(mContext);
         
            View  view = LayoutInflater.from(mContext).inflate(mBackgroundResource, null, false);
            Button mToast = (Button) view.findViewById(R.id.toast_baseButton);
            if (mMessage != null) {
                 mToast.setVisibility(View.VISIBLE);
                 mToast.setText(mMessage);
                 mToast.setTextColor(mTextColor > 0 ? mContext.getResources().getColor(mTextColor) : DEFAULT_ICON_COLOR);
            }
            
			toast.setView(view);
            toast.setDuration(mShowDuration >= 0 ? mShowDuration : DEFAULT_TOAST_DURATION);
            
			return toast;
        }
    }
}
