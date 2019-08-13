package utils.paint;

import java.awt.Point;

class MousePathPoint extends Point{

    /**
     *
     */
    private static final long serialVersionUID = -3005850694404077385L;

    private long finishTime;

    public MousePathPoint(int x, int y, int lastingTime){
        super(x,y);
        finishTime= System.currentTimeMillis() + lastingTime;
    }

    public boolean isUp(){
        return System.currentTimeMillis() > finishTime;
    }
}