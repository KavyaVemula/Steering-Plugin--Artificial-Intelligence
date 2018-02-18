package boid.sea;

import buckland.ch3.boid.Prey;
import buckland.ch3.common.D2.Vector2D;
import static buckland.ch3.common.misc.Cgdi.gdi;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * This class implements a minnow prey which renders a small steering antenna
 * proportional the speed at the head of the prey.
 * @author Ron.Coleman
 */
public class Minnow extends Prey {
    // Refactors the antenna length since using speed only it may be too long.
    public final double SPEED_REFACTOR = 0.20;
    
    /**
     * Constructor
     * @param pSpawnPos Spawn position in the world.
     */
    public Minnow(Vector2D pSpawnPos) {
        super(pSpawnPos);
    }
    
    /**
     * This method gets invoked every cycle during the render phase by game world.
     * It only serves to draw the antenna, although in future this may change. 
     * @param bTrueFalse Don't use this whatever it does. 
     */    
    @Override
    public void Render(boolean bTrueFalse) {
        // Render the background objects for the prey
        super.Render(bTrueFalse);

        // Draw the antenna in the foreground
        drawAntenna();
    }
    
    /**
     * Draws the antenna.
     */
    protected void drawAntenna() {
        // Compute the antenna length
        double dLength = SPEED_REFACTOR * this.m_vVelocity.Length();
        
        // Get the direction prey heading
        Vector2D pHeading = this.Heading();
        
        double dTheta = Math.atan2(pHeading.y, pHeading.x);
        
        // This is where the antenna starts which is known by prey position
        int dXStart = (int) (this.Pos().x + 0.5);
        int dYStart = (int) (this.Pos().y + 0.5);
        
        // The end location of the antenna is knowable using algebra and trig:
        // cos(theta) = deltaX / L = (xEnd - xStart) / L
        // sin(theta) = deltaY / L = (yEnd - yStart) / L
        // So, we can solve for xEnd and yEnd
        int dXEnd = (int) (dLength * Math.cos(dTheta) + dXStart + 0.5);
        int dYEnd = (int) (dLength * Math.sin(dTheta) + dYStart + 0.5);
        
        // Get the graphic context for drawing the antenna
        Graphics2D g = gdi.GetHdc();
        
        // Draw the antenna.
        g.setColor(Color.GREEN);
        
        g.drawLine(dXStart, dYStart, dXEnd, dYEnd);        
    }
}
