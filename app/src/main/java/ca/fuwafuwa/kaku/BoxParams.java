package ca.fuwafuwa.kaku;

/**
 * Created by 0x1bad1d3a on 4/16/2016.
 */
public final class BoxParams {

    final public int x;
    final public int y;
    final public int width;
    final public int height;

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
