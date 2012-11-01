package aguegu.dotmatrix;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

class DotMatrixPanel extends JPanel
{
	private static final long serialVersionUID = -2531292225634588108L;

	private static final int BLOCK_WIDTH = 13;
	private static final Color BACKGROUND_COLOR = Color.lightGray;
	private static final Color ON_Color = Color.white;
	private static final Color OFF_Color = Color.gray;
	
	private boolean[] _dot;
	
	public DotMatrixPanel()
	{
		super();
		_dot = new boolean[64];
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setBackground(BACKGROUND_COLOR);
		g2d.clearRect(0, 0, this.getWidth(), this.getHeight());

		g2d.setColor(ON_Color);
		BasicStroke bs = new BasicStroke(BLOCK_WIDTH - 1);
		g2d.setStroke(bs);

		for (int r = 0; r < 8; r++)
		{
			int y = BLOCK_WIDTH + r * BLOCK_WIDTH;
			for (int c = 0; c < 8; c++)
			{
				int x = BLOCK_WIDTH + c * BLOCK_WIDTH;
				g2d.drawLine(x, y, x, y);
			}
		}
	}
}
