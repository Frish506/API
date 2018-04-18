

import sun.applet.Main;



public class SpillRequest {
    public static int x, y, windowWidth, windowLength;
    public static String cssPath, destNodeID, originNodeID;

    public SpillRequest() {

    }

    public void run(int xcoord, int ycoord, int windowWidth, int windowLength, String cssPath, String destNodeID, String originNodeID) {
        this.x = xcoord;
        this.y = ycoord;
        this.windowWidth = windowWidth;
        this.windowLength = windowLength;
        this.cssPath = cssPath;
        this.destNodeID = destNodeID;
        this.originNodeID = originNodeID;
        String[] a = new String[0];
        Main.main(a);
    }
}
