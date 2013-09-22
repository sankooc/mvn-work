package org.alin.algorithm.shortest;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.net.URL;

class Node {
	int	x;
	int	y;
	int	delta_plus;	/* edge starts from this node */
	int	delta_minus;	/* edge terminates at this node */
	int	dist;		/* distance from the start node */
	int	prev;		/* previous node of the shortest path */
	int	succ,pred;	/* node in Sbar with finite dist. */
	int	w;
	int	h;
	int	pw;
	int	dx;
	int	dy;
	String	name;
}

class Edge {
	int	rndd_plus;	/* initial vertex of this edge */
	int	rndd_minus;	/* terminal vertex of this edge */
	int	delta_plus;	/* edge starts from rndd_plus */
	int	delta_minus;	/* edge terminates at rndd_minus */
	int	len;		/* length */
	String	name;
}

public class Dijkstra extends Applet implements MouseListener {
	int	n,m;
	int	u,snode;	/* start node */
	int	pre_s_first, pre_s_last;
	boolean	isdigraph;
	int	iteration, step;
	Node	v[] = new Node[100];
	Edge	e[] = new Edge[200];

	int findNode(String name) {
		for (int i=0; i<n; i++)
			if (v[i].name.equals(name))
				return i;
		return -1;
	}

	void input_graph(InputStream is) throws IOException {
		int x,y,l;
		String	s;

		Reader r = new BufferedReader(new InputStreamReader(is));
		StreamTokenizer st = new StreamTokenizer(r);
		st.commentChar('#');
		st.nextToken();	n = (int)st.nval;
		st.nextToken();	m = (int)st.nval;
		st.nextToken();	s = st.sval;
		isdigraph = "digraph".equals(s);
		for (int i = 0; i<n; i++) {
			Node node = new Node();
			st.nextToken();	node.name = st.sval;
			st.nextToken();	node.x = (int)st.nval;
			st.nextToken();	node.y = (int)st.nval;
			v[i] = node;
		}
		for (int i = 0; i<m; i++) {
			Edge edge = new Edge();
			st.nextToken();	edge.name = st.sval;
			switch (st.nextToken()) {
			case StreamTokenizer.TT_NUMBER:
				edge.rndd_plus = (int)st.nval;
				break;
			case StreamTokenizer.TT_WORD:
				edge.rndd_plus = findNode(st.sval);
				break;
			default:
				break;
			}
			switch (st.nextToken()) {
			case StreamTokenizer.TT_NUMBER:
				edge.rndd_minus = (int)st.nval;
				break;
			case StreamTokenizer.TT_WORD:
				edge.rndd_minus = findNode(st.sval);
				break;
			default:
				break;
			}
			st.nextToken();	edge.len = (int)st.nval;
			e[i] = edge;
		}
		for (int i=0; i<n; i++) {
			v[i].succ = v[i].pred = -2;
			v[i].prev = v[i].dist = -1;
			v[i].pw = 0;
		}
		iteration = step = 0;
	}

	void rdb() {
		int	i,k;

		for (i=0; i<n; i++)
			v[i].delta_plus = v[i].delta_minus = -1;
		for (i=0; i<m; i++)
			e[i].delta_plus = e[i].delta_minus = -1;
		for (i=0; i<m; i++) {
			k = e[i].rndd_plus;
			if (v[k].delta_plus == -1)
				v[k].delta_plus = i;
			else {
				k = v[k].delta_plus;
				while(e[k].delta_plus >= 0)
					k = e[k].delta_plus;
				e[k].delta_plus = i;
			}
			k = e[i].rndd_minus;
			if (v[k].delta_minus == -1)
				v[k].delta_minus = i;
			else {
				k = v[k].delta_minus;
				while(e[k].delta_minus >= 0)
					k = e[k].delta_minus;
				e[k].delta_minus = i;
			}
		}
	}

	void append_pre_s(int i) {
		v[i].succ = v[i].pred = -1;
		if (pre_s_first<0)
			pre_s_first = i;
		else
			v[pre_s_last].succ = i;
		v[i].pred = pre_s_last;
		pre_s_last = i;
	}

	void remove_pre_s(int i) {
		int	succ = v[i].succ;
		int	pred = v[i].pred;

		if (succ>=0)
			v[succ].pred = pred;
		else
			pre_s_last = pred;
		if (pred>=0)
			v[pred].succ = succ;
		else
			pre_s_first = succ;
	}

