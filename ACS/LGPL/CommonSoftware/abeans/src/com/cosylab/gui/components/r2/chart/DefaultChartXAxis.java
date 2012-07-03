package com.cosylab.gui.components.r2.chart;

/**
 * Default class for drawing x axis on chart.
 */
public class DefaultChartXAxis extends AbstractChartAxis implements ChartXAxis {
/**
 * DefaultChartXAxis constructor.
 */
public DefaultChartXAxis() {
	super();
}
/**
 * This method draw X axis (with thickes, signatures etc.)
 * @param g java.awt.Graphics
 */
public void drawAxis(java.awt.Graphics g) {

	if (chartSize==null) return;

	if (xScale.isLogaritmic()) {
		drawLogAxis(g);
	} else {
		drawLinAxis(g);
	}

}
/**
 * This method draw X axis (with thickes, signatures etc.)
 * @param g java.awt.Graphics
 */
private void drawLinAxis(java.awt.Graphics g) {

	g.setColor(lineColor);

	// draw axis

	g.drawLine(-majorTickLength,chartSize.height-1,chartSize.width-1+majorTickLength,chartSize.height-1);
//	g.drawLine(0,0,chartSize.width-1,0);

	//  min and max tickmarks

	g.drawLine(
		chartSize.width-1,
		chartSize.height-1,
		chartSize.width-1,
		chartSize.height+majorTickLength-2);

	// draw minor tickmarks

	if (chartSize.width < 20)
		return;

	for (int i= 0; i < 9; i++) {
		g.drawLine(
			(int) ((i + 1) * (double) chartSize.width / 10.0),
			chartSize.height-1,
			(int) ((i + 1) * (double) chartSize.width / 10.0),
			chartSize.height-2+minorTickLength);
	}

	// write out min and max values for x and y

	g.setColor(fontColor);
	g.setFont(font);

	String s= format(xScale.min());
	java.awt.FontMetrics fm= g.getFontMetrics(font);

	int width= fm.stringWidth(s);
	
	g.drawString(
		s,
		(int)(-width/2.0),
		chartSize.height + fm.getHeight() + textMargin.top);
	
	s= format(xScale.max());

	int width1= fm.stringWidth(s);

	g.drawString(
		format(xScale.max()),
		chartSize.width-(int)(width1/2.0),
		chartSize.height + fm.getHeight() +textMargin.top);

	preferedHeight= fm.getHeight() + textMargin.bottom+textMargin.top+majorTickLength;
	preferedWidth= ( width>width1 ? width : width1 ) + textMargin.left+textMargin.right+majorTickLength;


}
/**
 * This method draw X axis (with thickes, signatures etc.)
 * @param g java.awt.Graphics
 */
private void drawLogAxis(java.awt.Graphics g) {

	// draw axis

	g.drawLine(-majorTickLength,chartSize.height-1,chartSize.width-1+majorTickLength,chartSize.height-1);

	//  min and max tickmarks

	g.drawLine(
		chartSize.width-1,
		chartSize.height-1,
		chartSize.width-1,
		chartSize.height+majorTickLength-2);

	// write out min and max values for x and y

	String s= format(xScale.min());
	java.awt.FontMetrics fm= g.getFontMetrics(font);

	int width= fm.stringWidth(s);
	
	g.drawString(
		s,
		(int)(-width/2.0)-3,
		chartSize.height + fm.getHeight() + textMargin.top);
	
	s= format(xScale.max());

	int width1= fm.stringWidth(s);

	g.drawString(
		format(xScale.max()),
		chartSize.width-(int)(width1/2.0)-3,
		chartSize.height + fm.getHeight() +textMargin.top);

	preferedHeight= fm.getHeight() + textMargin.bottom+textMargin.top+majorTickLength;
	preferedWidth= ( width>width1 ? width : width1 ) + textMargin.left+textMargin.right+majorTickLength;

	// draw minor tickmarks

	if (chartSize.width < 20)
		return;

	for (int i= 1; i < 10; i++) {
		g.drawLine(
			(int) (Math.log(i) * (double) chartSize.width ),
			chartSize.height-1,
			(int) (Math.log(i) * (double) chartSize.width ),
			chartSize.height-2+minorTickLength);
	}

}
}
