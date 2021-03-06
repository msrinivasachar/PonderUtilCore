/*
 * Created on Apr 1, 2005
 */
package uk.org.ponder.dateutil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * A thread-local SimpleDateFormat wrapper. If the default constructor is
 * used, supplies a W3C standard date format with millisecond resolution.
 * This class is somewhat useful since construction of a SimpleDateFormat runs
 * at around 13µs (2Ghz A64), with ThreadLocal gets around 300 times faster at
 * 40ns or so. NB, GregorianCalendar runs at about 2µs.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class LocalSDF {
  /** this represents W3C (ISO-8601) standard dates as defined in 
   * http://www.w3.org/TR/NOTE-datetime */
  public static final String W3C_DATE = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
  /** An 8601 format with a variable timezone **/
  public static final String W3C_DATE_TZ = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
  /** An 8601 format with a truncated (forced) timezone **/
  public static final String W3C_DATE_NOTZ = "yyyy-MM-dd'T'HH:mm:ss.SSS";
  /** A format to easy breaking dates into field representations **/
  public static final String BREAKER_DATE = "ddMMyyyyHHmmss";
  
  public static LocalSDF w3cformat = new LocalSDF();
  public static LocalSDF breakformat = new LocalSDF(BREAKER_DATE);
  
  private String formatstring;
  private ThreadLocal formatter = new ThreadLocal() {
    public Object initialValue() {
      if (formatstring == null) {
        SimpleDateFormat format = new SimpleDateFormat(W3C_DATE);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        format.setLenient(false);
        return format;
      }
      return new SimpleDateFormat(formatstring);
    }
  };
  public LocalSDF() {
  }
  public LocalSDF(String formatstring) {
    this.formatstring = formatstring;
  }
  public SimpleDateFormat get() {
    return (SimpleDateFormat) formatter.get();
  }
  public String format(Date toformat) {
    return get().format(toformat);
  }
  public Date parse(String datestring) {
    Date parsed =  DateUtil.parse(get(), datestring);
    return parsed;
  }
}