	void step1() {		/* initialize */
		u = snode;
		for (int i=0; i<n; i++) {
			v[i].succ = v[i].pred = -2;
			v[i].prev = v[i].dist = -1;
		}
		v[u].succ = -3;
		v[u].dist = 0;
		pre_s_first = pre_s_last = -1;
	}

	void step2() {		/* replace labels */
		int i,j;

		j = v[u].delta_plus;
		while (j>=0) {
			i = e[j].rndd_minus;
			if ((v[i].succ>=-2)&&((v[i].dist<0)||
				(v[i].dist>v[u].dist+e[j].len))) {
				if (v[i].dist<0)
					append_pre_s(i);
				v[i].dist = v[u].dist + e[j].len;
				v[i].prev = u;	  /* label */
			}
			j = e[j].delta_plus;
		}
		if (!isdigraph) {
		j = v[u].delta_minus;
		while (j>=0) {
			i = e[j].rndd_plus;
			if ((v[i].succ>=-2)&&((v[i].dist<0)||
				(v[i].dist>v[u].dist+e[j].len))) {
				if (v[i].dist<0)
					append_pre_s(i);
				v[i].dist = v[u].dist + e[j].len;
				v[i].prev = u;	  /* label */
			}
			j = e[j].delta_minus;
		}
		}
		v[u].succ = -4;
	}

	void step3() {		/* find the shortest node in Sbar */
		int i,rho;

		rho = -1;
		for (i = pre_s_first; i>=0; i = v[i].succ) {
			if ((rho < 0)||(rho>v[i].dist)) {
				rho = v[i].dist;
				u = i;
			}
		}
		remove_pre_s(u);
		v[u].succ = -3;
	}

	void step4() {
		v[u].succ = -4;
	}

	double weight(Node n, double x, double y) {
		double	w,z,xx,yy;

		w = 0;
		for (int j = n.delta_plus; j>=0; j=e[j].delta_plus) {
			xx = (double)(v[e[j].rndd_minus].x - n.x);
			yy = (double)(v[e[j].rndd_minus].y - n.y);
			z = (x*xx+y*yy)/Math.sqrt((x*x+y*y)*(xx*xx+yy*yy))+1.0;
			w += z*z*z*z;
		}
		for (int j = n.delta_minus; j>=0; j=e[j].delta_minus) {
			xx = (double)(v[e[j].rndd_plus].x - n.x);
			yy = (double)(v[e[j].rndd_plus].y - n.y);
			z = (x*xx+y*yy)/Math.sqrt((x*x+y*y)*(xx*xx+yy*yy))+1.0;
			w += z*z*z*z;
		}
		return w;
	}

	void init_sub() {
		int x[] = {1, 0, -1, 1, 0, -1};
		int y[] = {1, 1, 1, -1, -1, -1};
		int 	i,j,k;
		double	w,z;

		for (i=0; i<n; i++) {
			k=0;
			w=weight(v[i],(double)x[0],(double)y[0]);
			for (j=1; j<6; j++) {
				z=weight(v[i],(double)x[j],(double)y[j]);
				if (z<w) {
					w = z;
					k = j;
				}
			}
			v[i].dx = x[k];
			v[i].dy = y[k];
		}
	}

