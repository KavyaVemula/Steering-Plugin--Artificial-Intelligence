
package boid.sea;

import buckland.ch3.boid.Predator;
import buckland.ch3.boid.Prey;
import buckland.ch3.boid.Vehicle;
import buckland.ch3.common.D2.Vector2D;
import java.util.List;
import static buckland.ch3.ParamLoader.Prm;
import static buckland.ch3.common.D2.Vector2D.Vec2DNormalize;
import static buckland.ch3.common.D2.Vector2D.add;
import static buckland.ch3.common.D2.Vector2D.mul;
import static buckland.ch3.common.D2.Vector2D.sub;
import java.util.ArrayList;
import static buckland.ch3.common.misc.Cgdi.gdi;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

/**
 * This class provides an extension to Professor Coleman's code that 
 * implements a specialized kind of predator.
 * @author Kavya Reddy Vemula
 */
public class Shark extends Predator {
    protected float[] aDash1 = {10.0f};
    protected BasicStroke pDashed
            = new BasicStroke(1.0f,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f, aDash1, 0.0f);
    public final double PERIPHERAL_VISION = 36;
    
    public final double PERIPHERAL_VISION_RADIUS = Math.acos(PERIPHERAL_VISION);
    // Set this flag to true if debugging
    public final boolean DEBUGGING = false;
    
    protected double dKillzone = -1;
    
    // Last count of prey witin the killzone
    protected int iLastCaptureCount = 0;


    protected List<Vehicle> pPreyList;
    
    // Last calculated heading in degrees
    protected  double dTheta;
    
    // Speed
    protected  double dSpeed;
    
    protected List<Vehicle> PredatorList;
    
    protected int dKillScore =0;
    
    /**
     * Constructor
     * @param pSpawnPos Initial spawn position.
     */
    public Shark(Vector2D pSpawnPos) {
        super(pSpawnPos);
        
        this.dKillzone = Prm.ViewDistance;
        
        PredatorList = this.m_pWorld.GetPredators();
    }

    /**
     * This method gets invoked every cycle during the update phase by game world.
     * @param dTimeElapsed Elapsed time in seconds since last update
     */
    @Override
    public void Update(double dTimeElapsed) {
        // Run the superclass operations for the predator
        super.Update(dTimeElapsed);

        // Get the heading in degrees
        Vector2D pHeading = this.Heading();

        this.dTheta = Math.atan2(pHeading.y, pHeading.x) * 180 / Math.PI;
        
        this.dSpeed = this.Speed();      

        // This array holds our lunch
        List<Vehicle> pLunchBox = new ArrayList<>();
        
        // Get all the prey
        pPreyList = this.m_pWorld.GetPrey();
       
        // Find out which ones are in the "killzone"
        for (int i=0; i < pPreyList.size(); i++) {
            Prey pPrey = (Prey) pPreyList.get(i);
            double dDistance = this.Pos().Distance(pPrey.Pos());
            double dFacing = this.Heading().Dot(pPrey.Pos());
            Prey prey =  pPrey;
            
            // Checking if the Prey falls in the kill zone and facing the same
            // direction as Predator
            if (dDistance <= dKillzone && dFacing > 0) {
                // Adding the Prey to Lunch Box
                pLunchBox.add(pPrey);
                
                // Seek if prey is near
                if (dDistance <= 35) {
                    System.out.println("Performing Seek");
                    this.SetVelocity(Seek(pPrey.Pos()));
                    this.SetVelocity(Seek(prey.Pos()));
                } 
                
                // Pursuit if Prey is far
                else {
                    System.out.println("Performing Pursuit");
                    this.SetVelocity(Pursuit(pPrey));
                    this.SetVelocity(Pursuit(prey));
                }               
                pPreyList.remove(pPrey);
                dKillScore+=1;
                // Bringing back Max speed to 70 after prey is dead
                this.SetMaxSpeed(70); 
            }
        }   
        
        // Track when the dinner count peaks then starts to drop
        int iCaptureCount = pLunchBox.size();
        
        if (iCaptureCount != iLastCaptureCount) {
            //System.out.print("" + iCaptureCount);
        }
        
        if(iCaptureCount == 0 && iLastCaptureCount != 0)
            System.out.println("---");       

        iLastCaptureCount = iCaptureCount;
    }
    
        private Vector2D Seek(Vector2D TargetPos) {
        //System.out.println(this.Velocity());
        Vector2D DesiredVelocity = mul(Vec2DNormalize(sub(TargetPos, this.Pos())),70);
        return sub(DesiredVelocity, this.Velocity());
    }

    
     private Vector2D Pursuit(final Vehicle evader) {
        //if the evader is ahead and facing the agent then we can just seek
        //for the evader's current position.
        Vector2D ToEvader = sub(evader.Pos(), this.Pos());

        double RelativeHeading = this.Heading().Dot(evader.Heading());

        if ((ToEvader.Dot(this.Heading()) > 0)
                && (RelativeHeading < -0.95)) //acos(0.95)=18 degs
        {
            this.dSpeed += 10;
            this.SetMaxSpeed(dSpeed); // Burst speed to catch prey
            return Seek(evader.Pos());
        }
        double LookAheadTime = ToEvader.Length() / (this.MaxSpeed() + evader.Speed());
        return Seek(add(evader.Pos(), mul(evader.Velocity(), LookAheadTime)));
    }

    /**
     * This method gets invoked every cycle during the render phase by game world. 
     * @param bTrueFalse 
     */
    @Override
    public void Render(boolean bTrueFalse) {
        // Render the background objects for the predator
        super.Render(bTrueFalse);

        // Convert the heading to a string
        String sHeading = String.format("%4.1f", dTheta);
        
        // Get the graphics context
        Graphics2D g = gdi.GetHdc();

        // Set the color to black
        g.setColor(Color.BLACK);

        // Draw the string using the default font
        int dX = (int) (this.Pos().x - dKillzone/2);
        int dY = (int) (this.Pos().y - dKillzone/2);
        
        // Prey Number
        g.drawString("PREY: " + pPreyList.size(), 450,480);
        drawKillzone();
        DrawRing(g);
        DrawCaptureCount(g);
        
    }
    
        protected void drawKillzone(){
        Vector2D pHeading = this.Heading();
        
        double dTheta4 = Math.atan2(pHeading.y, pHeading.x);
        double dTheta1 = dTheta4 - PERIPHERAL_VISION * Math.PI / 180;
        double dTheta2 = dTheta4 + PERIPHERAL_VISION * Math.PI / 180;
        
        drawLeg(dTheta1);
        drawLeg(dTheta2);
    }
    
    protected void drawLeg(double dTheta){
        int dX0 = (int) this.Pos().x;
        int dY0 = (int) this.Pos().y;
        
        int dX1 = (int) (Math.cos(dTheta) * dKillzone + dX0);
        int dY1 = (int) (Math.sin(dTheta) * dKillzone + dY0);
        
        Graphics2D g = gdi.GetHdc();
        
        g.setColor(Color.red);
        
        g.drawLine(dX0, dY0, dX1, dY1);
    }
    
    /**
     * Draws the kill zone ring.
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
    
    protected void DrawCaptureCount(Graphics2D g) {
        // Set the color to red
        g.setColor(Color.RED);

        // Draw the capture number using the default font
        int dPosX = (int) Pos().x;
        int dPosY = (int) Pos().y;
        
        g.drawString(dKillScore + " ", dPosX+10, dPosY+10);         
    }
}