
/*
 *
 * MIT License
 *
 * Copyright (c) 2020 Sarnava Konar
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sarnava.textwriter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class TextWriter extends View {

    private Paint paint;
    private Path path;
    private int DELAY=20, step, currentPosition;
    private float screenWidth, screenHeight;
    private float VERTICAL_BOUND=100f, HORIZONTAL_BOUND, GAP = 50f;
    private float x, y, sweepAngle;
    private float centreX, centreY;
    private boolean hasDrawingStarted;
    private String text;
    private char currentCharacter;
    Configuration config = Configuration.RECTANGLE;
    private Listener listener;

    public TextWriter(Context context) {
        super(context);

        init(null);
    }

    public TextWriter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public TextWriter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    public enum Configuration{

        SQUARE(1),
        RECTANGLE(2),
        INTERMEDIATE(3);

        Configuration(int config) {}
    }

    private void init(@Nullable AttributeSet attrs){

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(Color.BLACK);

        path = new Path();
    }

    /**
     * Sets the color of the paint
     *
     * @param color The paint color
     */
    public TextWriter setColor(int color){

        paint.setColor(color);
        return this;
    }

    /**
     * Sets the stroke width of the paint
     *
     * @param width paint width
     */
    public TextWriter setWidth(float width){

        paint.setStrokeWidth(width);
        return this;
    }

    /**
     * {@link #invalidate()} is called after {@param delay} milliseconds. More the value of delay,
     * more time it will take to finish drawing
     *
     * @param delay the duration in milliseconds to delay the invalidation by
     */
    public TextWriter setDelay(int delay){

        DELAY = delay;
        return this;
    }

    /**
     * More the size, more bigger will be the drawing of letters and thereby the overall drawing
     *
     * @param sizeFactor the size of letter in float
     */
    public TextWriter setSizeFactor(float sizeFactor){

        VERTICAL_BOUND = sizeFactor;
        return this;
    }

    /**
     * Sets the gap in b/w two successive letters
     *
     * @param spacing the distance in float
     */
    public TextWriter setLetterSpacing(float spacing){

        GAP = spacing;
        return this;
    }

    /**
     * Sets the configuration/shape of the drawing based on {@link Configuration} selected
     *
     * @param config Square, Rectangle or Intermediate
     */
    public TextWriter setConfig(Configuration config){

        this.config = config;
        return this;
    }

    /**
     * This is the input text that needs to be drawn
     *
     * @param text the input string
     */
    public TextWriter setText(String text){

        this.text = text;
        return this;
    }

    /**
     * Sets the {@link Listener} for getting callback after finishing drawing
     *
     * @param listener {@link Listener}
     */
    public TextWriter setListener(Listener listener){

        this.listener = listener;
        return this;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        screenWidth = getWidth();
        screenHeight = getHeight();

        if(!hasDrawingStarted) {

            if(screenWidth <= 0 || screenHeight <= 0)
                invalidate();

            return;
        }

        continueDrawing();

        canvas.drawPath(path, paint);

        postInvalidateDelayed(DELAY);
    }

    public void startAnimation() throws RuntimeException {

        if(TextUtils.isEmpty(text)){
            throw new RuntimeException("Text is null or empty");
        }

        //sets the HORIZONTAL_BOUND based on the configuration
        if(config == Configuration.SQUARE)
            HORIZONTAL_BOUND = VERTICAL_BOUND;
        else if(config == Configuration.RECTANGLE)
            HORIZONTAL_BOUND = VERTICAL_BOUND/2;
        else
            HORIZONTAL_BOUND = 3*VERTICAL_BOUND/4;

        float totalLetterWidth = 0;
        for(int i=0; i< text.length(); i++){

            if(!Character.isUpperCase(text.charAt(i)) && !Character.isSpaceChar(text.charAt(i))){

                throw new RuntimeException("Text does not follow rules");
            }

            //adds the width required to draw the particular letter
            if(text.charAt(i) == 'I')
                totalLetterWidth += 0;
            else if(text.charAt(i) == ' ')
                totalLetterWidth += HORIZONTAL_BOUND;
            else if(text.charAt(i) == 'C' || text.charAt(i) == 'G')
                totalLetterWidth += HORIZONTAL_BOUND + HORIZONTAL_BOUND*Math.cos(-315*Math.PI/180);
            else if(text.charAt(i) == 'J' || text.charAt(i) == 'U' || text.charAt(i) == 'L')
                totalLetterWidth += 3*HORIZONTAL_BOUND/2;
            else
                totalLetterWidth += 2*HORIZONTAL_BOUND;
        }

        //get only the letters excluding whitespaces
        int letters = text.replaceAll(" ", "").length();

        //calculates the total width required to draw the letters including gaps b/w letters
        float totalWidth = totalLetterWidth + GAP*(letters - 1);

        //the starting and ending x-coordinate
        float startX = (screenWidth - totalWidth)/2;
        float endX = (screenWidth + totalWidth)/2;

        //the y-coordinate of the center of the canvas for drawing a letter
        centreY = screenHeight/2;

        currentPosition = 0;
        currentCharacter = text.charAt(currentPosition);
        hasDrawingStarted = true;

        //the x-coordinate of the center of the canvas for drawing a letter
        if(currentCharacter == 'I')
            centreX = startX;
        else if(currentCharacter == ' ')
            centreX = startX + HORIZONTAL_BOUND/2;
        else if(currentCharacter == 'J' || currentCharacter == 'U' || currentCharacter == 'L')
            centreX = startX + 3*HORIZONTAL_BOUND/4;
        else
            centreX = startX + HORIZONTAL_BOUND;

        Log.e("boom", startX+"  -  "+endX+"  -  "+screenWidth);

        //start the drawing
        startDrawing();
    }

    private void startDrawingA(){

        x = centreX - HORIZONTAL_BOUND;
        y = centreY + VERTICAL_BOUND;

        path.moveTo(x, y);

        step = 1;

        invalidate();
    }

    private void startDrawingB(){

        x = centreX - HORIZONTAL_BOUND;
        y = centreY + VERTICAL_BOUND;

        path.moveTo(x, y);

        sweepAngle = 0;
        step = 1;

        invalidate();
    }

    private void startDrawingC(){

        sweepAngle = 0;

        step = 1;

        invalidate();
    }

    private void startDrawingD(){

        x = centreX - HORIZONTAL_BOUND;
        y = centreY + VERTICAL_BOUND;

        path.moveTo(x, y);

        sweepAngle = 0;
        step = 1;

        invalidate();
    }

    private void startDrawingE(){

        x = centreX + HORIZONTAL_BOUND;
        y = centreY - VERTICAL_BOUND;

        path.moveTo(x, y);

        step = 1;

        invalidate();
    }

    private void startDrawingF(){

        x = centreX + HORIZONTAL_BOUND;
        y = centreY - VERTICAL_BOUND;

        path.moveTo(x, y);

        step = 1;

        invalidate();
    }

    private void startDrawingG(){

        sweepAngle = 0;

        step = 1;

        invalidate();
    }

    private void startDrawingH(){

        x = centreX - HORIZONTAL_BOUND;
        y = centreY - VERTICAL_BOUND;

        path.moveTo(x, y);

        step = 1;

        invalidate();
    }

    private void startDrawingI(){

        x = centreX;
        y = centreY - VERTICAL_BOUND;

        path.moveTo(x, y);

        step = 1;

        invalidate();
    }

    private void startDrawingJ(){

        x = centreX + 3*HORIZONTAL_BOUND/4;
        y = centreY - VERTICAL_BOUND;

        path.moveTo(x, y);

        step = 1;
        sweepAngle = 0;

        invalidate();
    }

    private void startDrawingK(){

        x = centreX - HORIZONTAL_BOUND;
        y = centreY - VERTICAL_BOUND;

        path.moveTo(x, y);

        step = 1;

        invalidate();
    }

    private void startDrawingL(){

        x = centreX - 3*HORIZONTAL_BOUND/4;
        y = centreY - VERTICAL_BOUND;

        path.moveTo(x, y);

        step = 1;

        invalidate();
    }

    private void startDrawingM(){

        x = centreX - HORIZONTAL_BOUND;
        y = centreY + VERTICAL_BOUND;

        path.moveTo(x, y);

        step = 1;

        invalidate();
    }

    private void startDrawingN(){

        x = centreX - HORIZONTAL_BOUND;
        y = centreY + VERTICAL_BOUND;

        path.moveTo(x, y);

        step = 1;

        invalidate();
    }

    private void startDrawingO(){

        sweepAngle = 0;

        step = 1;

        invalidate();
    }

    private void startDrawingP(){

        x = centreX - HORIZONTAL_BOUND;
        y = centreY + VERTICAL_BOUND;

        path.moveTo(x, y);

        sweepAngle = 0;
        step = 1;

        invalidate();
    }

    private void startDrawingQ(){

        sweepAngle = 0;
        step = 1;

        invalidate();
    }

    private void startDrawingR(){

        x = centreX - HORIZONTAL_BOUND;
        y = centreY + VERTICAL_BOUND;

        path.moveTo(x, y);

        sweepAngle = 0;
        step = 1;

        invalidate();
    }

    private void startDrawingS(){

        sweepAngle = 0;

        step = 1;

        invalidate();
    }

    private void startDrawingT(){

        x = centreX - HORIZONTAL_BOUND;
        y = centreY - VERTICAL_BOUND;

        path.moveTo(x, y);

        step = 1;

        invalidate();
    }

    private void startDrawingU(){

        x = centreX - 3*HORIZONTAL_BOUND/4;
        y = centreY - VERTICAL_BOUND;

        path.moveTo(x, y);

        sweepAngle = 0;

        step = 1;

        invalidate();
    }

    private void startDrawingV(){

        x = centreX - HORIZONTAL_BOUND;
        y = centreY - VERTICAL_BOUND;

        path.moveTo(x, y);

        step = 1;

        invalidate();
    }

    private void startDrawingW(){

        x = centreX - HORIZONTAL_BOUND;
        y = centreY - VERTICAL_BOUND;

        path.moveTo(x, y);

        step = 1;

        invalidate();
    }

    private void startDrawingX(){

        x = centreX - HORIZONTAL_BOUND;
        y = centreY - VERTICAL_BOUND;

        path.moveTo(x, y);

        step = 1;

        invalidate();
    }

    private void startDrawingY(){

        x = centreX - HORIZONTAL_BOUND;
        y = centreY - VERTICAL_BOUND;

        path.moveTo(x, y);

        step = 1;

        invalidate();
    }

    private void startDrawingZ(){

        x = centreX - HORIZONTAL_BOUND;
        y = centreY - VERTICAL_BOUND;

        path.moveTo(x, y);

        step = 1;

        invalidate();
    }

    private void startDrawingSpace(){

        adjust();
    }

    private void drawA(){

        if(step == 1){

            if(x > centreX - HORIZONTAL_BOUND/8 && y < centreY - VERTICAL_BOUND){

                x = centreX + HORIZONTAL_BOUND/8;
                y = centreY - VERTICAL_BOUND;
                path.lineTo(x, y);
                step++;
            }
            else {

                path.lineTo(x, y);

                x+=HORIZONTAL_BOUND/4 - HORIZONTAL_BOUND/32;
                y-=VERTICAL_BOUND/2;
            }
        }
        else if(step == 2){

            if(x > centreX + HORIZONTAL_BOUND && y > centreY + VERTICAL_BOUND){

                x = centreX - HORIZONTAL_BOUND/2;
                y = centreY;
                path.moveTo(x, y);
                step++;
            }
            else {

                path.lineTo(x, y);

                x+=HORIZONTAL_BOUND/4 - HORIZONTAL_BOUND/32;
                y+=VERTICAL_BOUND/2;
            }
        }
        else if(step == 3){

            if(x > centreX + HORIZONTAL_BOUND/2){

                step++;
                adjust();
            }
            else {

                path.lineTo(x, y);

                x+=HORIZONTAL_BOUND/2;
            }
        }
    }

    private void drawB(){

        if(step == 1){

            if(y < centreY - VERTICAL_BOUND){

                x = centreX;
                y = centreY - VERTICAL_BOUND;
                path.lineTo(x, y);
                step++;
            }
            else {

                path.lineTo(x, y);
                y-=VERTICAL_BOUND/2;
            }
        }
        else if(step == 2){

            if(sweepAngle > 180){

                sweepAngle = 0;
                x = centreX - HORIZONTAL_BOUND;
                y = centreY;
                path.lineTo(x, y);
                step++;
            }
            else {

                RectF rect = new RectF();
                rect.set(centreX - HORIZONTAL_BOUND,
                        centreY - VERTICAL_BOUND,
                        centreX + HORIZONTAL_BOUND,
                        centreY);

                path.addArc (rect, 270, sweepAngle);
                sweepAngle+=45;
            }
        }
        else if(step == 3){

            if(sweepAngle > 180){

                sweepAngle = 0;
                x = centreX - HORIZONTAL_BOUND;
                y = centreY + VERTICAL_BOUND;
                path.lineTo(x, y);
                step++;
                adjust();
            }
            else {

                RectF rect = new RectF();
                rect.set(centreX - HORIZONTAL_BOUND,
                        centreY,
                        centreX + HORIZONTAL_BOUND,
                        centreY + VERTICAL_BOUND);

                path.addArc (rect, 270, sweepAngle);
                sweepAngle+=45;
            }
        }
    }

    private void drawC(){

        if(step == 1){

            if(sweepAngle < -270){

                step++;
                adjust();
            }
            else {

                RectF rect = new RectF();
                rect.set(centreX - HORIZONTAL_BOUND,
                        centreY - VERTICAL_BOUND,
                        centreX + HORIZONTAL_BOUND,
                        centreY + VERTICAL_BOUND);

                path.addArc (rect, -45, sweepAngle);

                sweepAngle-=45;
            }
        }
    }

    private void drawD(){

        if(step == 1){

            if(y < centreY - VERTICAL_BOUND){

                y = centreY - VERTICAL_BOUND;
                step++;
            }
            else {

                path.lineTo(x, y);

                y-=VERTICAL_BOUND/2;
            }
        }
        else if(step == 2){

            if(sweepAngle > 180){

                sweepAngle = 0;
                step++;
                adjust();
            }
            else {

                RectF rect = new RectF();
                rect.set(centreX - 3*HORIZONTAL_BOUND,
                        centreY - VERTICAL_BOUND,
                        centreX + HORIZONTAL_BOUND,
                        centreY + VERTICAL_BOUND);

                path.addArc (rect, 270, sweepAngle);

                sweepAngle+=45;
            }
        }
    }

    private void drawE(){

        if(step == 1){

            if(x < centreX - HORIZONTAL_BOUND){

                x = centreX - HORIZONTAL_BOUND;
                step++;
            }
            else {

                path.lineTo(x, y);

                x-=HORIZONTAL_BOUND/2;
            }
        }
        else if(step == 2){

            if(y > centreY + VERTICAL_BOUND){

                y = centreY + VERTICAL_BOUND;
                step++;
            }
            else {

                path.lineTo(x, y);

                y+=VERTICAL_BOUND/2;
            }
        }
        else if(step == 3){

            if(x > centreX + HORIZONTAL_BOUND){

                x = centreX - HORIZONTAL_BOUND;
                y = centreY;
                path.moveTo(x, y);
                step++;
            }
            else {

                path.lineTo(x, y);

                x+=HORIZONTAL_BOUND/2;
            }
        }
        else if(step == 4){

            if(x > centreX + HORIZONTAL_BOUND/2){

                step++;
                adjust();
            }
            else {

                path.lineTo(x, y);

                x+=HORIZONTAL_BOUND/2;
            }
        }
    }

    private void drawF(){

        if(step == 1){

            if(x < centreX - HORIZONTAL_BOUND){

                x = centreX - HORIZONTAL_BOUND;
                step++;
            }
            else {

                path.lineTo(x, y);

                x-=HORIZONTAL_BOUND/2;
            }
        }
        else if(step == 2){

            if(y > centreY + VERTICAL_BOUND){

                y = centreY;
                path.moveTo(x, y);
                step++;
            }
            else {

                path.lineTo(x, y);

                y+=VERTICAL_BOUND/2;
            }
        }
        else if(step == 3){

            if(x > centreX + HORIZONTAL_BOUND/2){

                step++;
                adjust();
            }
            else {

                path.lineTo(x, y);

                x+=HORIZONTAL_BOUND/2;
            }
        }
    }

    private void drawG(){

        if(step == 1){

            if(sweepAngle < -270){

                x = (float) (centreX + HORIZONTAL_BOUND*Math.cos(-315*Math.PI/180));
                y = (float) (centreY + VERTICAL_BOUND*Math.sin(-315*Math.PI/180));
                step++;
            }
            else {

                RectF rect = new RectF();
                rect.set(centreX - HORIZONTAL_BOUND,
                        centreY - VERTICAL_BOUND,
                        centreX + HORIZONTAL_BOUND,
                        centreY + VERTICAL_BOUND);

                path.addArc (rect, -45, sweepAngle);

                sweepAngle-=45;
            }
        }
        else if(step == 2){

            if(y < centreY){

                step++;
            }
            else {

                path.lineTo(x, y);

                y-=VERTICAL_BOUND/4;
            }
        }
        else if(step == 3){

            if(x < centreX){

                step++;
                adjust();
            }
            else {

                path.lineTo(x, y);

                x-=HORIZONTAL_BOUND/2;
            }
        }
    }

    private void drawH(){

        if(step == 1){

            if(y > centreY + VERTICAL_BOUND){

                y = centreY - VERTICAL_BOUND;
                x = centreX + HORIZONTAL_BOUND;
                path.moveTo(x, y);
                step++;
            }
            else {

                path.lineTo(x, y);

                y+=VERTICAL_BOUND/2;
            }
        }
        else if(step == 2){

            if(y > centreY + VERTICAL_BOUND){

                y = centreY;
                x = centreX - HORIZONTAL_BOUND;
                path.moveTo(x, y);
                step++;
            }
            else {

                path.lineTo(x, y);

                y+=VERTICAL_BOUND/2;
            }
        }
        else if(step == 3){

            if(x > centreX + HORIZONTAL_BOUND){

                step++;
                adjust();
            }
            else {

                path.lineTo(x, y);

                x+=HORIZONTAL_BOUND/2;
            }
        }
    }

    private void drawI(){

        if(step == 1){

            if(y > centreY + VERTICAL_BOUND){

                step++;
                adjust();
            }
            else {

                path.lineTo(x, y);

                y+=VERTICAL_BOUND/2;
            }
        }
    }

    private void drawJ(){

        if(step == 1){

            if(y > centreY + 3*VERTICAL_BOUND/4){

                y = centreY + 3*VERTICAL_BOUND/4;
                //path.lineTo(x, y);
                step++;
            }
            else {

                path.lineTo(x, y);

                y+=VERTICAL_BOUND/2;
            }
        }
        else if(step == 2){

            if(sweepAngle > 180){

                sweepAngle = 0;
                step++;
                adjust();
            }
            else {

                RectF rect = new RectF();
                rect.set(centreX - 3*HORIZONTAL_BOUND/4,
                        centreY,
                        centreX + 3*HORIZONTAL_BOUND/4,
                        centreY + VERTICAL_BOUND);

                path.addArc (rect, 0, sweepAngle);

                sweepAngle+=45;
            }
        }
    }

    private void drawK(){

        if(step == 1){

            if(y > centreY + VERTICAL_BOUND){

                y = centreY;
                path.moveTo(x, y);
                x = centreX - HORIZONTAL_BOUND;
                y = centreY;
                path.lineTo(x, y);
                step++;
            }
            else {

                path.lineTo(x, y);

                y+=VERTICAL_BOUND/2;
            }
        }
        else if(step == 2){

            if(x > centreX + HORIZONTAL_BOUND && y < centreY - VERTICAL_BOUND){

                x = centreX - HORIZONTAL_BOUND/2;
                y = centreY - VERTICAL_BOUND/4;
                path.moveTo(x, y);
                step++;
            }
            else {

                path.lineTo(x, y);

                x+=HORIZONTAL_BOUND/2;
                y-=VERTICAL_BOUND/4;
            }
        }
        else if(step == 3){

            if(x > centreX + HORIZONTAL_BOUND && y > centreY + VERTICAL_BOUND){

                step++;
                adjust();
            }
            else {

                path.lineTo(x, y);

                x+=3*HORIZONTAL_BOUND/4;
                y+=5*VERTICAL_BOUND/8;
            }
        }
    }

    private void drawL(){

        if(step == 1){

            if(y > centreY + VERTICAL_BOUND){

                y = centreY + VERTICAL_BOUND;
                step++;
            }
            else {

                path.lineTo(x, y);

                y+=VERTICAL_BOUND/2;
            }
        }
        else if(step == 2){

            if(x > centreX + 3*HORIZONTAL_BOUND/4){

                step++;
                adjust();
            }
            else {

                path.lineTo(x, y);

                x+=HORIZONTAL_BOUND/2;
            }
        }
    }

    private void drawM(){

        if(step == 1){

            if(y < centreY - VERTICAL_BOUND){

                y = centreY - VERTICAL_BOUND;
                x = centreX - HORIZONTAL_BOUND + HORIZONTAL_BOUND/8;
                path.lineTo(x, y);
                step++;
            }
            else {

                path.lineTo(x, y);

                y-=VERTICAL_BOUND/2;
            }
        }
        else if(step == 2){

            if(x > centreX && y > centreY){

                x = centreX;
                y = centreY;
                step++;
            }
            else {

                path.lineTo(x, y);

                x+=HORIZONTAL_BOUND/2 - HORIZONTAL_BOUND/16;
                y+=VERTICAL_BOUND/2;
            }
        }
        else if(step == 3){

            if(x > centreX + HORIZONTAL_BOUND - HORIZONTAL_BOUND/8 && y < centreY - VERTICAL_BOUND){

                x = centreX + HORIZONTAL_BOUND;
                y = centreY - VERTICAL_BOUND;
                path.lineTo(x, y);
                step++;
            }
            else {

                path.lineTo(x, y);

                x+=HORIZONTAL_BOUND/2 - HORIZONTAL_BOUND/16;
                y-=VERTICAL_BOUND/2;
            }
        }
        else if(step == 4){

            if(y > centreY + VERTICAL_BOUND){

                y = centreY + VERTICAL_BOUND;
                step++;
                adjust();
            }
            else {

                path.lineTo(x, y);

                y+=VERTICAL_BOUND/2;
            }
        }
    }

    private void drawN(){

        if(step == 1){

            if(y < centreY - VERTICAL_BOUND){

                y = centreY - VERTICAL_BOUND;
                x = centreX - HORIZONTAL_BOUND + HORIZONTAL_BOUND/8;
                path.lineTo(x, y);
                step++;
            }
            else {

                path.lineTo(x, y);

                y-=VERTICAL_BOUND/2;
            }
        }
        else if(step == 2){

            if(x > centreX + HORIZONTAL_BOUND - HORIZONTAL_BOUND/8 && y > centreY + VERTICAL_BOUND){

                x = centreX + HORIZONTAL_BOUND;
                y = centreY + VERTICAL_BOUND;
                path.lineTo(x, y);
                step++;
            }
            else {

                path.lineTo(x, y);

                x+=HORIZONTAL_BOUND/2 - HORIZONTAL_BOUND/16;
                y+=VERTICAL_BOUND/2;
            }
        }
        else if(step == 3){

            if(y < centreY - VERTICAL_BOUND){

                y = centreY - VERTICAL_BOUND;
                step++;
                adjust();
            }
            else {

                path.lineTo(x, y);

                y-=VERTICAL_BOUND/2;
            }
        }
    }

    private void drawO(){

        if(step == 1){

            if(sweepAngle < -360){

                step++;
                adjust();
            }
            else {

                RectF rect = new RectF();
                rect.set(centreX - HORIZONTAL_BOUND,
                        centreY - VERTICAL_BOUND,
                        centreX + HORIZONTAL_BOUND,
                        centreY + VERTICAL_BOUND);

                path.addArc (rect, 0, sweepAngle);

                sweepAngle-=45;
            }
        }
    }

    private void drawP(){

        if(step == 1){

            if(y < centreY - VERTICAL_BOUND){

                x = centreX;
                y = centreY - VERTICAL_BOUND;
                path.lineTo(x, y);
                step++;
            }
            else {

                path.lineTo(x, y);
                y-=VERTICAL_BOUND/2;
            }
        }
        else if(step == 2){

            if(sweepAngle > 180){

                sweepAngle = 0;
                x = centreX - HORIZONTAL_BOUND;
                y = centreY;
                path.lineTo(x, y);
                step++;
                adjust();
            }
            else {

                RectF rect = new RectF();
                rect.set(centreX - HORIZONTAL_BOUND,
                        centreY - VERTICAL_BOUND,
                        centreX + HORIZONTAL_BOUND,
                        centreY);

                path.addArc (rect, 270, sweepAngle);
                sweepAngle+=45;
            }
        }
    }

    private void drawQ(){

        if(step == 1){

            if(sweepAngle < -360){

                x = centreX + HORIZONTAL_BOUND/2;
                y = centreY + VERTICAL_BOUND/2;
                path.moveTo(x, y);
                step++;
            }
            else {

                RectF rect = new RectF();
                rect.set(centreX - HORIZONTAL_BOUND,
                        centreY - VERTICAL_BOUND,
                        centreX + HORIZONTAL_BOUND,
                        centreY + VERTICAL_BOUND);

                path.addArc (rect, 0, sweepAngle);

                sweepAngle-=45;
            }
        }
        else if(step == 2){

            if(x > centreX + HORIZONTAL_BOUND && y > centreY + VERTICAL_BOUND){

                step++;
                adjust();
            }
            else {

                path.lineTo(x, y);

                x+=HORIZONTAL_BOUND/2;
                y+=VERTICAL_BOUND/2;
            }
        }
    }

    private void drawR(){

        if(step == 1){

            if(y < centreY - VERTICAL_BOUND){

                x = centreX;
                y = centreY - VERTICAL_BOUND;
                path.lineTo(x, y);
                step++;
            }
            else {

                path.lineTo(x, y);
                y-=VERTICAL_BOUND/2;
            }
        }
        else if(step == 2){

            if(sweepAngle > 180){

                sweepAngle = 0;
                x = centreX - HORIZONTAL_BOUND;
                y = centreY;
                path.lineTo(x, y);
                x = centreX;
                y = centreY;
                path.moveTo(x, y);
                step++;
            }
            else {

                RectF rect = new RectF();
                rect.set(centreX - HORIZONTAL_BOUND,
                        centreY - VERTICAL_BOUND,
                        centreX + HORIZONTAL_BOUND,
                        centreY);

                path.addArc (rect, 270, sweepAngle);
                sweepAngle+=45;
            }
        }
        else if(step == 3){

            if(x > centreX + HORIZONTAL_BOUND && y > centreY + VERTICAL_BOUND){

                step++;
                adjust();
            }
            else {

                path.lineTo(x, y);

                x+=HORIZONTAL_BOUND/2;
                y+=VERTICAL_BOUND/2;
            }
        }
    }

    private void drawS(){

        if(step == 1){

            if(sweepAngle < -270){

                sweepAngle = 0;
                step++;
            }
            else {

                RectF rect = new RectF();
                rect.set(centreX - HORIZONTAL_BOUND,
                        centreY - VERTICAL_BOUND,
                        centreX + HORIZONTAL_BOUND,
                        centreY - VERTICAL_BOUND/1024);

                path.addArc (rect, 0, sweepAngle);

                sweepAngle-=45;
            }
        }
        else if(step == 2){

            if(sweepAngle > 270){

                step++;
                adjust();
            }
            else {

                RectF rect = new RectF();
                rect.set(centreX - HORIZONTAL_BOUND,
                        centreY + VERTICAL_BOUND/1024,
                        centreX + HORIZONTAL_BOUND,
                        centreY + VERTICAL_BOUND);

                path.addArc (rect, -90, sweepAngle);

                sweepAngle+=45;
            }
        }
    }

    private void drawT(){

        if(step == 1){

            if(x > centreX + HORIZONTAL_BOUND){

                x = centreX;
                path.moveTo(x, y);
                step++;
            }
            else {

                path.lineTo(x, y);

                x+=HORIZONTAL_BOUND/2;
            }
        }
        else if(step == 2){

            if(y > centreY + VERTICAL_BOUND){

                step++;
                adjust();
            }
            else {

                path.lineTo(x, y);

                y+=VERTICAL_BOUND/2;
            }
        }
    }

    private void drawU(){

        if(step == 1){

            if(y > centreY + VERTICAL_BOUND/2){

                y = centreY + VERTICAL_BOUND/2;
                step++;
            }
            else {

                path.lineTo(x, y);

                y+=VERTICAL_BOUND/2;
            }
        }
        else if(step == 2){

            if(sweepAngle < -180){

                x = centreX + 3*HORIZONTAL_BOUND/4;
                sweepAngle = 0;
                step++;
            }
            else {

                RectF rect = new RectF();
                rect.set(centreX - 3*HORIZONTAL_BOUND/4,
                        centreY - VERTICAL_BOUND/8, //for rounded figure
                        centreX + 3*HORIZONTAL_BOUND/4,
                        centreY + VERTICAL_BOUND);

                path.addArc (rect, -180, sweepAngle);

                sweepAngle-=45;
            }
        }
        else if(step == 3){

            if(y < centreY - VERTICAL_BOUND){

                step++;
                adjust();
            }
            else {

                path.lineTo(x, y);

                y-=VERTICAL_BOUND/2;
            }
        }
    }

    private void drawV(){

        if(step == 1){

            if(x > centreX - HORIZONTAL_BOUND/8 && y > centreY + VERTICAL_BOUND){

                x = centreX + HORIZONTAL_BOUND/8;
                y = centreY + VERTICAL_BOUND;
                path.lineTo(x, y);
                step++;
            }
            else {

                path.lineTo(x, y);

                x+=HORIZONTAL_BOUND/4 - HORIZONTAL_BOUND/32;
                y+=VERTICAL_BOUND/2;
            }
        }
        else if(step == 2){

            if(x > centreX + HORIZONTAL_BOUND && y < centreY + VERTICAL_BOUND/2){

                step++;
                adjust();
            }
            else {

                path.lineTo(x, y);

                x+=HORIZONTAL_BOUND/4 - HORIZONTAL_BOUND/32;
                y-=VERTICAL_BOUND/2;
            }
        }
    }

    private void drawW(){

        if(step == 1){

            if(x > centreX - HORIZONTAL_BOUND/2 - HORIZONTAL_BOUND/16 && y > centreY + VERTICAL_BOUND){

                x = centreX - HORIZONTAL_BOUND/2 + HORIZONTAL_BOUND/16;
                y = centreY + VERTICAL_BOUND;
                path.lineTo(x, y);
                step++;
            }
            else {

                path.lineTo(x, y);

                x+=HORIZONTAL_BOUND/8 - HORIZONTAL_BOUND/128;
                y+=VERTICAL_BOUND/2;
            }
        }
        else if(step == 2){

            path.lineTo(centreX - HORIZONTAL_BOUND/16, centreY - VERTICAL_BOUND);
            path.lineTo(centreX + HORIZONTAL_BOUND/16, centreY - VERTICAL_BOUND);
            step++;
        }
        else if(step == 3){

            path.lineTo(centreX + HORIZONTAL_BOUND/2 - HORIZONTAL_BOUND/16, centreY + VERTICAL_BOUND);
            x = centreX + HORIZONTAL_BOUND/2 + HORIZONTAL_BOUND/16;
            y = centreY + VERTICAL_BOUND;
            path.lineTo(x, y);
            step++;
        }
        else if(step == 4){

            if(x > centreX + HORIZONTAL_BOUND && y < centreY - VERTICAL_BOUND){

                step++;
                adjust();
            }
            else {

                path.lineTo(x, y);

                x+=HORIZONTAL_BOUND/8 - HORIZONTAL_BOUND/128;
                y-=VERTICAL_BOUND/2;
            }
        }
    }

    private void drawX(){

        if(step == 1){

            if(x > centreX - HORIZONTAL_BOUND && y > centreY + VERTICAL_BOUND){

                x = centreX + HORIZONTAL_BOUND;
                y = centreY - VERTICAL_BOUND;
                path.moveTo(x, y);
                step++;
            }
            else {

                path.lineTo(x, y);

                x+=HORIZONTAL_BOUND/2;
                y+=VERTICAL_BOUND/2;
            }
        }
        else if(step == 2){

            if(x < centreX - HORIZONTAL_BOUND && y > centreY + VERTICAL_BOUND){

                step++;
                adjust();
            }
            else {

                path.lineTo(x, y);

                x-=HORIZONTAL_BOUND/2;
                y+=VERTICAL_BOUND/2;
            }
        }
    }

    private void drawY(){

        if(step == 1){

            if(x > centreX && y > centreY){

                x = centreX;
                y = centreY;
                step++;
            }
            else {

                path.lineTo(x, y);

                x+=HORIZONTAL_BOUND/2;
                y+=VERTICAL_BOUND/2;
            }
        }
        else if(step == 2){

            if(x > centreX + HORIZONTAL_BOUND && y < centreY - VERTICAL_BOUND){

                x = centreX;
                y = centreY;
                path.moveTo(x, y);
                step++;
            }
            else {

                path.lineTo(x, y);

                x+=HORIZONTAL_BOUND/2;
                y-=VERTICAL_BOUND/2;
            }
        }
        else if(step == 3){

            if(y > centreY + VERTICAL_BOUND){

                step++;
                adjust();
            }
            else {

                path.lineTo(x, y);

                y+=VERTICAL_BOUND/2;
            }
        }
    }

    private void drawZ(){

        if(step == 1){

            if(x > centreX + HORIZONTAL_BOUND){

                x = centreX + HORIZONTAL_BOUND;
                y = centreY - VERTICAL_BOUND + VERTICAL_BOUND/8;
                path.lineTo(x, y);
                step++;
            }
            else {

                path.lineTo(x, y);

                x+=HORIZONTAL_BOUND/2;
            }
        }
        else if(step == 2){

            if(x < centreX - HORIZONTAL_BOUND && y > centreY + VERTICAL_BOUND - VERTICAL_BOUND/8){

                x = centreX - HORIZONTAL_BOUND;
                y = centreY + VERTICAL_BOUND;
                path.lineTo(x, y);
                step++;
            }
            else {

                path.lineTo(x, y);

                x-=HORIZONTAL_BOUND/2;
                y+=VERTICAL_BOUND/2 - VERTICAL_BOUND/16;
            }
        }
        else if(step == 3) {

            if(x > centreX + HORIZONTAL_BOUND){

                step++;
                adjust();
            }
            else {

                path.lineTo(x, y);

                x+=HORIZONTAL_BOUND/2;
            }
        }
    }

    private void startDrawing() {

        switch (currentCharacter){

            case 'A': startDrawingA();break;
            case 'B': startDrawingB();break;
            case 'C': startDrawingC();break;
            case 'D': startDrawingD();break;
            case 'E': startDrawingE();break;
            case 'F': startDrawingF();break;
            case 'G': startDrawingG();break;
            case 'H': startDrawingH();break;
            case 'I': startDrawingI();break;
            case 'J': startDrawingJ();break;
            case 'K': startDrawingK();break;
            case 'L': startDrawingL();break;
            case 'M': startDrawingM();break;
            case 'N': startDrawingN();break;
            case 'O': startDrawingO();break;
            case 'P': startDrawingP();break;
            case 'Q': startDrawingQ();break;
            case 'R': startDrawingR();break;
            case 'S': startDrawingS();break;
            case 'T': startDrawingT();break;
            case 'U': startDrawingU();break;
            case 'V': startDrawingV();break;
            case 'W': startDrawingW();break;
            case 'X': startDrawingX();break;
            case 'Y': startDrawingY();break;
            case 'Z': startDrawingZ();break;
            case ' ': startDrawingSpace();break;
        }
    }

    private void continueDrawing(){

        switch (currentCharacter){

            case 'A': drawA();break;
            case 'B': drawB();break;
            case 'C': drawC();break;
            case 'D': drawD();break;
            case 'E': drawE();break;
            case 'F': drawF();break;
            case 'G': drawG();break;
            case 'H': drawH();break;
            case 'I': drawI();break;
            case 'J': drawJ();break;
            case 'K': drawK();break;
            case 'L': drawL();break;
            case 'M': drawM();break;
            case 'N': drawN();break;
            case 'O': drawO();break;
            case 'P': drawP();break;
            case 'Q': drawQ();break;
            case 'R': drawR();break;
            case 'S': drawS();break;//fix
            case 'T': drawT();break;
            case 'U': drawU();break;
            case 'V': drawV();break;
            case 'W': drawW();break;
            case 'X': drawX();break;
            case 'Y': drawY();break;
            case 'Z': drawZ();break;
        }
    }

    private void adjust(){

        //shifts the centreX to the end of the canvas after drawing the current letter
        if(currentCharacter == 'I')
            centreX += 0;
        else if(currentCharacter == 'C' || currentCharacter == 'G')
            centreX += HORIZONTAL_BOUND*Math.cos(-315*Math.PI/180);
        else if(currentCharacter == ' ')
            centreX += HORIZONTAL_BOUND / 2;
        else if(currentCharacter == 'J' || currentCharacter == 'U' || currentCharacter == 'L')
            centreX += 3 * HORIZONTAL_BOUND / 4;
        else
            centreX += HORIZONTAL_BOUND;

        //checks for next letter if exists
        if(currentPosition + 1 < text.length()) {

            currentPosition++;
            currentCharacter = text.charAt(currentPosition);
            setCentreX(currentCharacter);
            startDrawing();
        }
        else {

            Log.e("boom", centreX+"");

            //stop drawing
            if(listener != null)
                listener.WritingFinished();
        }
    }

    private void setCentreX(char currentCharacter){

        //sets the centreX as the centre of the canvas for the next letter
        if(currentCharacter == 'I')
            centreX += GAP;
        else if(currentCharacter == ' ')
            centreX += HORIZONTAL_BOUND / 2;
        else if(currentCharacter == 'J' || currentCharacter == 'U' || currentCharacter == 'L')
            centreX += GAP + 3 * HORIZONTAL_BOUND / 4;
        else
            centreX += GAP + HORIZONTAL_BOUND;
    }

    public interface Listener {

        //callback method triggered after drawing is finished
        void WritingFinished();
    }
}

