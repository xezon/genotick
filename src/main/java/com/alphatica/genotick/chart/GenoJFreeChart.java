package com.alphatica.genotick.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import com.alphatica.genotick.ui.UserOutput;

import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static com.alphatica.genotick.utility.Assert.gassert;

class GenoJFreeChart implements GenoChart {
    
    private class XYChartDefinition {
        final Map<String, XYSeries> xySeriesMap = new HashMap<>();
        String xLabel;
        String yLabel;
    }
    
    private class TimeChartDefinition {
        final Map<String, OHLCSeries> ohlcSeries = new HashMap<>();
        final Map<String, TimeSeries> timeSeries = new HashMap<>();
    }
    
    private final UserOutput output;
    private final Map<String, XYChartDefinition> xyChartMap;
    private final Map<String, TimeChartDefinition> timeChartMap;
    private final boolean drawChart;
    private final boolean saveChart;
    private int numOpenedChartFrames;
    
    GenoJFreeChart(GenoChartMode mode, UserOutput output) {
        this.output = output;
        this.xyChartMap = new HashMap<>();
        this.timeChartMap = new HashMap<>();
        this.drawChart = mode.contains(GenoChartMode.DRAW);
        this.saveChart = mode.contains(GenoChartMode.SAVE);
        this.numOpenedChartFrames = 0;
        gassert(this.drawChart || this.saveChart);
    }

    @Override
    public void addXYLineChart(
            String chartTitle, String xLabel, String yLabel,
            String seriesTitle, double xValue, double yValue) {
        XYChartDefinition definition = getOrCreateXYChartDefinition(chartTitle, xLabel, yLabel);
        XYSeries series = definition.xySeriesMap.computeIfAbsent(seriesTitle, XYSeries::new);
        series.add(xValue, yValue);
    }
    
    @Override
    public void addCandlestickChart(String chartTitle, String seriesTitle, Date date, double open, double high, double low, double close) {
        TimeChartDefinition definition = getOrCreateTimeChartDefinition(chartTitle);
        OHLCSeries series = definition.ohlcSeries.computeIfAbsent(seriesTitle, OHLCSeries::new);
        series.add(new Minute(date), open, high, low, close);
    }
    
    @Override
    public void addTimeSeriesChart(String chartTitle, String seriesTitle, Date date, double value) {
        TimeChartDefinition definition = getOrCreateTimeChartDefinition(chartTitle);
        TimeSeries series = definition.timeSeries.computeIfAbsent(seriesTitle, TimeSeries::new);
        series.add(new Minute(date), value);
    }

    @Override
    public void plot(String chartTitle) {
        plotXYChart(chartTitle);
        plotTimeChart(chartTitle);
    }

    @Override
    public void plotAll() {
        plotCharts();
    }

    private XYChartDefinition getOrCreateXYChartDefinition(String chartTitle, String xLabel, String yLabel) {
        XYChartDefinition definition = xyChartMap.get(chartTitle);
        if (isNull(definition)) {
            definition = new XYChartDefinition();
            definition.xLabel = xLabel;
            definition.yLabel = yLabel;
            xyChartMap.put(chartTitle, definition);
        }
        return definition;
    }
    
    private TimeChartDefinition getOrCreateTimeChartDefinition(String chartTitle) {
        TimeChartDefinition definition = timeChartMap.get(chartTitle);
        if (isNull(definition)) {
            definition = new TimeChartDefinition();
            timeChartMap.put(chartTitle, definition);
        }
        return definition;
    }
    
    private void plotXYChart(String title) {
        XYChartDefinition definition = xyChartMap.remove(title);
        if (nonNull(definition)) {
            plotChart(title, definition);
        }
    }
    
    private void plotTimeChart(String title) {
        TimeChartDefinition definition = timeChartMap.remove(title);
        if (nonNull(definition)) {
            plotChart(title, definition);
        }
    }

    private void plotCharts() {
        xyChartMap.forEach(this::plotChart);
        xyChartMap.clear();
        timeChartMap.forEach(this::plotChart);
        timeChartMap.clear();
    }

    private void plotChart(String title, XYChartDefinition definition) {
        // A JFreeChart object cannot be reused for drawing and saving
        // Thus individual objects must be created for each operation to avoid a memory corruption
        if (drawChart) {
            JFreeChart chartObject = createChartObject(title, definition);
            drawChart(title, chartObject);
        }
        if (saveChart) {
            JFreeChart chartObject = createChartObject(title, definition);
            saveChart(title, chartObject);
        }
    }

