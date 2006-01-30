/*
 * Created on Jan 20, 2006
 */
package uk.org.ponder.dateutil;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import uk.org.ponder.errorutil.PropertyException;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * A "validation bean" which will accept and collect separate String values for
 * day, month and year into a java.util.Date format.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */

public class DMYTransit {

  public Calendar calendar = new GregorianCalendar();
  private String month;
  private String year;
  private String day;

  public void setCalendar(Calendar calendar) {

  }

  public void setMonth(String month) {
    this.month = month;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public void setDay(String day) {
    this.day = day;
  }

  public Date getDate() {
    try {
      int yearint = Integer.parseInt(year);
      int monthint = Integer.parseInt(month);
      int dayint = Integer.parseInt(day);
      calendar.set(yearint, monthint, dayint);
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e, PropertyException.class,
          "Invalid date format");
    }
    return calendar.getTime();
  }

}