package gui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import net.ericaro.surfaceplotter.surface.AbstractSurfaceModel;
import net.ericaro.surfaceplotter.surface.JSurface;
import net.ericaro.surfaceplotter.surface.SurfaceModel;
import net.ericaro.surfaceplotter.surface.SurfaceVertex;

public final class SurfaceChart extends JComponent {

    private static final long serialVersionUID = 1L;

    public SurfaceChart() {

        setBorder(BorderFactory.createEmptyBorder());

        this.setLayout(new BorderLayout());

        // final Map map = (Map) variable;

        MapSurfaceModel arraySurfaceModel = new MapSurfaceModel();
        // arraySurfaceModel.setValues(map.getValues().getXAxis(), map.getValues().getYAxis(), map.getValues().getZvalues());

        JSurface surface = new JSurface(arraySurfaceModel);
        // surface.setXLabel("X [" + map.getUnitX() + "]");
        // surface.setYLabel("Y [" + map.getUnitY() + "]");

        this.add(surface);

    }

    public class MapSurfaceModel extends AbstractSurfaceModel {

        private SurfaceVertex[][] surfaceVertex;

        public MapSurfaceModel() {
            setPlotFunction2(false);
            setBoxed(true);
            setDisplayXY(true);
            setExpectDelay(false);
            setDisplayZ(true);
            setMesh(true);
            setPlotType(SurfaceModel.PlotType.SURFACE);
            setDisplayGrids(true);
            setPlotColor(SurfaceModel.PlotColor.SPECTRUM);
            setFirstFunctionOnly(true);
            setZMin(Float.MAX_VALUE);
            setZMax(Float.MIN_VALUE);
        }

        public void setValues(float[] xAxis, float[] yAxis, float[][] zValues) {
            setDataAvailable(false);

            final int xLength = xAxis.length;
            final int yLength = yAxis.length;

            setXMin(xAxis[0]);
            setXMax(xAxis[(xLength - 1)]);
            setYMin(yAxis[0]);
            setYMax(yAxis[(yLength - 1)]);
            setCalcDivisions(Math.max(xLength - 1, yLength - 1));

            final float xfactor = 20.0F / (xMax - xMin);
            final float yfactor = 20.0F / (yMax - yMin);

            final int total = (calcDivisions + 1) * (calcDivisions + 1);
            surfaceVertex = new SurfaceVertex[1][total];

            for (int i = 0; i < xLength; i++) {
                for (int j = 0; j < yLength; j++) {
                    int k = i * (calcDivisions + 1) + j;
                    float xv = xAxis[i];
                    float yv = yAxis[j];
                    float v1 = 0;

                    if (zValues != null) {
                        v1 = zValues[j][i];
                        z1Max = Math.max(z1Max, v1);
                        z1Min = Math.min(z1Min, v1);
                    } else {
                        v1 = Float.NaN;
                    }

                    surfaceVertex[0][k] = new SurfaceVertex((xv - xMin) * xfactor - 10.0F, (yv - yMin) * yfactor - 10.0F, v1);
                }
            }

            for (int s = 0; s < total; s++) { // avoid NPE in plotArea
                if (surfaceVertex[0][s] == null) {
                    surfaceVertex[0][s] = new SurfaceVertex(Float.NaN, Float.NaN, Float.NaN);
                }
            }

            if (z1Max - z1Min == 0) {
                z1Max += 0.1;
                z1Min -= 0.1;
            }

            autoScale();
            setDataAvailable(true);
            fireStateChanged();
        }

        @Override
        public SurfaceVertex[][] getSurfaceVertex() {
            return this.surfaceVertex;
        }
    }

}
