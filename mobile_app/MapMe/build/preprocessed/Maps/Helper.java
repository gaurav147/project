package Maps;

import java.util.Calendar;
import javax.microedition.io.HttpConnection;
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.location.*;

public final class Helper
{
    public Helper()
    {
    }

    public static String EncodeURL(String URL)
    {
        URL = replace(URL, 'à', "%E0");
        URL = replace(URL, 'è', "%E8");
        URL = replace(URL, 'é', "%E9");
        URL = replace(URL, 'ì', "%EC");
        URL = replace(URL, 'ò', "%F2");
        URL = replace(URL, 'ù', "%F9");
        URL = replace(URL, '$', "%24");
        URL = replace(URL, '#', "%23");
        URL = replace(URL, '£', "%A3");
        URL = replace(URL, '@', "%40");
        URL = replace(URL, '\'', "%27");
        URL = replace(URL, ' ', "%20");

        return URL;
    }

    public static String replace(String source, char oldChar, String dest)
    {
        String ret = "";
        for (int i = 0; i < source.length(); i++)
        {
                if (source.charAt(i) != oldChar)
                        ret += source.charAt(i);
                else
                        ret += dest;
        }
        return ret;
    }

    public static String getNow()
    {
            StringBuffer buffer = new StringBuffer();
            String[] monthName = {"January", "February",
                "March", "April", "May", "June", "July",
                "August", "September", "October", "November",
                "December"};
            Calendar cal = Calendar.getInstance();
            buffer.append(monthName[cal.get(Calendar.MONTH)]);
            buffer.append(" ");
            buffer.append(cal.get(Calendar.DATE));
            buffer.append(", ");
            buffer.append(cal.get(Calendar.YEAR));
            buffer.append(", ");
            int hour = cal.get(Calendar.HOUR);
            hour = hour == 0 ? 12 : hour;
            buffer.append(hour);
            buffer.append(":");
            buffer.append(cal.get(Calendar.MINUTE));
            buffer.append(" ");
            if(cal.get(Calendar.AM_PM) == 0)
            {
                    buffer.append("am");
            }
            else
            {
                    buffer.append("pm");
            }

            return buffer.toString();
    }
    
    public static String getNowInDate()
    {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int hour = cal.get(Calendar.AM_PM) == 0 ? cal.get(Calendar.HOUR) : cal.get(Calendar.HOUR) + 12;
        return cal.get(Calendar.YEAR) + "-" + month + "-" +
                        cal.get(Calendar.DATE) + " " + hour + ":" +
                        cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
    }
    
    public static double[] getLocation(LocationProvider lp) throws Exception
    {
        double []arr = new double[3];               
        if(lp != null)
        {
            switch(lp.getState())
            {
                case LocationProvider.AVAILABLE:
                    Location l = lp.getLocation(-1);
                    QualifiedCoordinates  c = l.getQualifiedCoordinates();
                    if(c != null )
                    {
                      double lat = c.getLatitude();
                      double lon = c.getLongitude();
                      double alt = c.getAltitude();
                      String latStr = Double.toString(lat).substring(0,7);
                      String lonStr = Double.toString(lon).substring(0,7);
                      lat = Double.parseDouble(latStr);
                      lon = Double.parseDouble(lonStr);
                      arr[0] = lat;
                      arr[1] = lon;
                      arr[2] = alt;
                    }
                    else
                    {
                        throw new Exception("Co ordinate is null!!");
                    }
                    break;

                case LocationProvider.OUT_OF_SERVICE:
                    throw new Exception("LocationProvider OUT_OF_SERVICE.");

                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    throw new Exception("LocationProvider TEMPORARILY_UNAVAILABLE.");

                default:
                    break;
            }
        }
        else
        {
            throw new Exception("LocationProvider object is null.");
        }
        
        return arr;
    }
    
    public static boolean saveLocation(String baseUrl, double lat, double lon)
    {
        InputStream is =null;
        StringBuffer sb = null;
        HttpConnection http = null;
        boolean result = false;
        try
        {
            String latStr = Double.toString(lat).substring(0,7);
            String lonStr = Double.toString(lon).substring(0,7);
            lat = Double.parseDouble(latStr);
            lon = Double.parseDouble(lonStr);
                       
            baseUrl +="&lat=" + Double.toString(lat) + "&lon=" + Double.toString(lon) + "&time=" + Helper.getNowInDate();
            baseUrl = Helper.EncodeURL(baseUrl);
            http = (HttpConnection) Connector.open(baseUrl);                
            http.setRequestMethod(HttpConnection.GET);                
            if (http.getResponseCode() == HttpConnection.HTTP_OK)
            {
                sb = new StringBuffer();
                int ch;
                is = http.openInputStream();
                while ((ch = is.read()) != -1)
                {
                    sb.append((char) ch);
                }

                result = true;                
            }
            else
            {
                result = false;                 
            }
            
        }
        catch (Exception ex)
        {
            result = false;
             // this.strLat.setText(e.getMessage());
        }
        finally
        {
            try 
            {
                if (is != null)
                {
                    is.close();
                }
                    
                if (http != null)
                {
                    http.close();
                }
                        
            }
            catch (Exception e) 
            {
            }
        }
        
        return result;
    }
    
    public static boolean saveLocation(LocationProvider lp, String baseUrl)
    {
        boolean result = false; 
        try
        {
            double arr[] = Helper.getLocation(lp);
            if(arr != null)
            {
                result = Helper.saveLocation(baseUrl, arr[0], arr[1]);            
            }
        }
        catch(Exception ex)
        {
            result = false;
        }
        
        return result;
    }
}
