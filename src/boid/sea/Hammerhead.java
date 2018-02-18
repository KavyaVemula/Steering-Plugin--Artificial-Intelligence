/*
 Copyright (c) Ron Coleman

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package boid.sea;

import buckland.ch3.common.D2.Vector2D;
import static buckland.ch3.common.misc.Cgdi.gdi;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

/**
 * This class implements a "hammerhead" type predator which renders the killzone
 * and the number of prey which enter the killzone.
 * 
 * @author Ron Coleman
 */
public class Hammerhead extends Shark {
    
    // See http://docs.oracle.com/javase/tutorial/2d/geometry/strokeandfill.html
    protected float[] aDash1 = {10.0f};
    protected BasicStroke pDashed
            = new BasicStroke(1.0f,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f, aDash1, 0.0f);
    
    /**
     * Constructor
     * @param pSpawnPos Initial spawn position in the world
     */
    public Hammerhead(Vector2D pSpawnPos) {
        super(pSpawnPos);
    }
    /**
     * This method gets invoked every cycle during the render phase by game world. 
     * @param bTrueFalse 
     */
    @Override
    public void Render(boolean bTrueFalse) {
        // Render the background objects for the predator
        super.Render(bTrueFalse);

        // Get the graphics context
        Graphics2D g = gdi.GetHdc();

        // Draw the capture count
        DrawCaptureCount(g);
        
        // Draw the killzone with peripheral vision
        DrawKillzone(g);
    }   
    
    protected void DrawKillzone(Graphics2D g) {
        // Draw the ring of the complete zone
        DrawRing(g);
        
        // Draw two arms of the vision outside of which is the blindspot
        double dTheta = Math.atan2(Heading().y, Heading().x);
        
        DrawArm(g, dTheta + PERIPHERAL_VISION / 2 * Math.PI / 180);
        DrawArm(g, dTheta - PERIPHERAL_VISION / 2 * Math.PI / 180);        
    }
    
    protected void DrawCaptureCount(Graphics2D g) {
        // Convert the capture number to a string
        String sCapture = String.format("%3d", iLastCaptureCount);
        
        // Set the color to red
        g.setColor(Color.RED);

        // Draw the capture number using the default font
        int dPosX = (int) Pos().x;
        int dPosY = (int) Pos().y;
        
        g.drawString(sCapture + " ", dPosX+10, dPosY+10);         
    }
    
    /**
     * Draws the killzone ring.
     * @param g Graphics context
     */
    protected void DrawRing(Graphics2D g) {
        // Draw the predator at the center of the killzone
        int dX = (int)(Pos().x - dKillzone);
        
        int dY = (int)(Pos().y - dKillzone);
        
        int dWidth = (int) (2 * dKillzone);
        
        int dHeight = dWidth;
        
        // Draw the killzone with the dashed stroke
        g.setColor(Color.RED); 
        
        Stroke pOldStroke = g.getStroke();
        
        g.setStroke(pDashed);

        g.drawOval(dX, dY, dWidth, dHeight);
        
        g.setStroke(pOldStroke);
                
    }
    
    /**
     * Draws a killzone arm.
     * @param g Graphics context
     * @param dAngle Angle to draw the arm.
     * @see Minnow class for algorithm
     */
    protected void DrawArm(Graphics2D g, double dAngle) {        
        int dXStart = (int) (this.Pos().x + 0.5);
        int dYStart = (int) (this.Pos().y + 0.5);
        
        int dXEnd = (int) (dKillzone * Math.cos(dAngle) + dXStart + 0.5);
        int dYEnd = (int) (dKillzone * Math.sin(dAngle) + dYStart + 0.5);
        
        g.setColor(Color.RED);
        
        g.drawLine(dXStart, dYStart, dXEnd, dYEnd); 
    }
}