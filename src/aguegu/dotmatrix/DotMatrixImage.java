package aguegu.dotmatrix;

import java.awt.image.BufferedImage;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class DotMatrixImage extends BufferedImage
{
	private static int blockWidth = 13;
	private static Color backgroundColor = Color.lightGray;
	private static Color onColor = Color.white;
	private static Color offColor = Color.gray;
	private static BasicStroke bs;

	private Graphics2D g2d;

	public DotMatrixImage()
	{
		super(blockWidth * 8, blockWidth * 8, BufferedImage.TYPE_BYTE_GRAY);

		g2d = this.createGraphics();
		g2d.setBackground(backgroundColor);
		g2d.clearRect(0, 0, this.getWidth(), this.getHeight());
		
		bs = new BasicStroke(blockWidth - 1);
		this.update();
	}

	public void update()
	{
		g2d.setColor(onColor);		
		g2d.setStroke(bs);

		for (int r = 0; r < 8; r++)
		{
			int y = 6 + r * blockWidth;
			for (int c = 0; c < 8; c++)
			{
				int x = 6 + c * blockWidth;
				g2d.drawLine(x, y, x, y);
			}
		}
	}
}
