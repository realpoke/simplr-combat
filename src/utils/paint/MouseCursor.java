package utils.paint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;

import org.osbot.rs07.script.MethodProvider;

public class MouseCursor {

    private int r, g, b = 255;
    private int mX, mY;
    private int size;
    private int angle;
    private BasicStroke cursorStroke;
    private AffineTransform oldTransform;
    private MethodProvider api;

    public MouseCursor(int size, int thickness, MethodProvider api) {
        this.size = size;
        this.cursorStroke = new BasicStroke(thickness);
        this.api = api;
    }

    public void nextRGB(){
        if(r == 255 && g < 255 & b == 0){
            g++;
        }
        if ( g == 255 && r > 0 && b == 0 )
        {
            r--;
        }
        if ( g == 255 && b < 255 && r == 0 )
        {
            b++;
        }
        if ( b == 255 && g > 0 && r == 0 )
        {
            g--;
        }
        if ( b == 255 && r < 255 && g == 0 )
        {
            r++;
        }
        if ( r == 255 && b > 0 && g == 0 )
        {
            b--;
        }

    }

    public Color nextColor(){
        nextRGB();
        return new Color(r,g,b);
    }

    public void paint(Graphics2D g) {
        g.setColor(nextColor());
        oldTransform = g.getTransform();
        mX = api.getMouse().getPosition().x;
        mY = api.getMouse().getPosition().y;

        if (mX != -1) {
            g.setStroke(cursorStroke);

            g.rotate(Math.toRadians(angle += 6), mX, mY);

            if (angle >= 360)
                angle = 0;

            g.draw(new Arc2D.Double(mX - (size / 2), mY - (size / 2), size, size, 330, 60, Arc2D.OPEN));
            g.draw(new Arc2D.Double(mX - (size / 2), mY - (size / 2), size, size, 151, 60, Arc2D.OPEN));

            g.setTransform(oldTransform);
        }
    }

}