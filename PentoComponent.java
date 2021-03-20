import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.*;
import java.awt.print.*;
import java.io.File;
import java.nio.file.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.OrientationRequested;
import static javax.swing.JOptionPane.showMessageDialog;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FontUIResource;

/**
 *
 * @author Oren Raz
 * This program shows a visualized representation of pentominoes puzzle for various of board patterns.
 * Improvements from previous version include a Board Editor, save and load board and print 
 */
public class PentoComponent extends JPanel  {
    /* many static variables to be used, in order to avoid calling methods with many parameters
     and so allow to split big chunks of code to a few different methods with ease */
    static private int pixels;      // size of each cell on the board (in pixels) 
    static private String [] board; // The current board to be drawen
    static int currentSolution;     // Solutions are in array, that is the index for the current one
    static int currentTemplate;     // index of current board
    static final private int WINDOW_COVERAGE = 75; // 0% - 100%, 100=fullscreen
    static JFrame g = new JFrame("Pentominoes");
    static JPanel p = new JPanel();
    static String [][] solutions;   // solutions array to be used by few methods
    static String solution= ""; // temporarily holds the solutions as string before converting to array 
    static Path path;
    static File file;
    static PrinterJob job = PrinterJob.getPrinterJob();
    /**
     * constructor of an empty PentoComponent
     */
    public PentoComponent() {
        board = new String[] {""};
    }
    
    /**
     * constructor of an initialized PentoComponent
     * @param b the board as a string array
     */
    public PentoComponent(String [] b) {
        board = b;
    }
    
    /**
     * setting PentoComponent's new board
     * @param b the board as a string array
     * @return void
     */
    static public void setBoard (String [] b) {
        board = b;
    }
    
    /**
     * getting the table of the PentoComponent
     * @return String array of the board
     */
    public String [] getBoard () {
        return board;
    }

    /**
     * compute the size for the components.
     * @return Dimension
     */
    @Override 
    public Dimension getPreferredSize() {
        return (computeMinimumSize());
    }

