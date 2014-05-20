/**
 * Chart2D, a java library for drawing two dimensional charts.
 * Copyright (C) 2001 Jason J. Simas
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * The author of this library may be contacted at:
 * E-mail:  jjsimas@users.sourceforge.net
 * Street Address:  J J Simas, 887 Tico Road, Ojai, CA 93023-3555 USA
 */
package RTM.charts.piechart;

import net.sourceforge.chart2d.Chart2DProperties;
import net.sourceforge.chart2d.Dataset;
import net.sourceforge.chart2d.LegendProperties;
import net.sourceforge.chart2d.MultiColorsProperties;
import net.sourceforge.chart2d.Object2DProperties;
import net.sourceforge.chart2d.PieChart2D;
import net.sourceforge.chart2d.PieChart2DProperties;

/**
 * A simple 2D piechart. Based on Chart2D java library.
 * @author Janusz Martynia
 */
public class Simple2DPiechart{
  
  private String title;
  private String [] legendLabels;
  private double [] values;
  private final PieChart2D chart2D;
  
  public Simple2DPiechart(String title, String [] labels, double [] values){
    this.title  = title;
    this.legendLabels = labels;
    this.values = values;
    chart2D = new PieChart2D();
  }
  /**
   * Configure the chart and frame, and open the frame.
   */
  public void init() {

    //<-- Begin Chart2D configuration -->

    //Configure object properties
    Object2DProperties object2DProps = new Object2DProperties();
    object2DProps.setObjectTitleText (title);

    //Configure chart properties
    Chart2DProperties chart2DProps = new Chart2DProperties();
    chart2DProps.setChartDataLabelsPrecision (-3);

    //Configure legend properties
    LegendProperties legendProps = new LegendProperties();
    //String[] legendLabels =
    //  {"PieChart2D", "LBChart2D", "LLChart2D", "GraphChart2D", "Chart2D", "Object2D"};
    legendProps.setLegendLabelsTexts (legendLabels);

    //Configure dataset
    int numSets = values.length, numCats = 1, numItems = 1;
    Dataset dataset = new Dataset (numSets, numCats, numItems);
    for(int i = 0; i < values.length; i++){
      dataset.set (i, 0, 0, (float)values[i]);
  }
    //Configure graph component colors
    MultiColorsProperties multiColorsProps = new MultiColorsProperties();

    //Configure pie area
    PieChart2DProperties pieChart2DProps = new PieChart2DProperties();

    //Configure chart
    
    chart2D.setObject2DProperties (object2DProps);
    chart2D.setChart2DProperties (chart2DProps);
    chart2D.setLegendProperties (legendProps);
    chart2D.setDataset (dataset);
    chart2D.setMultiColorsProperties (multiColorsProps);
    chart2D.setPieChart2DProperties (pieChart2DProps);

    //Optional validation:  Prints debug messages if invalid only.
    if (!chart2D.validate (false)) chart2D.validate (true);

    //<-- End Chart2D configuration -->
  }
  public PieChart2D get() {
      return chart2D;
  }
}
