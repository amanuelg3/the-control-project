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

package com.samdroid.app.control2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;

public class AnalogStickView extends View {
		
	private Paint BGPaint, ShadowPaint;
	private int[] center = new int[2];
	private int[] pos = new int[2];
	private float radius;
	private int pid, drawSize = 0;
	private boolean touching = false;
	private OnStickMoveInterface handler;
	private Resources res;
	private Drawable centerNorm;
	private Drawable centerPress;
	
	interface OnStickMoveInterface{
	   void onMove(View v,int x, int y);
	}
	
	public AnalogStickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public AnalogStickView(Context context) {
		super(context);
		init();
	}
	
	private void init() {
			res = this.getResources();
			centerNorm = res.getDrawable(R.drawable.dpad_center_normal);
			centerPress = res.getDrawable(R.drawable.dpad_center_pressed);
			
	    	BGPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	    	BGPaint.setARGB(96,128,128,128);
	    	
	    	ShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	    	ShadowPaint.setARGB(50,150,150,150);
	    	
	    	pos[0] = -1;
	    	pos[1] = -1;
	}
	
	public void setOnStickMoveListener(OnStickMoveInterface listener){
		handler = listener;
	}
	
	protected void onSizeChanged(int w, int h, int oldW, int oldH) {
		   int xpad = getPaddingLeft() + getPaddingRight();
		   int ypad = getPaddingTop() + getPaddingBottom();
		   
		   drawSize = w/5;
		   radius = (Math.min(w - xpad, h - ypad)/2)-drawSize;
		   center[0] = (w - xpad)/2;
		   center[1] = (h - ypad)/2;
		   
		   
		   BGPaint.setShader(new LinearGradient(0, 0, w, h, Color.argb(128, 128, 128, 128), Color.argb(128, 196, 196, 196), Shader.TileMode.CLAMP));
		   
		   if (pos[0] == -1) {
			   pos[0] = center[0];
			   pos[1] = center[1];
		   }
	}
	
	protected void onDraw(Canvas c) {
		super.onDraw(c);
		
		c.drawCircle(center[0], center[1], radius, BGPaint);
		if (touching) {
			c.drawCircle(pos[0]+2, pos[1]+2, drawSize+2, ShadowPaint);
			centerPress.setBounds(new Rect(pos[0]-drawSize,pos[1]-drawSize,pos[0]+drawSize,pos[1]+drawSize));
			centerPress.draw(c);
		} else {
			c.drawCircle(pos[0]-4, pos[1]-4, drawSize+2, ShadowPaint);
			centerNorm.setBounds(new Rect(pos[0]-drawSize,pos[1]-drawSize,pos[0]+drawSize,pos[1]+drawSize));
			centerNorm.draw(c);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_UP:
			touching = false;
			pos[0] = center[0];
			pos[1] = center[1];
			pid = -1;
			if(handler!=null){
				handler.onMove(this, 0, 0);
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			touching = false;
			pos[0] = center[0];
			pos[1] = center[1];
			pid = -1;
			if(handler!=null){
				handler.onMove(this, 0, 0);
			}
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
			break;
		case MotionEvent.ACTION_MOVE:
			touching = true;
			pos[0] = (int) event.getX(pid);
			pos[1] = (int) event.getY(pid);
			int vx = pos[0] - center[0];
			int n1 = vx;
			if (vx < 0) {
				n1 = -vx;
			}
			int vy = pos[1] - center[1];
			int n2 = vy;
			if (vy < 0) {
				n2 = -vy;
			}
			if (n1 + n2 > radius) {
				double magV = Math.sqrt(vx*vx+vy*vy);
				pos[0] = (int) (center[0] + vx / magV * radius);
				pos[1] = (int) (center[1] + vy / magV * radius);
			}
			if(handler!=null){
				handler.onMove(this, (int)(((pos[0]-center[0])/radius)*100), (int)(((pos[1]-center[1])/radius)*100));
			}
			break;
		}
		invalidate();
		return true;
	}
	
}
