package aguegu.dotmatrix;

import javax.swing.JPanel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

class DotMatrixPanel extends JPanel
{
	private static final long serialVersionUID = -2531292225634588108L;
	private boolean[] dot;
	private DotMatrixImage[] dmi;

	public DotMatrixPanel()
	{
		super();
		this.setSize(DotMatrixImage.getBlockWidth() * (9 * 8 + 1),
				DotMatrixImage.getBlockWidth() * (9 * 3 + 1));

		dot = new boolean[512];
		dmi = new DotMatrixImage[24];
		for (int i = 0; i < dmi.length; i++)
		{
			dmi[i] = new DotMatrixImage();
		}

		this.addMouseListener(new MA());
		init();
	}

	class MA implements MouseListener
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				int blockX = e.getX() / DotMatrixImage.getBlockWidth();
				int blockY = e.getY() / DotMatrixImage.getBlockWidth();

				int blockID = blockX / 9;
				int blockC = blockX % 9 - 1;
				int blockR = blockY % 9 - 1;

				if (blockID >= 8 || blockC < 0 || blockR < 0)
					return;

				System.out.printf("(%d, %d)", e.getX(), e.getY());
				System.out.printf("(%d, %d)", blockX, blockY);
				System.out.printf("(%d, %d, %d)\n", blockID, blockC, blockR);

				int index = -1;
				switch (blockY / 9)
				{
				case 0:
					index = blockR * 64 + blockID * 8 + blockC;
					break;
				case 1:
					index = blockR * 64 + (7 - blockC) * 8 + blockID;
					break;
				case 2:
					index = blockID * 64 + (7 - blockR) * 8 + blockC;
					break;
				}

				try
				{
					dot[index] = !dot[index];
				}
				catch (ArrayIndexOutOfBoundsException ex)
				{
				}

				update();
				repaint();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e)
		{
			// TODO Auto-generated method stub

		}

	}

	private void init()
	{		
		this.update();
	}

	public void update()
	{
		for (int i = 0; i < dot.length; i++)
		{
			int x = i % 8;
			int y = i / 8 % 8;
			int z = i / 64;

			dmi[y].setDot(z * 8 + x, dot[i]);
			dmi[8 + x].setDot(z * 8 + 7 - y, dot[i]);
			dmi[16 + z].setDot((7 - y) * 8 + x, dot[i]);
		}

		for (DotMatrixImage image : dmi)
		{
			image.update();
		}
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setBackground(Color.lightGray);
		g2d.clearRect(0, 0, this.getWidth(), this.getHeight());

		for (int r = 0, y = DotMatrixImage.getBlockWidth(); r < 3; r++, y += DotMatrixImage
				.getBlockWidth() * 9)
		{
			for (int c = 0, x = DotMatrixImage.getBlockWidth(); c < 8; c++, x += DotMatrixImage
					.getBlockWidth() * 9)
			{
				g2d.drawImage(dmi[r * 8 + c], x, y, null);
			}
		}
	}
}
