/*
 * Developer email: hiankit.work@gmail.com
 * GitHub: https://github.com/bxute
 */

/*
 * Developer email: hiankit.work@gmail.com
 * GitHub: https://github.com/bxute
 */

package com.oneramp.hjcalendar;

public class CalendarConfig {
  private int cellSize;
  private int dayLabelFormat;
  private int dayNameTextSize;

  public CalendarConfig(int cellSize,
                        int dayLabelFormat,
                        int dayNameTextSize) {
    this.cellSize = cellSize;
    this.dayLabelFormat = dayLabelFormat;
    this.dayNameTextSize = dayNameTextSize;
  }

  public int getCellSize() {
    return cellSize;
  }

  public void setCellSize(int cellSize) {
    this.cellSize = cellSize;
  }

  public int getDayNameTextSize() {
    return dayNameTextSize;
  }

  public void setDayNameTextSize(int dayNameTextSize) {
    this.dayNameTextSize = dayNameTextSize;
  }

  public int getDayLabelFormat() {
    return dayLabelFormat;
  }

  public void setDayLabelFormat(int dayLabelFormat) {
    this.dayLabelFormat = dayLabelFormat;
  }
}
