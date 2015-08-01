package Maps;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.location.*;

public class MapMe extends MIDlet implements CommandListener, LocationListener
{
    private boolean midletPaused = false;
    private Form mainForm;
    private StringItem strLat;
    private StringItem strLon;
    private StringItem strAlt;
    private Command cmdExit;
    private Command cmdLocateMe;
    private Command cmdMapMe;
    private Command cmdSaveMe;
    private Command cmdAboutMe;    
    private Command start;
    private Command stop;    
    private String URL = "http://localhost/Mobile/LocationSaver.php";
    private int count = 0;    
    private LocationProvider locationProvider = null;

    public Form getmainForm()
    {
        if(this.mainForm == null)
        {
            this.mainForm = new Form("Map Me !!",new Item[] { getstrLat(), getStrLon(), getstrAlt() });
            this.mainForm.setCommandListener(this);
            this.mainForm.addCommand(this.getcmdLocateMe());
            this.mainForm.addCommand(this.getcmdMapMe());
            this.mainForm.addCommand(this.getcmdSaveMe());
            this.mainForm.addCommand(this.getcmdAboutMe());            
            this.mainForm.addCommand(this.getcmdExit());            
            this.mainForm.addCommand(this.getStart());
            try 
            {
               Criteria cr = new Criteria();
               cr.setHorizontalAccuracy(Criteria.NO_REQUIREMENT);
               cr.setVerticalAccuracy(Criteria.NO_REQUIREMENT);
               cr.setCostAllowed(true);
               cr.setPreferredPowerConsumption(Criteria.POWER_USAGE_HIGH);                 
               this.locationProvider = LocationProvider.getInstance(cr);
            } 
            catch (Exception e) 
            {
               exitMIDlet();
            }
        }
        
        return this.mainForm;
    }  
    
    public void locationUpdated(LocationProvider provider, Location location) 
    {
        if (location != null && location.isValid()) 
        {
            QualifiedCoordinates qc = location.getQualifiedCoordinates();             
            boolean isSaved = Helper.saveLocation(URL, qc.getLatitude(), qc.getLongitude());
            String text = isSaved == true ? "Location Saved " + ++count : "Failed";
            strLat.setText(text);
        }
    }

    public void providerStateChanged(LocationProvider lp, int i) 
    {
        
    }
    
    public StringItem getstrAlt()
    {
        if(this.strAlt == null)
        {
            this.strAlt = new StringItem("","");
        }
        return this.strAlt;
    }

    public StringItem getstrLat()
    {
        if(this.strLat == null)
        {
            this.strLat = new StringItem("","");
        }
        return this.strLat;
    }
    
    public StringItem getStrLon()
    {
        if(this.strLon == null)
        {
            this.strLon = new StringItem("","");
        }
        return this.strLon;
    }

    public Command getcmdExit()
    {
        if (this.cmdExit == null) {
            this.cmdExit = new Command("Exit", Command.EXIT, 0);
        }
        return this.cmdExit;
    }

    public Command getcmdLocateMe()
    {
        if (this.cmdLocateMe == null) {
            this.cmdLocateMe = new Command("Pin Me", Command.SCREEN, 0);
        }
        return this.cmdLocateMe;
    }

    public Command getcmdMapMe()
    {
        if (this.cmdMapMe == null) {
            this.cmdMapMe = new Command("Map", Command.SCREEN, 0);
        }
        return this.cmdMapMe;
    }

    public Command getcmdSaveMe()
    {
        if (this.cmdSaveMe == null) {
            this.cmdSaveMe = new Command("Save", Command.SCREEN, 0);
        }
        return this.cmdSaveMe;
    }

    public Command getcmdAboutMe()
    {
        if (this.cmdAboutMe == null) {
            this.cmdAboutMe = new Command("About Me", Command.SCREEN, 0);
        }
        return this.cmdAboutMe;
    }
    
    public Command getStart()
    {
        if (this.start == null)
        {
            this.start = new Command("Start", Command.SCREEN, 1);
        }
        
        return this.start;
    }
    
    public Command getStop()
    {
        if (this.stop == null)
        {
            this.stop = new Command("Stop", Command.SCREEN, 1);
        }
        
        return this.stop;
    }

    public void startApp() 
    {
        if (midletPaused) {
            resumeMIDlet ();
        } else {
            initialize ();
            startMIDlet ();
        }
        midletPaused = false;
    }

    public void commandAction(Command command, Displayable displayable)
    {
        if (displayable == this.mainForm)
        {
            if (command == this.cmdExit) 
            {
       
                exitMIDlet();
       
            }
            else if(command == this.getcmdAboutMe())
            {
                String message = "summer internship(System time: " + Helper.getNowInDate();
                Alert alert = new Alert("About", message, null, AlertType.INFO);
                alert.setTimeout(Alert.FOREVER);
                this.getDisplay().setCurrent(alert);
            }
            else if(command == this.getcmdLocateMe())
            {
                try
                {
                    double []arr = Helper.getLocation(this.locationProvider);
                    String text = "Lat: " + Double.toString(arr[0])
                            + "  Lon:" + Double.toString(arr[1])
                            + "  Alt:" + Double.toString(arr[2]);
                    this.strLat.setText(text);
                    
                }
                catch (Exception e)
                {
                    this.strLat.setText(e.getMessage());
                }                
            }
            else if(command == this.getcmdMapMe())
            {
                try
                {
                    double []arr = Helper.getLocation(this.locationProvider);
                    this.getDisplay().setCurrent(new MapViewer(this,this.mainForm,arr[0],arr[1]));                    
                }
                catch(Exception ex)
                {
                    this.strLat.setText(ex.getMessage());
                }
            }
            else if(command == this.getcmdSaveMe())
            {
                try
                {
                    boolean result = true;
                    if (result)
                    {
                        this.strLat.setText("location saved in database!!");
                    }
                    else
                    {
                        this.strLat.setText("Failed to save location!!");
                    }   
                }
                catch (Exception e)
                {
                     this.strLat.setText(e.getMessage());
                }             
            }                      
            else if (command == this.getStart())
            {
                this.mainForm.removeCommand(this.getStart());
                new Thread()
                        {
                            public void run()
                            {
                                locationProvider.setLocationListener(MapMe.this, 10, -1, -1);
                            }
                        }.start();
                
                this.count = 0;
	        this.mainForm.addCommand(this.getStop());                
            }
            else if (command == this.getStop())
            {
                this.mainForm.removeCommand(this.getStop());
                new Thread()
                        {
                            public void run()
                            {
                                locationProvider.setLocationListener(null, -1, -1, -1);                                
                            }
                        }.start();
                
                this.mainForm.addCommand(this.getStart());
                this.strLat.setText("");
            }                
        }
    }

    public void pauseApp() 
    {
        midletPaused = true;
    }

    public void resumeMIDlet()
    {
      
    }    

    public void destroyApp(boolean unconditional)
    {
        
    }

    public Display getDisplay ()
    {
        return Display.getDisplay(this);
    }

    public void switchDisplayable(Alert alert, Displayable nextDisplayable)
    {
        
        Display display = getDisplay();
        if (alert == null) {
            display.setCurrent(nextDisplayable);
        } else {
            display.setCurrent(alert, nextDisplayable);
        }
       
    }

    public void startMIDlet()
    {
        
        switchDisplayable(null, getmainForm());
        
    }

    public void exitMIDlet()
    {
        switchDisplayable (null, null);
        destroyApp(true);
        notifyDestroyed();
    }
    
    private void initialize()
    {
       
    }
}
