
import javax.media.jai.PlanarImage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.io.File;

public class ImageViewer extends JFrame
   implements ActionListener, MouseListener
{

   private Icon imageIcon;
   private JLabel picture;
   private JPanel jp;
   private Container container;

   public ImageViewer(File file)
   {
      super("Image Viewer");
      addMouseListener(this);
      container = getContentPane();
      jp = new JPanel();
      container.add(jp);

      PlanarImage image = JAIImageReader.readImage(file.getAbsolutePath());
      imageIcon = makeIcon(image);
      picture = new JLabel(imageIcon);      
      Dimension size = new Dimension(image.getWidth(),
                                       image.getHeight());
      picture.setPreferredSize(size);
      jp.add(picture);

      // Put the picture in a scroll pane  
      //JScrollPane pictureScrollPane =
      //      new JScrollPane(picture,
      //                     ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
      //                     ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

      //jp. add(pictureScrollPane);
      //setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        setSize(new Dimension(500,500));
        setVisible(true);
   }

   public Icon makeIcon(PlanarImage image) 
   {
        //float scale = 190.0F/Math.max(image.getWidth(),
        //                              image.getHeight());
        //ParameterBlock pb = new ParameterBlock();
        //pb.addSource(image);
        //pb.add(scale);
        //pb.add(scale);
        //pb.add(0.0F);
        //pb.add(0.0F);
        //pb.add(new InterpolationNearest());

        //PlanarImage scaled = JAI.create("scale", pb, null);
        //return new IconJAI(scaled);
        return new IconJAI(image);
    }

    public void actionPerformed(ActionEvent e)
    {
    }

    public void mousePressed(MouseEvent event) {
        System.out.println("Mouse at x: " + event.getX());
        System.out.println("Mouse at y: " + event.getY());
    }

    public void mouseClicked(MouseEvent event) {}
    public void mouseReleased(MouseEvent event) {}
    public void mouseEntered(MouseEvent event) {}
    public void mouseExited(MouseEvent event) {}

}

