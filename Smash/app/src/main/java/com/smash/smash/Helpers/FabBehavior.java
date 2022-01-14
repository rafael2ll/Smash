package com.smash.smash.Helpers;

import android.content.*;
import android.support.design.widget.*;
import android.util.*;
import android.view.*;

public class FabBehavior extends CoordinatorLayout.Behavior<FloatingActionButton>
  {
	public FabBehavior(Context context, AttributeSet attrs) {
		super();
	}

	@Override
	public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
									   final View directTargetChild, final View target, final int nestedScrollAxes) {
		return true;
	}

	@Override
	public void onNestedScroll(final CoordinatorLayout coordinatorLayout,
							   final FloatingActionButton child,
							   final View target, final int dxConsumed, final int dyConsumed,
							   final int dxUnconsumed, final int dyUnconsumed) {
		super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
		if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
			child.hide();
		} else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
			child.show();
		}
	}
}
