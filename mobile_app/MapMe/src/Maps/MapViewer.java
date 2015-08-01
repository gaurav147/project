package Maps;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import com.jappit.midmaps.googlemaps.GoogleMaps;
import com.jappit.midmaps.googlemaps.GoogleMapsCoordinates;
import com.jappit.midmaps.googlemaps.GoogleMapsMarker;
import com.jappit.midmaps.googlemaps.GoogleStaticMap;
import com.jappit.midmaps.googlemaps.GoogleStaticMapHandler;

public class MapViewer extends Canvas implements GoogleStaticMapHandler,CommandListener
{
    private MIDlet midlet;
    private Displayable mainScreen;
    private Command backCommand,zoomInCommand,zoomOutCommand;;
    private double lat;
    private double lon;
    private GoogleMaps gMaps = null;
    private GoogleStaticMap map = null;    
    private String apiKey = "AIzaSyC7cbBwAkkyOxbMJUqzbFwzcZj0Qlyomzk";

    public MapViewer(MIDlet midlet, Displayable mainScreen, double lat, double lon)
    {
        this.lat = lat;
        this.lon = lon;
        this.mainScreen = mainScreen;
        this.midlet = midlet;
        this.addCommand(this.backCommand = new Command("Back", "Back to Search", Command.BACK, 0));
        this.addCommand(this.zoomInCommand = new Command("Zoom in", Command.OK, 1));
	this.addCommand(this.zoomOutCommand = new Command("Zoom out", Command.OK, 2));
        this.setCommandListener(this);        
        this.gMaps = new GoogleMaps(this.apiKey);
        map = gMaps.createMap(getWidth(), getHeight(), GoogleStaticMap.FORMAT_PNG);
        map.setHandler(this);
        map.setCenter(new GoogleMapsCoordinates(this.lat, this.lon));
        GoogleMapsMarker redMarker = new GoogleMapsMarker(new GoogleMapsCoordinates(this.lat, this.lon));
        redMarker.setColor(GoogleStaticMap.COLOR_RED);
        redMarker.setSize(GoogleMapsMarker.SIZE_MID);
        map.addMarker(redMarker);
        map.setZoom(15);
        map.update();
    }

    protected void paint(Graphics g)
    {
        map.draw(g, 0, 0, Graphics.TOP | Graphics.LEFT);
    }

    public void GoogleStaticMapUpdateError(GoogleStaticMap map, int errorCode, String errorMessage)
    {
        showError("map error: " + errorCode + ", " + errorMessage);
    }

    public void GoogleStaticMapUpdated(GoogleStaticMap map)
    {
        repaint();
    }
    
    private void showError(String message)
    {
        Alert error = new Alert("Error", message, null, AlertType.ERROR);
        Display.getDisplay(midlet).setCurrent(error, this);
    }

    protected void keyPressed(int key)
    {
        int gameAction = getGameAction(key);
        if(gameAction == Canvas.UP || gameAction == Canvas.RIGHT || gameAction == Canvas.DOWN || gameAction == Canvas.LEFT)
        {
            map.move(gameAction);
        }
    }

    public void commandAction(Command c, Displayable d)
    {
        if(c == this.backCommand)
        {
            Display.getDisplay(this.midlet).setCurrent(this.mainScreen);
        }
        else if(c == this.zoomInCommand)
        {
            map.zoomIn();
        }
        else if(c == this.zoomOutCommand)
        {
            map.zoomOut();
        }
    }
}
