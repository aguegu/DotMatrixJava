package aguegu.dotmatrix;

import javax.swing.JPanel;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

class DotMatrixPanel extends JPanel
{
	private static final long serialVersionUID = -2531292225634588108L;
	private DotMatrix dm;
	private DotMatrixImage[] dmi;

	public DotMatrixPanel(DotMatrix dm)
	{
		super();
		this.setSize(DotMatrixImage.getBlockWidth() * (9 * 8 + 1),
				DotMatrixImage.getBlockWidth() * (9 * 3 + 1));

		this.dm = dm;
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

				// System.out.printf("(%d, %d)", e.getX(), e.getY());
				// System.out.printf("(%d, %d)", blockX, blockY);
				// System.out.printf("(%d, %d, %d)\n", blockID, blockC, blockR);

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

				dm.reverseDot(index); 

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
		for (int i = 0; i < DotMatrix.DOT_LENGTH; i++)
		{
			int x = i % 8;
			int y = i / 8 % 8;
			int z = i / 64;

			boolean val = dm.getDot(i);
			
			dmi[y].setDot(z * 8 + x, val);
			dmi[8 + x].setDot(z * 8 + 7 - y, val);
			dmi[16 + z].setDot((7 - y) * 8 + x, val);
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
				int index = r * 8 + c;
				g2d.drawImage(dmi[index], x, y, null);
			}
		}
	}

	
}