    /**
     * Paints the board with it's solution or with the given pattern.
     * @param none (Default parameter Graphics g)
     * @return void
     */
   @Override
    public void paintComponent(Graphics g) {
        super.paintComponents(g);
        computeMinimumSize(); // initiallizing pixels size
        this.setBackground(Color.WHITE);
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length(); j++) {
                g.setColor(getcharColor(board[i].charAt(j)));
                g.fillRect(pixels * j, pixels * i, pixels, pixels);
            }
        }
    }

    /**
     * Decides the colour of each letter from the colours array
     * @param c the character
     * @return Colour of this particular character
     */
    private Color getcharColor(char c) {
    Color [] col = new Color [] // 13 color fylki
        {   Color.WHITE, // Blank 
            Color.ORANGE, // F
            Color.GREEN,  // I
            Color.BLUE,  // L
            Color.CYAN,  // P
            Color.MAGENTA,  // N
            new Color(15, 49, 92),
            Color.RED,  // U
            Color.YELLOW,  // V
            new Color(165, 20, 220),  // W
            new Color(99, 21, 55),  // X
            new Color(45, 175, 255),  // Y
            new Color(51, 102, 0)  // Z
        };
        String pento = " FILPNTUVWXYZ";  // space gets white
        for (int i = 0; i < pento.length(); i++) 
        if (c == pento.charAt(i))
            return col[i];
        return java.awt.Color.LIGHT_GRAY; // for '*' ( = blank cell)
    }

        /**
         * Computes the size of the panel with respect to the screen.
         * Also initializing the size (in pixels) of each cell in the board.
         * Can easily be modified to different sizes by changing the final variable 'WINDOW_COVERAGE' on
         * top of the class.
         * @param none
         * @return Dimensions for the panel
         */
    private static Dimension computeMinimumSize(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int frameH = (int)(WINDOW_COVERAGE*screenSize.height/100);
        int frameW = (int)(WINDOW_COVERAGE*screenSize.width/100);
        double aspectRatio = screenSize.width/screenSize.height;
        int w = 0;
        for (int i = 0; i < board.length; i++)
            if (w< board[i].length())
                w=board[i].length();
        if (w/board.length > aspectRatio)
            pixels = frameW/w;
        else pixels = frameH/board.length;
            
        return new Dimension((w)*pixels,(board.length)*pixels);
    }

        
        static String [][] emptyTemplates= new String [][]{ // just a few examples of boards
{"*         *"
, "   * * *   "
, " *       * "
, "   * * *   "
, " *       * "
, "   * * *   "
, "*         *"},

 { "           "
, "           "
, "           "
, "   ******* "
, "   ******* "
, "   ******* "
, "   ******* "
, "           "
},

{ "*  ***    *"
, "  ***      "
, "  ***      "
, "   ***     "
, "     ***   "
, "      ***  "
, "      ***  "
, "*    ***  *"
},

{ "***   ***"
, "***   ***"
, "***   ***"
, "         "
, "         "
, "         "
, "***   ***"
, "***   ***"
, "***   ***"
},
{
"     ",
"     ",
"*    ",
"     ",
"     ",
"    *",
"    *",
"     ",
"  ** ",
}};
static PentoComponent pentoC;
static JPanel buttonPanel;// the lower part of the window cpntains buttons
static JButton bNext;// arrow right, next solution available
static JButton bPrev;// arrow left, previous solution
static JLabel lb;
static String [] combo = new String[] { "Example 1", "Example 2", "Example 3", "Example 4", "Example 5" };
static JComboBox templates;
 public static void main (String [] args) { 
        // Font size especially neccessary with NetBeans and 4k screen  
        setFont(new FontUIResource(new Font("Arial", Font.PLAIN, 
            3*Toolkit.getDefaultToolkit().getScreenSize().height/100)));
        pentoC = new PentoComponent();    // empty constructor call
        currentSolution=0;  // the first value of solution array is the empty board
        currentTemplate = 0;    // can be change via JComboBox
        getSolutions ();
        buttonPanel = new JPanel();
        bNext = new JButton("    \u25BA    ");
        bPrev = new JButton("    \u25C4    ");
        lb = new JLabel();
        Runnable evt;
        templates = new JComboBox<>(combo);
        evt =   () -> {
            arrangeLayout();
                };
        java.awt.EventQueue.invokeLater(evt);

        // Initiallizing the listeners: 
        bNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                changeView(emptyTemplates[currentTemplate],1);
                pentoC.repaint();
                p.setSize(computeMinimumSize());
            }
        });
        bPrev.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                changeView(emptyTemplates[currentTemplate],-1);
                pentoC.repaint();
                p.setSize(computeMinimumSize());
            }
        });
    }// END MAIN END MAIN END MAIN END MAIN END MAIN END MAIN END MAIN
    
    /**
     * Changes the board as response to action performed on arrow buttons.
     * Skips over the empty board, shows only solutions, empty board can be seen after choosing a 
     * board using the JComboBox. This method makes sure array index does not go out
     * of bound, when reaches the end is starts over from solution number 1 again
     * @param String [] template (empty board)
     * @param i forward or backwards (-1 and 1)
     * @return void
     */
    static private void changeView (String [] template, int i){
        currentSolution+=i;
            if(currentSolution  >=  solutions.length) // if last solution, start over
                currentSolution=1;
            if (currentSolution  <=  0) // in index 0 is the empty board, don't show
                currentSolution = solutions.length-1;
            if (solutions.length == 1) currentSolution=0;
            setBoard(solutions[currentSolution]);
            lb.setText("   "+currentSolution + "/" + (solutions.length-1)+"   ");
    }

    /**
     * Helper method, managing the layout. takes no parameters, initializing the static
     * JComponents.
     * @return void
     */
    static public void arrangeLayout (){
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu(" File ");
        JMenuItem create = new JMenuItem("Board Editor");
        JMenuItem save = new JMenuItem("Save Board");
        JMenuItem open = new JMenuItem("Open Board");
        JMenuItem printBoard = new JMenuItem("Print board");
        JMenu help = new JMenu("Help");
        JMenuItem tutorial = new JMenuItem("Get Started");
        menu.add(create);
        menu.add(save);
        menu.add(open);
        menu.add(printBoard);
        menuBar.add(menu);
        help.add(tutorial);
        menuBar.add(help);
        create.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                BoardEditor editor = new BoardEditor();
                g.setEnabled(false);
 /* user can't use this frame while editor frame is running, to prevent
 open multiple editors and get errors*/
            }
        });
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                saveBoard();
            }
        });
        open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                OpenFromFile();
            }
        });
        printBoard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                printComponenet(p);
            }
        });
        tutorial.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                openTutorial();
            }
        });
        p.setSize(computeMinimumSize());
        p.add(pentoC);
        g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        g.setLayout(new BorderLayout());
        lb.setText("  "+combo[currentTemplate]+"  ");
        templates.setSelectedIndex(currentTemplate);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        buttonPanel.add(bPrev);
        buttonPanel.add(lb);
        buttonPanel.add(bNext);
        buttonPanel.add(templates);
        g.add(p);
        g.add(menuBar, BorderLayout.NORTH);
        g.add(buttonPanel, BorderLayout.SOUTH);
        g.pack();
        g.setVisible(true);
        templates.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JComboBox templates = (JComboBox) event.getSource();
                currentTemplate = templates.getSelectedIndex();
                currentSolution = 0;
                pentoC.setBoard(emptyTemplates[currentTemplate]);
                getSolutions();
                arrangeLayout();
            }
        });
    }

    /**
     * This method sends the template to Pento.java to get solutions and set it into array.
     * @param none
     * @return void
     */
    private static void getSolutions (){
        // solution string holds the template and the solutions as printed by Pento.java
        solution = String.join(",", emptyTemplates[currentTemplate]) + "-"; 
            for( String[] b: Pento.makeSolutions(emptyTemplates[currentTemplate]) )
        {   // in Pento.java it is printed in Stdout, here it is saved in String
            for( String s: b ) solution+=(s+",");
            solution+="-";
        }   // converting the solutions String into 2D array, every "column" is a solution, "row" is one line of the solution
        String[] solutionArr = solution.split("-");
        int numOfSolutions = solutionArr.length;
        solutions = new String [numOfSolutions][];
        for (int i = 0; i < numOfSolutions; i++)
            solutions[i] = solutionArr[i].split(",");
        pentoC.setBoard(solutions[0]);
    }
       
    /**
     * The following method handles the new board and sends it to MakeSolutions method.
     * It first checks few valid parameters, and gives a error code number back to the BoardEditor.
     */
    public static final int NAME_TAKEN = -1; // Error codes
    public static final int  NO_NAME= -2; // Error codes
    protected static int addTemplate(String [] Bo, String BName) {
        // Throws away leading spaces in name and check for non-empty
        while (BName.length()!=0 && BName.charAt(0)==' ')
            BName =BName.substring(1,BName.length());
        if (BName.length()==0)
            return NO_NAME; // Name is empty
        // checks if name of template is unique
        for (String n : combo)
            if (n.equals(BName))
                    return NAME_TAKEN;
        // Chacks how many solutions are there, if there are any
        String SolutionsArray = ""; 
        for( String[] b: Pento.makeSolutions(Bo) )
        {   // in Pento.java it is printed in Stdout, here it is saved in String
            for( String s: b ) SolutionsArray+=(s+",");
            SolutionsArray+="-";
        }
        int solutionsCount = -1; // Check how many solutions exist
        for (int i=0;i<SolutionsArray.length();i++)
            if(SolutionsArray.charAt(i)=='-') solutionsCount++;  
        if (solutionsCount<=0)
             return 0; // no solutions for this patern
// From here starts the code that handles the main window in case the board is valid, and solutions exist
        g.setEnabled(true);
        currentTemplate = emptyTemplates.length;
        String [] TNames = new String [currentTemplate+1];
        String [][] templateAdding = new String [currentTemplate+1][];
        for (int i = 0; i<emptyTemplates.length;i++) {
            TNames[i] = combo[i];
            templateAdding[i] = emptyTemplates[i];
        }
        templateAdding[currentTemplate] = Bo; // Adds board to end of list
        TNames[currentTemplate] = BName;    // add boards name to end of comboBox
        emptyTemplates = templateAdding;
        combo = TNames;
        buttonPanel.remove(templates);
        templates = new JComboBox<>(combo);
        currentSolution = 0;
        pentoC.setBoard(emptyTemplates[currentTemplate]);
        getSolutions();
        arrangeLayout();
        return solutionsCount+1;
    }
    
    /**
     * Method to save the board.
     * @param none
     * @return void
     */
    static private void saveBoard() {
        if (currentTemplate < 5) { // to be changed to 5 later
         JOptionPane.showMessageDialog(g,"  Cannot save a deafult board template.   \n"+
                 " It will be always loaded automatically ",
                 "Error",JOptionPane.INFORMATION_MESSAGE);
            return; 
        }
        int vw = Toolkit.getDefaultToolkit().getScreenSize().width/100;
        int vh = Toolkit.getDefaultToolkit().getScreenSize().height/100;
        try{
            JFileChooser fc = new JFileChooser();
            fc.setPreferredSize(new Dimension(35*vw, 35*vh));
            fc.setCurrentDirectory(new File("user.home"));
            int returnVal = fc.showSaveDialog(g);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fc.getSelectedFile();
                if(file.getName().contains(".pnt")) // Checks for valid pento "pnt" file type
                    path = Paths.get(file.getPath());
                else
                    path = Paths.get(file.getPath()+".pnt");
                
                String toSave = combo[currentTemplate]+"\\"+
                        String.join(",", emptyTemplates[currentTemplate]) + "-";
                Files.write(path,toSave.getBytes("UTF-8"));
        }        
    }   catch(Exception e){
            showMessageDialog(null, "Error: Please verify the path of your file.");
        }
    }
    
    /**
     * Open a board from pnt file.
     * @param none
     * @return void
     */
    static private void OpenFromFile() {
        int vw = Toolkit.getDefaultToolkit().getScreenSize().width/100;
        int vh = Toolkit.getDefaultToolkit().getScreenSize().height/100;
        if (path != null)
            path = path.getParent();
        String filetext;    
        int dialogResult = -1;                       
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setPreferredSize(new Dimension(35*vw, 35*vh));
        // Next line suppose to make filechooser show only relevant files but it doesn't work
fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("*.pnt", "pnt"));
        if (path == null)      
        fileChooser.setCurrentDirectory(new File("user.home"));
        else
        fileChooser.setCurrentDirectory(new File(path.toString()));         
        int result = fileChooser.showOpenDialog(g);
        if (result != JFileChooser.APPROVE_OPTION)
            return;
        File selectedFile = fileChooser.getSelectedFile();
        path = Paths.get(selectedFile.getAbsolutePath());
        
        try
        {   // File is encoded into "*.pnt" and needed to be read by pentoComponent properly
            filetext = new String(Files.readAllBytes(path),"UTF-8"); // loads content
            String [] b = filetext.substring(filetext.indexOf("\\")+1, // board
                            filetext.length()-1).split(",");
            String name = filetext.substring(0, filetext.indexOf("\\"));
            int flag = addTemplate(b,name);
            if (flag == NAME_TAKEN) {
                JOptionPane.showMessageDialog(g,"   Board \""+name+"\" from file \""+
                        selectedFile.getName()+"\"\n is already loaded onto the board list   ",
                        "Alert",JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch(Exception e){
            showMessageDialog(null, "Error: Please verify the path of your file.");
        }
        
    }
     
    /**
     * Method to print the current panel.
     * Can be printed to PDF for testing. The method calculates the scaling needed
     * in order to fit the board in the the page.
     * @param component to be printed
     * @return void
     */
    static public void printComponenet(Component component){
        int margin = 50;
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setJobName("Print Board");

        pj.setPrintable(new Printable()
        {
            @Override
            public int print(Graphics pg, PageFormat pf, int pageNum)
            {
                if(pageNum > 0)
                    return Printable.NO_SUCH_PAGE;
                Graphics2D g2 = (Graphics2D)pg;
                // setting x and y margin
                g2.translate(pf.getImageableX()+margin, pf.getImageableY()+1.5*margin);
                double scaling; // for fitting in the page
                double pageW = pf.getWidth()-2*margin; // destination width
                double pageH = pf.getHeight()-3*margin; // destination height
                // Checking whether scaling to be determined by height or width:
                if ((double)component.getHeight()/component.getWidth()>pageH/pageW)
                // scaling determined by height (applies very sledom, component height needs to 
                // be significantially lareger than it's width, 3x12 board for example).
                    scaling = pageH/component.getHeight();
                else // scaling determined by width (most likely to happen)
                    scaling = pageW/component.getWidth();
                g2.scale(scaling,scaling);
                component.paintAll(g2);
                return Printable.PAGE_EXISTS;
            }
        });
        if(pj.printDialog() == false)
            return;
        try
        {
            pj.print();
        }
        catch(PrinterException xcp)
        {
            xcp.printStackTrace(System.err);
        }
    }
    
    /**
     * This method opens a dialog frame, with 4 pages tutorial on how to use this program.
     * It is called by the "Get started" button located under "Help" in the menubar.
     * Since it is only informative, the JDialog does not need to return any value.
     * @param none
     * @return void
     */
    public static void openTutorial() {
        String [] text = {"", // info text to be shown for the user 
            "Welcome to PentoComponent!\n\n"
            + "This programm will help you to solve your Penominoe puzzle and to design more "
            + "boards for pentominoes in a few very simple steps.\n"
            + "There are few boards available as an example, choose a board below and scroll through it's"
            + " solutions, using the left and right arrows.\n\n"
            + "To open the board editor tool, click on \"File\" at the left of the menu bar,"
            + " then click \"Board Editor\".",
            "Board editor. Build your own!\n\n"
            + "Thanks to our latest editor tool you can build your board in just a few very simple steps:"
            + "\n1. Choose a board width and height and click \"Draw Board\".\n"
            + "2. Board will appear on your screen in the size you specified. Boards initial colour is "
            + "green, meaning a pentomino should be place there, by clicking on the cells you can change "
            + " the colour to purple, for indicating a blank cell.\n"
            + "3. Give a name to your board to appear on the list of boards\n"
            + "4. Click on \"Find Solutions!\" button\n\n"
            + "Note:\n"
            + "Board name must be unique.\n"
            + "Boards active cell count must be divisble by 5, the number of active cell appears in the"
            + " editor and refreshes after every activating or deactivating.\n"
            + "After choosing a board size and draw it, you can still change the size and draw a new board "
            + "without saving the previous board's setting.\n"
            + "Only if all goes well you will get the solutions to your board.",
            "Save your board.\n\n"
            + "To save your board click on \"File\" and then \"Save Board\".\n"
            + "Choose the folder you want to save it and the name of the file, it does not have "
            + "to be the same name as the board. No need to specify file extension.\n\n"
            + "Note the that save operation saves the current board loaded onto the screen by default."
            + "Your can't save the example boards, but no need, they will appear again on startup.",
            "Open board from file.\n\n"
            + "Click \"File\" -> \"Open Board\" the choose the navigate to the folder you saved"
            + " your boards and choose a file with *.pnt extension.\n"
            + "After opening the file, the board will automatically be loaded to your screen, and "
            + "you can review the solutions. You can open multiple files, all the boards will be "
            + "added to the list of boards.",
            "Print solutions.\n\n"
            + "The last option under \"File\" in the menu bar.\n"
            + "After clicking \"Print\" choose a printer (Can also print to pdf file) and click OK.\n"
            + "Note that it prints the current solution only, what you see on the screen will be"
            + " printed, without the text."
        };
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JDialog info = new JDialog(g,"Pento tutorial 1/4"); // The dialog window
        info.setVisible(true);
        JTextArea content = new JTextArea(); 
        content.setSize(new Dimension(70*screenSize.width/100, 70*screenSize.height/100));
        JPanel dialogContent = new JPanel();
        JPanel buttons = new JPanel();
        JButton left = new JButton("Back");  // inactive when on first page
        JButton right = new JButton("Next"); // Either "next" or "finish"
        dialogContent.add(content);
        content.setText(text[1]);
        content.setWrapStyleWord(true); // wrap the text 
        content.setLineWrap(true);      
        int vw = 2*screenSize.width/100; // for margin reltive to screen size
        content.setMargin( new Insets(vw,vw,vw,vw)); // more readable with margin
        buttons.add(left);  // disabled when page is 1/4
        buttons.add(right); 
        info.setLayout(new BorderLayout());
        info.add(dialogContent, BorderLayout.CENTER);
        info.add(buttons, BorderLayout.SOUTH);
        content.setBackground(info.getBackground());
        info.pack();
        info.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        g.setEnabled(false); // Modal didn't work, so I do it like that
        left.setEnabled(false);
        left.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
               right.setText("Next");
               String p = info.getTitle();
               int page = Integer.parseInt(p.substring(p.indexOf("/")-1,p.indexOf("/"))); // current page
               page--;
               info.setTitle("Pento tutorial "+page+"/4");
               content.setText(text[page]);
               if (page==1)
                   left.setEnabled(false);
               info.pack();
            }
        });
        right.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
               left.setEnabled(true); 
               String p = info.getTitle();
               int page = Integer.parseInt(p.substring(p.indexOf("/")-1,p.indexOf("/")));
               if (page==4) {
                   g.setEnabled(true);
                   info.dispose();
               }
               page++;
               info.setTitle("Pento tutorial "+page+"/4");
               content.setText(text[page]);
               if (page==4)
                   right.setText("Finish");
               info.pack();
            }
        });
        info.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent event) {
            g.setEnabled(true);
            info.dispose();
        }
    });
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
