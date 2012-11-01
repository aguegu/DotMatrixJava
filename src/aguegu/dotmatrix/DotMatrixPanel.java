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
		g2d.setBackground(Color.black);
		g2d.clearRect(0, 0, this.getWidth(), this.getHeight());

		g2d.drawImage(new DotMatrixImage(), 12, 12, null);

	}
}
