import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class EliSpot extends JFrame
   implements ActionListener
{
   private JPanel jp;
   private Color bgcolor = new Color(153,159,199);
   private JMenuBar mbar;
   private JMenu mEliFunctions;  
   private JMenuItem CountSpots;    
   private File[] files;
   private JFileChooser fileChooser;
   private Container container;
   
   public EliSpot()
   {  
      super("EliSpot");
      setGUI();   
   }
   
   private void setGUI()
   {
      jp = new JPanel();
      container = getContentPane();
      container.add(jp);
      jp.setBackground(bgcolor);
      repaint();          
      
      //create and add the menubar
      mbar = new JMenuBar();
      setJMenuBar(mbar);
      mEliFunctions = new JMenu("Eli Functions");
      mbar.add(mEliFunctions);
      
      //add menu items
       JMenuItem CountSpots = new JMenuItem("Count Spots");
       mEliFunctions.addSeparator();
       mEliFunctions.add(CountSpots);
       
       //add ActionListeners to menu items      
       CountSpots.addActionListener(new ActionListener()
       {
         public void actionPerformed(ActionEvent e)
         {
            // Create the file chooser
            if (files == null && fileChooser==null)
            {
               // Create file chooser pointing to the home directory
               fileChooser = new JFileChooser();                        
            }
            else if(files!=null && fileChooser == null)
            {
               // Create file chooser pointing to the specified file path
               fileChooser = new JFileChooser(files[0]);
            }
            else if(files != null && files.length > 0)
            {
               fileChooser.setSelectedFile(files[0]);
               fileChooser.setVisible(true);
            }
            else
            {
               fileChooser.setVisible(true);
            }

            fileChooser.setMultiSelectionEnabled(true);

            // Show the file chooser dialog box
            int selected = fileChooser.showOpenDialog(container);
     
            if (selected == JFileChooser.APPROVE_OPTION)
            {
               // Get the selected file
               files = fileChooser.getSelectedFiles();
               //System.out.println("File name:" + files.getName());

               //for(int i=0; i<files.length; i++)
               //{
                   //System.out.println(files[i]);
               //}
               new SpotCounter(files);


               return;
            }
            else if (selected == fileChooser.CANCEL_OPTION)
            {
               return;
            }
          }   
       });
      
      setSize(new Dimension(500,200));
      setVisible(true);     
   }

   
   public void actionPerformed(ActionEvent e)
   {
   }
     
   public static void main(String[] args)
   {
      new EliSpot();
   }
}



