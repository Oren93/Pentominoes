import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.*;
import javax.swing.plaf.FontUIResource;
import net.miginfocom.swing.MigLayout;
/**
 * This program is a user interface-based Board editor for Pentominoes.
 * It is a part of the Pento program along with Pento.java for finding solutions and PentoComponent.java
 * as a main program.
 * The editor is being called from the main class in PentoComponent.java and opens in it�s own window,
 * meaning that user can open it and still interact with the other window (non-modal). However, there is
 * a main class in this class as well meant for testing, not all the features work when starting directly
 * here (showing solutions in particular won�t work).
 * @author Oren
 */
public class BoardEditor extends JComponent/*JDialog*/ {
    static String [] board; // String represantation of the board, contains * and spaces
    static JFrame g; // The window
    JSpinner width; // for choosing width of board
    JSpinner height; // for choosing height of board
    static JLabel label; // for user interface instructions 
    static JPanel p1; // contains buttons and spinners 
    static JPanel p2; // contains canvas only
    static int numOfBlanks = 0; // count non-active cells for quick cell count analyze
    // viewport height and viewport width:
    static int vh = Toolkit.getDefaultToolkit().getScreenSize().height/100;
    static int vw = Toolkit.getDefaultToolkit().getScreenSize().width/100;
    static template canvas; // to draw board on
    static JButton ready; // Button to indicate board is ready to find solution
    static JTextField name; // Name of board
    static JLabel nameLbl;
    static JButton cancel;
    static boolean FirstMouseEvt = true; // Is it the first cell to paint within mousedrag?
    static int blanking = 0; // 0 = toggle, 1 = blanking only, -1 = activating only
    