    private void plotChart(String title, TimeChartDefinition definition) {
        if (drawChart) {
            JFreeChart chartObject = createChartObject(title, definition);
            drawChart(title, chartObject);
        }
        if (saveChart) {
            JFreeChart chartObject = createChartObject(title, definition);
            saveChart(title, chartObject);
        }
    }

    private JFreeChart createChartObject(String title, XYChartDefinition definition) {
        boolean legend = true;
        boolean tooltips = true;
        boolean urls = false;
        XYSeriesCollection collection = new XYSeriesCollection();
        definition.xySeriesMap.forEach((seriesTitle, series) -> collection.addSeries(series));
        JFreeChart chartObject = ChartFactory.createXYLineChart(
                title, definition.xLabel, definition.yLabel, collection,
                PlotOrientation.VERTICAL, legend, tooltips, urls);
        XYPlot xyPlot = chartObject.getXYPlot();
        NumberAxis numberAxis = (NumberAxis)xyPlot.getRangeAxis();
        numberAxis.setAutoRangeIncludesZero(false);
        return chartObject;
    }
    
    private JFreeChart createChartObject(String title, TimeChartDefinition definition) {
        boolean legend = true;
        OHLCSeriesCollection ohlcSeriesCollection = new OHLCSeriesCollection();
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        definition.ohlcSeries.forEach((seriesTitle, series) -> ohlcSeriesCollection.addSeries(series));
        definition.timeSeries.forEach((seriesTitle, series) -> timeSeriesCollection.addSeries(series));
        JFreeChart chartObject = ChartFactory.createCandlestickChart(
                title, "time", "price", ohlcSeriesCollection, legend);
        XYPlot xyPlot = chartObject.getXYPlot();
        NumberAxis numberAxis = (NumberAxis)xyPlot.getRangeAxis();
        DateAxis dateAxis = (DateAxis)xyPlot.getDomainAxis();
        dateAxis.setDateFormatOverride(new SimpleDateFormat("dd-LLL-yy hh:mm", Locale.ENGLISH));
        CandlestickRenderer renderer = (CandlestickRenderer)xyPlot.getRenderer();
        renderer.setAutoWidthMethod(CandlestickRenderer.WIDTHMETHOD_SMALLEST);
        for (int i = 0, size = definition.ohlcSeries.size(); i < size; ++i) {
            renderer.setSeriesPaint(i, Color.decode("#222222"));
        }
        numberAxis.setAutoRangeIncludesZero(false);
        addDatasetToPlot(xyPlot, timeSeriesCollection);
        return chartObject;
    }
    
    private void addDatasetToPlot(XYPlot xyPlot, XYDataset xyDataset) {
        int index = xyPlot.getDatasetCount();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        for (int i = 0, size = xyDataset.getSeriesCount(); i < size; ++i) {
            renderer.setSeriesShapesVisible(i, false);
            renderer.setSeriesStroke(i, new BasicStroke(1.5f));
        }
        xyPlot.setRenderer(index, renderer);
        xyPlot.setDataset(index, xyDataset);
        xyPlot.mapDatasetToRangeAxis(index, 0);
        xyPlot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
    }

    private void drawChart(String title, JFreeChart chartObject) {
        ChartFrame frame = new ChartFrame(title, chartObject);
        ChartPanel chartPanel = frame.getChartPanel();
        chartPanel.setMouseZoomable(true);
        chartPanel.setMouseWheelEnabled(true);
        frame.pack();
        frame.setVisible(true);
        frame.toFront();
        alignChartFrame(frame);
        numOpenedChartFrames++;
    }

    private void saveChart(String title, JFreeChart chartObject) {
        String filename = makeFileName(title);
        File file = new File(output.getOutDir(), filename);
        try {
            ChartUtilities.saveChartAsPNG(file, chartObject, 512, 512);
        }
        catch (IOException exception) {
            output.errorMessage(format("JFreeChart: Unable to save chart image %s: %s", filename, exception.getMessage()));
        }
    }

    private String makeFileName(String title) {
        String chartName = title.toLowerCase().replace(" ", "_");
        String identifier = output.getIdentifier();
        return String.format("chart_%s_%s.png", chartName, identifier);
    }

    private void alignChartFrame(ChartFrame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double pixels = 40.0 * numOpenedChartFrames;
        double widthPercent = pixels / screenSize.getWidth();
        double heightPercent = pixels / screenSize.getHeight();
        RefineryUtilities.positionFrameOnScreen(frame, widthPercent, heightPercent);
    }
}
