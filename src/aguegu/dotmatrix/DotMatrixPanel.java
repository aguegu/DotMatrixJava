package aguegu.dotmatrix;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

class DotMatrixPanel extends JPanel
{
	private static final long serialVersionUID = -2531292225634588108L;

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setBackground(Color.lightGray);
		g2d.clearRect(0, 0, this.getWidth(), this.getHeight());

		for (int r = 0; r < 3; r++)
		{
			for (int c = 0; c < 8; c++)
			{
				DotMatrixImage dmi = new DotMatrixImage();
				g2d.drawImage(dmi, 13 + (dmi.getWidth() + 13) * c, 13 + (dmi.getWidth() + 13) * r, null);
			}
		}
	}
}