    /**
    * BoardEditor constructor.
    * When called, it opens a new JFrame with the editor, and initialize all the component in it.
    */    
    public BoardEditor () {
        // MakeSolutions running time bad so board can't be large
        // Initial value for spinner = 5, lowest 1, max 12, jumps 1
        width = new JSpinner(new SpinnerNumberModel(5, 1, 12, 1));
        height = new JSpinner(new SpinnerNumberModel(5, 1, 12, 1));
        g = new JFrame("Board Editor");
        g.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        g.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                PentoComponent.g.setEnabled(true);
                g.dispose();
            }
        });
        g.setLayout(new BorderLayout(10,5));
        // initiallizing Spinners
        width.setEditor(new JSpinner.NumberEditor(width));
        height.setEditor(new JSpinner.NumberEditor(height));
        // Button to draw the board
        JButton enter = new JButton("draw board");
        enter.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    enter.setText("new board"); // info: it is possible to draw new board and override
                    createTemplate();
                    setboard();
                    //instructions are no longer needed on label, cell count is more usefull
                    label.setText("Board's active cells count: "+(board.length*board[0].length()));
                    ready.setEnabled(true);
                }
            });
        
        label = new JLabel("Choose board's dimenssions");
        p1 = new JPanel(new MigLayout ("wrap 5")); // 5 buttons per line);
        p2 = new JPanel(); // board's panel
        canvas = new template((int)width.getValue(),(int)height.getValue()); // draw WxH board template 
        setboard();
        p2.add(canvas);
        p1.add(label,"span 5");
        cancel = new JButton("cancel");
        name = new JTextField(8); // for naming the board
        nameLbl = new JLabel("  Board Name: ");
        cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    System.out.println("Terminated by user");
                    PentoComponent.g.setEnabled(true);
                    g.dispose(); // exits only current program, PentoComponent remains open
                }
            });
        ready = new JButton("Show Solutions!");
        ready.setEnabled(false);
        p1.add(new JLabel("Width: "));
        p1.add(width);
        p1.add(new JLabel("Height: "));
        p1.add(height);
        p1.add(enter);
        p1.add(cancel);
        p1.add(nameLbl);
        p1.add(name,"span 2");
        p1.add(ready);
        g.add(p1,BorderLayout.NORTH);
        setSize(500,500);
        ready.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent act) {
                readyHandler(); // find solutions or errors
            }
        });
        canvas.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) { // Faster respond than mouseClicked
                    super.mouseClicked(e);
                    setboard(e.getX(), e.getY()); // toggle cells on-off 
                }
                public void mouseReleased(MouseEvent e) {
                    blanking = 0; 
                    FirstMouseEvt = true; // finished mousedrag and initialize for next drag
                } 
                public void mouseClicked(MouseEvent e) {
                }
            });
        canvas.addMouseMotionListener(new MouseMotionListener() {
                public void mouseDragged(java.awt.event.MouseEvent e) {
                    setboard(e.getX(), e.getY()); // toggle cells on-off
                }
                public void mouseMoved(java.awt.event.MouseEvent e) {
                }
            });
        g.pack();
        g.setVisible(true);
    }

    /**
     * Handles what the program does when the button for showing solutions is clicked.
     * Firstly it checks that all conditions are filled: Name for the board i given and is unique,
     * cell count divisible by 5, solutions are found. If these conditions are not filled, a message
     * will pop up to let the user know what to fix, if solutions are found it notifies the user
     * how many solution are there and redirect the user back to the main window (closes the editor).
     * @param none
     * @return void
     */
    private static void readyHandler() {
        String [] Err_Warn = new String [] {
            "  There are no solutions for this Board  ",
            "Warning",
            "  Please choose another name, name is taken  ",
            "Error",
            "  Please Enter a name for your Template  ",
            "Error",
            "  Number of cells must be divisible by 5  "
        };
        if (!Is5Divisible()) { // faster check than sending to Pento.java and get 0 solutions 
        JOptionPane.showMessageDialog(g,Err_Warn[6],Err_Warn[5],JOptionPane.INFORMATION_MESSAGE);
            return;
        }
         // find solutions and initiallize the board, or get an error code number to handle
        int flag = PentoComponent.addTemplate(board, name.getText());
        if (flag<=0) { // error detected
            JOptionPane.showMessageDialog(g,
            Err_Warn[(-2)*flag],
            Err_Warn[(-2)*flag+1],
            JOptionPane.INFORMATION_MESSAGE);
            return;
        }   
        else {
            JOptionPane.showMessageDialog(g,
            "  The system found "+flag+" solutions to your board!",
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
        g.dispose();
        }
    }
    
    /**
     * When hitting the draw button, a board template will be shown according to the size determined by spinners.
     * Hitting again will override previous template without saving.
     */
    private void  createTemplate() {
        p2.removeAll(); // remove previous template
        canvas.size((int)width.getValue(),(int)height.getValue());
        p2.add(canvas);
        g.add(p2,BorderLayout.SOUTH);
        g.pack();
    }

    /**
     * Changing the 2D array size of the String representation of the board.
     * this board String will be sent to PentoComponent.java.
     */
    private void setboard() {
        numOfBlanks = 0;
        board = new String[(int)height.getValue()];
        String w = "";
        for (int i = 0 ; i<(int)width.getValue();i++)
            w = w+" ";
        for (int i = 0 ; i<(int)height.getValue();i++)
            board[i] = w;   
    }

    /**
     * Adding and removing blank spots onto/from the template, both visually and in the String
     * representation (Blank is *, it is where pentomino shouldn't be placed).
     * If mouse is dragged along the canvas, the first cell will determine whether to activate
     * or deactivate the cells.
     * @param x mouse horizontal location relative to the canvas
     * @param y mouse vertical location relative to the canvas
     * @return void
     */
    private void setboard(int x, int y) {
        int pixels = canvas.getPixels();
        if (y%pixels==0||x%pixels==0)
            return; // clicked on the border between two squares
        x/=pixels; // The X,Y location of the MouseClick in term of cell count
        y/=pixels;
        String boardPart = board[y];
        String newpart;
        if (board[y].charAt(x)=='*' && blanking==1 || board[y].charAt(x)==' ' && blanking==-1 )
            return; // don't activate cells when deactivation mode is on
                    // and don't deactivate cells when activation mode is on
        if (board[y].charAt(x)=='*') {
            newpart =boardPart.substring(0,x)+" "+boardPart.substring(x+1);
            numOfBlanks--;  }
        else {
            newpart =boardPart.substring(0,x)+"*"+boardPart.substring(x+1);
            numOfBlanks++; }
        label.setText("Board's active cells count: "+(board.length*board[0].length()-numOfBlanks));
        board[y]=newpart; 
        // finished to change the representation, now changeging visually
        canvas.toggle(x,y);
        if (FirstMouseEvt){
            if (board[y].charAt(x)=='*')
            blanking = 1;
            else blanking = -1;
            FirstMouseEvt = false;
        }
    }

    /**
     * Get the board as String, currently not in used
     */
    protected String [] getBoard() {
        return board;
    }
    
    /**
     * Before wasting time finding solutions we check if the board is divisible by 5
     * @return true if valid board, false otherwise
     */
    private static boolean Is5Divisible(){
        if ((board.length*board[0].length()-numOfBlanks)%5==0)
            return true;
        return false;
    }
    
    public static void main (String [] args) {
    Runnable evt;
    evt =   () -> {
    // Font size especially neccessary with NetBeans and 4k screen
    setFont(new FontUIResource(new Font("Arial", Font.PLAIN, 
    3*Toolkit.getDefaultToolkit().getScreenSize().height/100)));
    BoardEditor a = new BoardEditor();
    };
    java.awt.EventQueue.invokeLater(evt);
    }
    
    
    /**
    * Helper method to initialize the font for all the components at once
    * @param none
    * @return void
    */
    static public void setFont(FontUIResource myFont) {
        UIManager.put("CheckBoxMenuItem.acceleratorFont", myFont);
        UIManager.put("Button.font", myFont);
        UIManager.put("ToggleButton.font", myFont);
        UIManager.put("RadioButton.font", myFont);
        UIManager.put("CheckBox.font", myFont);
        UIManager.put("ColorChooser.font", myFont);
        UIManager.put("ComboBox.font", myFont);
        UIManager.put("Label.font", myFont);
        UIManager.put("List.font", myFont);
        UIManager.put("MenuBar.font", myFont);
        UIManager.put("Menu.acceleratorFont", myFont);
        UIManager.put("RadioButtonMenuItem.acceleratorFont", myFont);
        UIManager.put("MenuItem.acceleratorFont", myFont);
        UIManager.put("MenuItem.font", myFont);
        UIManager.put("RadioButtonMenuItem.font", myFont);
        UIManager.put("CheckBoxMenuItem.font", myFont);
        UIManager.put("OptionPane.buttonFont", myFont);
        UIManager.put("OptionPane.messageFont", myFont);
        UIManager.put("Menu.font", myFont);
        UIManager.put("PopupMenu.font", myFont);
        UIManager.put("OptionPane.font", myFont);
        UIManager.put("Panel.font", myFont);
        UIManager.put("ProgressBar.font", myFont);
        UIManager.put("ScrollPane.font", myFont);
        UIManager.put("Viewport.font", myFont);
        UIManager.put("TabbedPane.font", myFont);
        UIManager.put("Slider.font", myFont);
        UIManager.put("Table.font", myFont);
        UIManager.put("TableHeader.font", myFont);
        UIManager.put("TextField.font", myFont);
        UIManager.put("Spinner.font", myFont);
        UIManager.put("PasswordField.font", myFont);
        UIManager.put("TextArea.font", myFont);
        UIManager.put("TextPane.font", myFont);
    }
}
/**
 * A canvas class to draw the board
 */
