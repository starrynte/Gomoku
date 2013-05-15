import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;


public class Game extends JFrame implements ActionListener, MouseListener, MouseMotionListener {
    private int[][] board; //[0][0] is top left; -1 = black, 0 = empty, 1 = white
    private int player; //-1, 1 same as above
    Board boardObj;
    SidePane side;
    int winner; //-1, 1 same as above; 2 = draw; 0 = uninitialized
    List<int[]> history; //for undo
    int mouseRow, mouseCol;

    public Game()
    {
        super("Gomoku");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().add(boardObj = new Board(), BorderLayout.CENTER);
        getContentPane().add(side = new SidePane(), BorderLayout.LINE_END);
        getContentPane().setBackground(new Color(255, 159, 63));
        pack();
        setVisible(true);
        
        winner = 0;
        history = new ArrayList<int[]>();
        mouseRow = -1;
        mouseCol = -1;
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("undo"))
		{
			if(history.size() > 0)
			{
				int[] point = history.remove(0);
				board[point[0]][point[1]] = 0;
				winner = 0;
	            side.setWinner("", Color.GREEN);
	            player = -player;
	            side.setPlayer(player);
	            boardObj.repaint();
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e)
    {
        if(winner == 0 && e.getButton() == MouseEvent.BUTTON1)
        {
            int cell = Math.min(boardObj.getWidth(), boardObj.getHeight()) / 20;
            int row = (e.getY() - cell / 2) / cell, col = (e.getX() - cell / 2) / cell;
            if(board[row][col] == 0)
            {
                board[row][col] = player;
                history.add(0, new int[]{ row, col });
                checkGameOver(row, col, player);
                switch(winner)
                {
                    case -1: side.setWinner("Player 1 wins!", Color.BLACK); break;
                    case 1: side.setWinner("Player 2 wins!", Color.WHITE); break;
                    case 2: side.setWinner("Draw!", Color.GREEN); break;
                }
                player = -player;
                side.setPlayer(player);
                boardObj.repaint();
            }
        }
    }
	@Override public void mouseEntered(MouseEvent e){ }
	@Override public void mouseExited(MouseEvent e){ }
	@Override public void mousePressed(MouseEvent e){ }
	@Override public void mouseReleased(MouseEvent e){ }

	@Override public void mouseDragged(MouseEvent e) { }
	@Override
	public void mouseMoved(MouseEvent e) {
		int cell = Math.min(boardObj.getWidth(), boardObj.getHeight()) / 20;
        int row = (e.getY() - cell / 2) / cell, col = (e.getX() - cell / 2) / cell;
        if(board[row][col] == 0)
        {
            mouseRow = row;
            mouseCol = col;
        }else
        {
        	mouseRow = -1;
        	mouseCol = -1;
        }
        boardObj.repaint();
	}
    
    private void checkGameOver(int row, int col, int player)
    {
        //horizontal
        int run = 0;
        for(int c = 0; c < 19; c++)
        {
            if(board[row][c] == player){
                run++;
            }else
            {
                if(run == 5){ winner = player; return; }
                run = 0;
            }
        }
        if(run == 5){ winner = player; return; }
        
        //vertical
        run = 0;
        for(int r = 0; r < 19; r++)
        {
            if(board[r][col] == player){
                run++;
            }else
            {
                if(run == 5){ winner = player; return; }
                run = 0;
            }
        }
        if(run == 5){ winner = player; return; }
        
        //positive-slope diagonal
        run = 0;
        for(int r = Math.min(row + col, 18), temp = row + col - r, c = temp; r >= temp; r--, c++)
        {
            if(board[r][c] == player){
                run++;
            }else
            {
                if(run == 5){ winner = player; return; }
                run = 0;
            }
        }
        if(run == 5){ winner = player; return; }
        
        //negative-slope diagonal
        run = 0;
        for(int r = Math.max(row - col, 0), c = Math.max(col - row, 0); Math.max(r, c) < 19; r++, c++)
        {
            if(board[r][c] == player){
                run++;
            }else
            {
                if(run == 5){ winner = player; return; }
                run = 0;
            }
        }
        if(run == 5){ winner = player; return; }
        
    }
	
    class Board extends JPanel
    {
        
        public Board()
        {
            super();
            setOpaque(false);
            setPreferredSize(new Dimension(640, 640));
            
            addMouseListener(Game.this);
            
            board = new int[19][19];
            player = -1;
        }
        
        protected void paintComponent(Graphics g)
        {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            super.paintComponent(g);
            
            int cell = Math.min(getWidth(), getHeight()) / 20;
            if(cell == 0) return; //to avoid infinite loop
            for(int x = cell; x <= (19 * cell); x += cell)
            {
                g.drawLine(x, cell, x, 19 * cell);
            }
            for(int y = cell; y <= (19 * cell); y += cell)
            {
                g.drawLine(cell, y, 19 * cell, y);
            }
            
            for(int row = 0; row < 19; row++)
            {
                for(int col = 0; col < 19; col++)
                {
                    if(board[row][col] == -1) g.setColor(Color.BLACK);
                    else if(board[row][col] == 1) g.setColor(Color.WHITE);
                    else continue;
                    g.fillOval(col * cell + cell / 2 + 1, row * cell + cell / 2 + 1, cell - 2, cell - 2);
                }
            }
        }
        
    }
    
    class SidePane extends JPanel
    {
        private final Border border = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED, 2), BorderFactory.createEmptyBorder(4, 4, 4, 4)),
                empty = BorderFactory.createEmptyBorder(6, 6, 6, 6);
        private JLabel player1, player2, winner;
        
        public SidePane()
        {
            super();
            setOpaque(false);
            setPreferredSize(new Dimension(100, 640));
            
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            player1 = new JLabel("Player 1");
            player1.setForeground(Color.BLACK);
            player1.setBorder(border);
            add(player1);
            player2 = new JLabel("Player 2");
            player2.setForeground(Color.WHITE);
            player2.setBorder(empty);
            add(player2);
            JButton button = new JButton("Undo");
            button.addActionListener(Game.this);
            button.setActionCommand("undo");
            add(button);
            winner = new JLabel();
            winner.setBorder(empty);
            add(winner);
        }
        
        void setPlayer(int newPlayer)
        {
            if(newPlayer == -1)
            {
                player2.setBorder(empty);
                player1.setBorder(border);
            }else
            {
                player1.setBorder(empty);
                player2.setBorder(border);
            }
        }
        
        void setWinner(String text, Color color)
        {
            winner.setForeground(color);
            winner.setText(text);
        }
        
    }
    
}
