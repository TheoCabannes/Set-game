import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

@SuppressWarnings("serial")
class SetCard extends Canvas {
	private int color, number, shade, shape;
	boolean selected;

	public SetCard(int card) {
		color = ((card >> 2) & 0x3) - 1;
		number = (card & 0x3) - 1;
		shape = ((card >> 6) & 0x3) - 1;
		shade = ((card >> 4) & 0x3) - 1;
		selected = false;
	}

	public void set(SetCard setCard) {
		color = setCard.color;
		number = setCard.number;
		shape = setCard.shape;
		shade = setCard.shade;
		repaint();
	}

	public int valueOf() {
		return Set.valueOf(number + 1, color + 1, shade + 1, shape + 1);
	}

	public void setSelected(boolean sel) {
		if (selected != sel) {
			selected = sel;
			repaint();
		}
	}

	public void paint(Graphics g) {
		int xpoints[] = new int[4];
		int ypoints[] = new int[4];
		Color bordercolor, fillcolor, backcolor;
		Dimension d = getSize();
		int w = d.width - 1;
		int h = d.height - 1;
		fillcolor = (color == 0) ? Color.red : (color == 1) ? Color.blue : (color == 2) ? Color.decode("0x00bf00") : Color.pink;
		bordercolor = fillcolor;
		if (selected)
			backcolor = Color.lightGray;
		else
			backcolor = Color.white;

		if (shade == 0) 
			fillcolor = backcolor;

		g.setColor(backcolor);
		g.fillRoundRect(2, 2, w - 4, h - 4, w / 10, h / 10);

		g.setColor(Color.black);
		g.drawRoundRect(1, 1, w - 2, h - 2, w / 10, h / 10);
		g.drawRoundRect(1, 1, w - 3, h - 2, w / 10, h / 10);
		g.drawRoundRect(1, 1, w - 2, h - 3, w / 10, h / 10);

		int count = number + 1;
		int sw = 3;

		for (int i = 0; i < count; i++) {
			switch (shape) {
			case 0:  //Losange
				xpoints[0] = (int) (w * 0.2);
				xpoints[1] = (int) (w * 0.5);
				xpoints[2] = (int) (w * 0.8);
				xpoints[3] = (int) (w * 0.5);
				ypoints[0] = (int) (h * (i + .5) / count);
				ypoints[1] = (int) (h * (i + .5) / count - h / 10);
				ypoints[2] = (int) (h * (i + .5) / count);
				ypoints[3] = (int) (h * (i + .5) / count + h / 10);
				g.setColor(fillcolor);
				g.fillPolygon(xpoints, ypoints, 4);

				if (shade == 1) {
					g.setColor(backcolor);
					for (int x = (int) (w * 0.2); x < w * 0.8; x += sw * 2) {
						for (int j = 0; j < sw; j++)
							g.drawLine(x + j, (int) (h * (i + .5) / count - h / 10), x + j,
									(int) (h * (i + .5) / count + h / 10));
					}
				}

				g.setColor(bordercolor);
				for (int k = 0; k < 3; k++) {
					g.drawPolygon(xpoints, ypoints, 4);
					for (int j = 0; j < 4; j++)
						ypoints[j]++;
				}
				break;
			case 1: //Ovale
				g.setColor(fillcolor);
				g.fillOval((int) (w * .2), (int) (h * (i + .5) / count - h / 9), (int) (w * .6), (int) (h / 6));

				if (shade == 1) {
					g.setColor(backcolor);
					for (int x = (int) (w * 0.19); x < w * 0.8; x += sw * 2) {
						for (int j = 0; j < sw; j++)
							g.drawLine(x + j, (int) (h * (i + .5) / count - h / 10), x + j,
									(int) (h * (i + .5) / count + h / 10));
					}
				}

				g.setColor(bordercolor);
				for (int k = 0; k < 3; k++) {
					g.drawOval((int) (w * .2), (int) (h * (i + .5) / count - h / 9) + k, (int) (w * .6), (int) (h / 6));
				}
				break;
			case 2: //Rectangle
				g.setColor(fillcolor);
				g.fillRect((int) (w * .2), (int) (h * (i + .5) / count - h / 9), (int) (w * .6), (int) (h / 6));

				if (shade == 1) {
					g.setColor(backcolor);
					for (int x = (int) (w * 0.19); x < w * 0.8; x += sw * 2) {
						for (int j = 0; j < sw; j++)
							g.drawLine(x + j, (int) (h * (i + .5) / count - h / 10), x + j,
									(int) (h * (i + .5) / count + h / 10));
					}
				}

				g.setColor(bordercolor);
				for (int k = 0; k < 3; k++) {
					g.drawRect((int) (w * .2), (int) (h * (i + .5) / count - h / 9) + k, (int) (w * .6), (int) (h / 6));
				}
				break;
			}
		}
	}

	public Dimension getPreferredSize() {
		return new Dimension(Joueur.cellWidth - 5, Joueur.cellLength - 10);
	}
}