class template extends Canvas  
{
    boolean [][] board; // boolean interpetation of the 'String [] Board' as above
    int w;  // width of board
    int h;  // height of board
    int pixels; // size of each cell
    /**
     * construct a board with specified height and width
     * @param width
     * @param height 
     */
    protected template(int width, int height) {
        board = new boolean [width][height];
        initialize(width, height);
        setBackground (Color.GRAY); 
        setSize(width*pixels,height*pixels);
    }  
    /**
     * Resize the canvas 
     * @param width
     * @param height 
     */
    protected void size(int width, int height) {
        board = new boolean [width][height];
        initialize(width, height);
        setSize(width*pixels,height*pixels);
        repaint();
    }
    /**
     * Getter for the cell size in pixels
     * @return int pixels per cell
     */
    protected int getPixels(){
        return pixels;
    }
    /**
     * initialize the value of w, h, pixels, according to the screen size and board size.
     * Makes sure the canvas is always visible without the need for scroll pane, by
     * calculating and the width and height and prioritize acocoding to the ration
     * whether the height determine the pixels size or width. that mean cells remain square regardles
     * to the ratio between width and height
     * @param width
     * @param height 
     */
    private void initialize (int width, int height) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int frameH = (int)(70*screenSize.height/100); // height won't exceed 70% of screen 
        int frameW = (int)(80*screenSize.width/100); // width won't exceed 80% of screen 
        double aspectRatio = (double)screenSize.width/screenSize.height;// usually 16:9 in modern screens
        w = width;
        h = height;
        if (width/height > aspectRatio) // if width is respectively larger, width determines cell size
            pixels = frameW/w;
        else pixels = frameH/h; // otherwise height determines
    }
    /**
     * Repaints the canvas when board is clicked, toggle "on" "off", also changing the boolean [] board
     * @param x
     * @param y 
     */
    protected void toggle (int x, int y) {
        if(board[x][y])
            board[x][y]=false;
        else board[x][y]=true;
        repaint();
    }

    /**
     * Default paint as inherited from canvas.
     * @param g Graphics
     * @return void
     */
    public void paint(Graphics g)  
    {   
        setBackground(Color.WHITE); // not really neccessary 
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if ((i+j)%2==1) // darkened colours for odd diagonals 
                    if (board[i][j]) // blak ("off") vs active ("on") cells colour
                        g.setColor(new Color(125, 50, 255));
                    else
                        g.setColor(new Color(35, 215, 160));
                else
                if (board[i][j]) // blak ("off") vs active ("on") cells colour
                    g.setColor(new Color(155, 50, 240));
                else
                    g.setColor(new Color(55, 235, 180));
                g.fillRect(pixels * i, pixels * j, pixels, pixels);
            }
        }  
    }  
}    