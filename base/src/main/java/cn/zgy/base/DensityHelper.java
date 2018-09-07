package cn.zgy.base;

/**
 * Dpi相关类
 *
 * @author a_liYa
 * @date 2017/3/31 16:30.
 */
public class DensityHelper {

    /**
     * 匹配理论Dpi
     *
     * @return
     */
    public static int matchTheoryDpi() {

        int wPx = UIUtils.getScreenW();
        int hPx = UIUtils.getScreenH();

        DpiEntity[] values = DpiEntity.values();

        double min = -1;
        int index = 0;

        for (int i = 0; i < values.length; i++) {
            DpiEntity value = values[i];
            double distance = getDistance(value.getwPx(), value.gethPx(), wPx, hPx);
            if (min < 0) {
                min = distance;
                index = i;
            } else if (distance < min) {
                min = distance;
                index = i;
            }
        }

        return values[index].getDpi();
    }

    private static double getDistance(int x1, int y1, int x2, int y2) {
        double _x = Math.abs(x1 - x2);
        double _y = Math.abs(y1 - y2);
        return Math.sqrt(_x * _x + _y * _y);
    }


    public enum DpiEntity {

        L(240, 320, 120, 0.75f),
        M(320, 480, 160, 1f),
        H(480, 800, 240, 1.5f),
        XH(720, 1280, 320, 2f),
        XXH(1080, 1920, 480, 3f),
        XXXH(1440, 2560, 560, 3.5f);

        private int wPx;
        private int hPx;
        private int dpi;
        private float density;

        DpiEntity(int wPx, int hPx, int dpi, float density) {
            this.wPx = wPx;
            this.hPx = hPx;
            this.dpi = dpi;
            this.density = density;
        }


        public int getwPx() {
            return wPx;
        }

        public void setwPx(int wPx) {
            this.wPx = wPx;
        }

        public int gethPx() {
            return hPx;
        }

        public void sethPx(int hPx) {
            this.hPx = hPx;
        }

        public int getDpi() {
            return dpi;
        }

        public void setDpi(int dpi) {
            this.dpi = dpi;
        }

        public float getDensity() {
            return density;
        }

        public void setDensity(float density) {
            this.density = density;
        }
    }

}