	public void init() {
		String mdname = getParameter("inputfile");
		try {
			InputStream is;

			is = new URL(getDocumentBase(),mdname).openStream();
			input_graph(is);
			try {
				if (is != null)
					is.close();
				} catch(Exception e) {
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found.");
		} catch (IOException e) {
			System.err.println("Cannot access file.");
		}

		String s = getParameter("start");
		if (s != null)
			snode = Integer.parseInt(s);
		else
			snode = 0;

		setBackground(Color.white);
		rdb();
		init_sub();
		addMouseListener(this);
	}

	public void paintNode(Graphics g, Node n, FontMetrics fm) {
		String s;
		int x = n.x;
		int y = n.y;
		int w = fm.stringWidth(n.name) + 10;
		int h = fm.getHeight() + 4;
		n.w = w;
		n.h = h;

		if (n.succ<-2)
			g.setColor(Color.blue);
		else if (n.succ==-2)
			g.setColor(Color.gray);
		else
			g.setColor(Color.red);

		g.drawRect(x-w/2,y-h/2,w,h);

		if (n.succ==-4)
			g.setColor(Color.cyan);
		else if (n.succ==-3)
			g.setColor(Color.pink);
		else if (n.succ>-2)
			g.setColor(Color.yellow);
		else
			g.setColor(getBackground());

		g.fillRect(x-w/2+1,y-h/2+1,w-1,h-1);

		g.setColor(Color.black);
		g.drawString(n.name,x-(w-10)/2,(y-(h-4)/2)+fm.getAscent());


		if (n.dist<0)
			s = "";
		else
			s = ""+n.dist;
		w = fm.stringWidth(s) + 10;
		x += (h+1)*n.dx; y += (h+1)*n.dy;
		g.setColor(getBackground());
		g.fillRect(x-n.pw/2,y-h/2,n.pw,h);
		n.pw = w;
		if (n.succ<-2)
			g.setColor(Color.blue);
		else
			g.setColor(Color.red);
		g.drawString(s,x-(w-10)/2,y-(h-4)/2+fm.getAscent());
	}

	int [] xy(int a, int b, int w, int h) {
		int	x[] = new int[2];

		if (Math.abs(w*b)>=Math.abs(h*a)) {
			x[0] = ((b>=0)?1:-1)*a*h/b/2;
			x[1] = ((b>=0)?1:-1)*h/2;
		} else {
			x[0] = ((a>=0)?1:-1)*w/2;
			x[1] = ((a>=0)?1:-1)*b*w/a/2;
		}
		return x;
	}

	void drawArrow(Graphics g,int x1,int y1,int x2,int y2) {
		int	a = x1-x2;
		int	b = y1-y2;

		if (isdigraph) {
		double	aa = Math.sqrt(a*a+b*b)/16.0;
		double	bb = b/aa;
			aa = a/aa;
		g.drawLine(x2,y2,x2+(int)((aa*12+bb*5)/13),y2+(int)((-aa*5+bb*12)/13));
		g.drawLine(x2,y2,x2+(int)((aa*12-bb*5)/13),y2+(int)((aa*5+bb*12)/13));
		}
		g.drawLine(x1,y1,x2,y2);
	}

	public void paintEdge(Graphics g, Edge e, FontMetrics fm) {
		Node v1 = v[e.rndd_plus];
		Node v2 = v[e.rndd_minus];

		int a = v1.x-v2.x;
		int b = v1.y-v2.y;

		int x1[] = xy(-a,-b,v1.w,v1.h);
		int x2[] = xy(a,b,v2.w,v2.h);

		if (v2.prev == e.rndd_plus) {
			if ((v1.succ<-2)&&(v2.succ>=-2))
				g.setColor(Color.red);
			else
				g.setColor(Color.blue);
		} else {
			g.setColor(Color.lightGray);
		}
		if ((!isdigraph)&&(v1.prev == e.rndd_minus)) {
			if ((v2.succ<-2)&&(v1.succ>=-2))
				g.setColor(Color.red);
			else
				g.setColor(Color.blue);
		}
		drawArrow(g,v1.x+x1[0],v1.y+x1[1],v2.x+x2[0],v2.y+x2[1]);

		int w = fm.stringWidth("" + e.len);
		int h = fm.getHeight();

		g.setColor(getBackground());
		g.fillRect((v1.x+v2.x-w)/2,(v1.y+v2.y-h)/2,w,h);

		if ((v2.prev == e.rndd_plus)||
		    ((!isdigraph)&&(v1.prev == e.rndd_minus)))
			g.setColor(Color.black);
		else
			g.setColor(Color.lightGray);
		g.drawString("" + e.len,(v1.x+v2.x-w)/2,(v1.y+v2.y-h)/2+fm.getAscent());
	}

	public void paint(Graphics g) {
		FontMetrics fm = g.getFontMetrics();
		for (int i=0; i<n; i++)
			paintNode(g,v[i],fm);
		for (int i=0; i<m; i++)
			paintEdge(g,e[i],fm);
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void mousePressed(MouseEvent ev) {
		if (iteration==0) {
			step1();
			iteration++;
			step = 2;
		} else if (iteration>=n) {
			step4();
			iteration = 0;
		} else {
			if (step == 2) {
				step2();
				step = 3;
			} else {
				step3();
				iteration++;
				step = 2;
			}
		}
		repaint();
	}
	public void mouseClicked(MouseEvent event) {}
	public void mouseReleased(MouseEvent event) {}
	public void mouseEntered(MouseEvent event) {}
	public void mouseExited(MouseEvent event) {}
}

