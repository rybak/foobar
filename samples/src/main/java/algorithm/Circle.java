package algorithm;

/**
 * Bresenham algorithm for drawing a bitmap circle of given radius.
 *
 * https://gist.github.com/amaembo/aabca85b2c24cde7fd930fab936087e9
 * https://twitter.com/tagir_valeev/status/1417057736609734656
 */
public class Circle {
	public static void main(String[] args) {
		// Preparation
		int radius = Integer.parseInt(args[0]);
		int rasterSize = radius * 2 + 1;
		boolean[][] raster = new boolean[rasterSize][rasterSize];

		// Bresenham algorithm
		int y = radius;
		int err = radius;
		for (int x = 0; x <= y; x++) {
			raster[radius+x][radius+y] = true; raster[radius+y][radius+x] = true;
			raster[radius+x][radius-y] = true; raster[radius+y][radius-x] = true;
			raster[radius-x][radius+y] = true; raster[radius-y][radius+x] = true;
			raster[radius-x][radius-y] = true; raster[radius-y][radius-x] = true;
			err -= 2 * x + 1;
			if (err < 0) {
				err += 2 * y - 1;
				y--;
			}
		}

		// Output
		for (int i = 0; i < rasterSize; i++) {
			for (int j = 0; j < rasterSize; j++) {
				System.out.print(raster[i][j] ? "██" : "  ");
			}
			System.out.println();
		}
	}
}