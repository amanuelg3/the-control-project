/*
This file is part of Control.

Control is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Control is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Control.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.samdroid.app.control;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class InfinateSeekBarView extends View {
		
	private Paint BGPaint;
	private int[] center = new int[2];
	private int[] pos = new int[2];
	private float radius;
	private int pid, deg, steps, lastStep = 0;
	private int tf = 24;
	private int lastDeg = 10;
	private boolean touching = false;
	private OnSeekStatChangeInterface handler;
	private Resources res;
	private Drawable centerNorm, centerPress;
	
	interface OnSeekStatChangeInterface{
	   void onSeekStatChange(View v,int p);
	}
	
	public InfinateSeekBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public InfinateSeekBarView(Context context) {
		super(context);
		init();
	}
	
	private void init() {
			res = this.getResources();
			centerNorm = res.getDrawable(R.drawable.scrubber_control_normal_holo);
			centerPress = res.getDrawable(R.drawable.scrubber_control_pressed_holo);
			
	    	BGPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	    	BGPaint.setStyle(Paint.Style.STROKE);
	    	BGPaint.setARGB(64, 96, 96, 96);
	    	BGPaint.setStrokeWidth(2);
	    	BGPaint.setTextSize(72);
	    	
	    	pos[0] = -1;
	    	pos[1] = -1;
	    	
	    	tf = (int) (24*this.getResources().getDisplayMetrics().density);
	}
	
	public void setOnSeekStatChangeInterface(OnSeekStatChangeInterface listener){
		handler = listener;
	}
	
	protected void onSizeChanged(int w, int h, int oldW, int oldH) {
		   int xpad = getPaddingLeft() + getPaddingRight();
		   int ypad = getPaddingTop() + getPaddingBottom();
		   
		   radius = (Math.min(w - xpad, h - ypad)/2)-24;
		   center[0] = (w - xpad)/2;
		   center[1] = (h - ypad)/2;
		   
		   if (pos[0] == -1) {
			   pos[0] = center[0];
			   pos[1] = (int) ((int) center[1] - radius);
		   }
	}
	
	protected void onDraw(Canvas c) {
		super.onDraw(c);
		
		c.drawCircle(center[0], center[1], radius, BGPaint);
		if (touching) {
			centerPress.setBounds(new Rect(pos[0]-tf,pos[1]-tf,pos[0]+tf,pos[1]+tf));
			centerPress.draw(c);
		} else {
			centerNorm.setBounds(new Rect(pos[0]-tf,pos[1]-tf,pos[0]+tf,pos[1]+tf));
			centerNorm.draw(c);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_UP:
			touching = false;
			pid = -1;
			break;
		case MotionEvent.ACTION_CANCEL:
			touching = false;
			pid = -1;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)>> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
	        final int pointerId = event.getPointerId(pointerIndex);
	        if (pointerId == pid) {
	            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
	            pid = event.getPointerId(newPointerIndex);
	        }
	        break;
		case MotionEvent.ACTION_DOWN:
			touching = true;
			pid = event.getPointerId(0);
			lastDeg = (int) (Math.atan2(event.getY(pid)-center[1],event.getX(pid)-center[0]) * 180/Math.PI + 360) % 360;
			break;
		case MotionEvent.ACTION_MOVE:
			touching = true;
			pos[0] = (int) event.getX(pid);
			pos[1] = (int) event.getY(pid);
			int vx = pos[0] - center[0];
			int vy = pos[1] - center[1];
			double magV = Math.sqrt(vx*vx+vy*vy);
			pos[0] = (int) (center[0] + vx / magV * radius);
			pos[1] = (int) (center[1] + vy / magV * radius);
			deg = (int) (Math.atan2(vy, vx) * 180/Math.PI + 360) % 360;
			if ((lastDeg - deg) >= 350) {
				steps++;
				lastDeg = lastDeg + 20;
			}else if ((lastDeg - deg) <= -350) {
				steps--;
				lastDeg = lastDeg - 20;
			}else if ((lastDeg - deg) <= -20 ) {
				steps--;
				lastDeg = lastDeg + 20;
			}else if ((lastDeg - deg) >= 20) {
				steps++;
				lastDeg = lastDeg - 20;
			}
			if(handler!=null && lastStep!=steps){
				handler.onSeekStatChange(this, steps);
				lastStep = steps;
			}
			break;
		}
		invalidate();
		return true;
	}
	
}
