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

    private final MapSurfaceModel arraySurfaceModel;
    private final JSurface surface;

    public SurfaceChart() {

        setBorder(BorderFactory.createEmptyBorder());

        this.setLayout(new BorderLayout());

        arraySurfaceModel = new MapSurfaceModel();

        surface = new JSurface(arraySurfaceModel);

        this.add(surface);

    }

    public MapSurfaceModel getArraySurfaceModel() {
        return arraySurfaceModel;
    }

    public JSurface getSurface() {
        return surface;
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

            float minZValue = Float.POSITIVE_INFINITY;
            float maxZValue = Float.NEGATIVE_INFINITY;

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

                        if (v1 < minZValue)
                            minZValue = v1;

                        if (v1 > maxZValue)
                            maxZValue = v1;

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

            zMin = minZValue;
            zMax = maxZValue;

            if (zMax - zMin == 0) {
                zMax += 0.1;
                zMin -= 0.1;
            }

            setDataAvailable(true);
            fireStateChanged();
        }

        @Override
        public SurfaceVertex[][] getSurfaceVertex() {
            return this.surfaceVertex;
        }
    }

}
