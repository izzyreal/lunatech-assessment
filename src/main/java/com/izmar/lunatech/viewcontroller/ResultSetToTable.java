/*
 * Copied from http://www.codereye.com/2014/12/show-resultset-as-html-table.html
 */

package com.izmar.lunatech.viewcontroller;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bashan
 *         Date: 18/11/2014 15:07
 */
public class ResultSetToTable {

  public void writeTable(ResultSet rs, Writer writer, Column[] columns) throws IOException, SQLException {
    writer.write("<table>");
    // Write titles
    writer.write("\t<tr>\n");
    for (Column column : columns) {
      writer.write("\t\t<th>" + column.title + "</th>");
    }
    writer.write("\t</tr>\n");

    // Write data
    int cols = columns.length;
    while (rs.next()) {
      writer.write("\t<tr>\n");
      for (int i = 0; i < cols; i++) {
        ColumnType columnType = columns[i].columnType;
        String columnAlign = columnAlignToColumnCss.get(columns[i].columnAlign);
        writer.write("\t\t<td" + columnAlign + ">" + format(columnType, rs.getObject(i + 1)) + "</td>");
      }
      writer.write("\t</tr>");
    }

    writer.write("</table>");
  }

  private static Map<Integer, ColumnType> sqlTypeToColumnType = new HashMap<Integer, ColumnType>();
  private static Map<ColumnType, ColumnAlign> columnTypeTpColumnAlign = new HashMap<ColumnType, ColumnAlign>();
  private static Map<ColumnAlign, String> columnAlignToColumnCss = new HashMap<ColumnAlign, String>();

  static {
    // Map SQL types to column types

    // integer
    sqlTypeToColumnType.put(Types.INTEGER, ColumnType.INTEGER);
    sqlTypeToColumnType.put(Types.BIGINT, ColumnType.INTEGER);
    sqlTypeToColumnType.put(Types.SMALLINT, ColumnType.INTEGER);
    sqlTypeToColumnType.put(Types.TINYINT, ColumnType.INTEGER);
    sqlTypeToColumnType.put(Types.NUMERIC, ColumnType.INTEGER);

    // float
    sqlTypeToColumnType.put(Types.FLOAT, ColumnType.INTEGER);
    sqlTypeToColumnType.put(Types.DOUBLE, ColumnType.INTEGER);
    sqlTypeToColumnType.put(Types.DECIMAL, ColumnType.INTEGER);

    // date
    sqlTypeToColumnType.put(Types.TIME, ColumnType.DATE);
    sqlTypeToColumnType.put(Types.TIMESTAMP, ColumnType.DATE);

    // Map column type to column alignment
    columnTypeTpColumnAlign.put(ColumnType.INTEGER, ColumnAlign.RIGHT);
    columnTypeTpColumnAlign.put(ColumnType.FLOAT, ColumnAlign.RIGHT);
    columnTypeTpColumnAlign.put(ColumnType.CURRENCY, ColumnAlign.RIGHT);
    columnTypeTpColumnAlign.put(ColumnType.DATE, ColumnAlign.LEFT);
    columnTypeTpColumnAlign.put(ColumnType.STRING, ColumnAlign.LEFT);

    // Map column alignment to css style
    columnAlignToColumnCss.put(ColumnAlign.LEFT, "");
    columnAlignToColumnCss.put(ColumnAlign.RIGHT, " style=\"text-align:right\"");
    columnAlignToColumnCss.put(ColumnAlign.CENTER, " style=\"text-align:center\"");
  }

  public void writeTable(ResultSet rs, Writer writer) throws IOException, SQLException {
    ResultSetMetaData meta = rs.getMetaData();
    int cols = meta.getColumnCount();

    Column[] columns = new Column[meta.getColumnCount()];
    for (int i = 0; i < cols; i++) {
      ColumnType columnType = sqlTypeToColumnType.get(meta.getColumnType(i + 1));
      ColumnAlign columnAlign = columnTypeTpColumnAlign.get(columnType);
      columns[i] = new Column(
          columnType != null ? columnType : ColumnType.STRING,
          meta.getColumnName(i + 1),
          columnAlign != null ? columnAlign : ColumnAlign.LEFT);
    }

    writeTable(rs, writer, columns);
  }

  public enum ColumnType {
    STRING, INTEGER, FLOAT, DATE, DATE_TIME, TIME, CURRENCY, PERCENTAGE
  }

  public enum ColumnAlign {
    LEFT, RIGHT, CENTER
  }

  private static NumberFormat integerFormat = NumberFormat.getInstance();
  private static NumberFormat floatFormat = NumberFormat.getInstance();
  private static final NumberFormat percentFormat = NumberFormat.getPercentInstance();
  private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
  private static final DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
  private static final DateFormat dateTimeFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
  private static final DateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");

  static {
    floatFormat.setMaximumFractionDigits(2);
    integerFormat.setMaximumFractionDigits(0);
  }

  public String format(ColumnType columnType, Object value) {
    String result;
    if (value != null) {
      if (columnType == ColumnType.STRING) {
        result = value.toString();
      } else {
        Format format;
        if (columnType == ColumnType.FLOAT) {
          format = floatFormat;
        } else if (columnType == ColumnType.CURRENCY) {
          format = currencyFormat;
        } else if (columnType == ColumnType.PERCENTAGE) {
          format = percentFormat;
        } else if (columnType == ColumnType.DATE) {
          format = dateFormat;
        } else if (columnType == ColumnType.DATE_TIME) {
          format = dateTimeFormat;
        } else if (columnType == ColumnType.TIME) {
          format = timeFormat;
        }else {
          format = integerFormat;
        }

        result = format.format(value);
      }
    } else {
      result = "N/A";
    }

    return result;
  }

  public static class Column {
    ColumnType columnType;
    String title;
    ColumnAlign columnAlign;

    public Column(ColumnType columnType, String title, ColumnAlign columnAlign) {
      this.columnType = columnType;
      this.title = title;
      this.columnAlign = columnAlign;
    }
  }
}
