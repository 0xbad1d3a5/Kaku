package ca.fuwafuwa.kaku;

/**
 * Created by Xyresic on 4/16/2016.
 */
public final class BoxParams {

    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public BoxParams(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public String toString(){
        return String.format("X:%d Y:%d (%dx%d)", x, y, width, height);
    }
}
