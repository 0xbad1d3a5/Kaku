package ca.fuwafuwa.kaku.Ocr;

/**
 * Created by 0xbad1d3a5 on 4/16/2016.
 */
public final class BoxParams {

    public int x;
    public int y;
    public int width;
    public int height;

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
