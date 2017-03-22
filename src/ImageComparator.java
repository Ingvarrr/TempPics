import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by vig on 3/21/17.
 */
public class ImageComparator {
    protected BufferedImage startPic = null;
    protected BufferedImage modifiedPic = null;
    protected String startPicFileName = null;
    protected String modifiedPicFileName = null;
    protected ArrayList<Point> listOfDiffPixels = new ArrayList<>();
    protected int diffPixelsNumber = 0;
    protected double percentageOfDiffPixels = 0;
    protected int horizontalQuartersQuantity = 0;
    protected int verticalQuarterQuantity = 0;
    protected int quarterX = 0;
    protected int quarterY = 0;

    public static void main(String[] args) {

        //Creating an object of this class(I supposed that we might have to create a lot of this objects)
        ImageComparator imageComparator = new ImageComparator();
        //Setting up properties for Comparator
        imageComparator.setProperties();
        //comparing images and marking differences with red rectangles
        imageComparator.imageCompare();
        //saving modified image with differences marked with red rectangles
        imageComparator.saveModifiedImage();
    }

    public ImageComparator() {
    }

    //for properties of compare setup
    public void setProperties() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            //setting up filenames
            System.out.println("Input name of the first image from \"images\" directory");
            startPicFileName = bufferedReader.readLine();
            if (!(new File("images/" + startPicFileName).exists())) {
                System.out.println("File not Found");
            }
            System.out.println("Input name of the second image from \"images\" directory");
            modifiedPicFileName = bufferedReader.readLine();
            if (!(new File("images/" + modifiedPicFileName).exists())) {
                System.out.println("File not Found");
            }
            //loading images into buffer for further processing
            loadImagesIntoBuffer();

            //setting up quarters(rectangles) for marking differences
            System.out.println("Choose setting for marking differences:");
            System.out.println("Type \"1\" if you want to set quantity of horizontal and vertical quarters" +
                    "\nType \"2\" if you want to set size of quarters in pixels");
            String settingForQuarters = bufferedReader.readLine();

            switch (settingForQuarters) {
                case "1":
                    System.out.println("Type quantity of horizontal quarters:");
                    horizontalQuartersQuantity = Integer.parseInt(bufferedReader.readLine());
                    System.out.println("Type quantity of vertical quarters:");
                    verticalQuarterQuantity = Integer.parseInt(bufferedReader.readLine());
                    calculateQuarterSize();
                    break;
                case "2":
                    System.out.println("Type size of horizontal quarters in pixels:");
                    quarterX = Integer.parseInt(bufferedReader.readLine());
                    System.out.println("Type size of vertical quarters in pixels:");
                    quarterY = Integer.parseInt(bufferedReader.readLine());
                    calculateQuarterCount();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //image comparing algorithm
    public void imageCompare() {
        if (startPic.getHeight() == modifiedPic.getHeight() &&
                startPic.getWidth() == modifiedPic.getWidth()) {
            for (int i = 0; i < startPic.getWidth(); i++) {
                for (int j = 0; j < startPic.getHeight(); j++) {
                    Color startPicPixelColor = new Color(startPic.getRGB(i, j));
                    Color modPicPixelColor = new Color(modifiedPic.getRGB(i, j));
                    int difference = 0;
                    difference += Math.abs(startPicPixelColor.getRed() - modPicPixelColor.getRed());
                    difference += Math.abs(startPicPixelColor.getGreen() - modPicPixelColor.getGreen());
                    difference += Math.abs(startPicPixelColor.getBlue() - modPicPixelColor.getBlue());
                    difference += Math.abs(startPicPixelColor.getAlpha() - modPicPixelColor.getAlpha());
                    //more than 10% difference in pixels checkout
                    if (((double) difference * 100) / 1020 > 10.0) {
                        listOfDiffPixels.add(new Point(i, j));
                    }
                }
            }
            countPercentageOfDiffPixels();
            markDifferencesWithRedRectangles();
        } else {
            System.out.println("Images have different sizes");
        }
    }

    //Marking differences with red rectangles(made half transparent for comfort reading
    // or opportunity to shade differences)
    protected void markDifferencesWithRedRectangles() {
        for (int i = 0; i < horizontalQuartersQuantity; i++) {
            for (int j = 0; j < verticalQuarterQuantity; j++) {
                BufferedImage startPicQuarter = startPic.getSubimage(
                        i * quarterX, j * quarterY, quarterX - 1, quarterY - 1);
                BufferedImage modifiedPicQuarter = modifiedPic.getSubimage(
                        i * quarterX, j * quarterY, quarterX - 1, quarterY - 1);
                int difference = 0;
                for (int k = 0; k < quarterX - 1; k++) {
                    for (int l = 0; l < quarterY - 1; l++) {
                        Color startPicQuarterPixelColor = new Color(startPicQuarter.getRGB(k, l));
                        Color modPicQuarterPixelColor = new Color(modifiedPicQuarter.getRGB(k, l));
                        difference += Math.abs(startPicQuarterPixelColor.getRed()
                                - modPicQuarterPixelColor.getRed());
                        difference += Math.abs(startPicQuarterPixelColor.getGreen()
                                - modPicQuarterPixelColor.getGreen());
                        difference += Math.abs(startPicQuarterPixelColor.getBlue()
                                - modPicQuarterPixelColor.getBlue());
                        difference += Math.abs(startPicQuarterPixelColor.getAlpha()
                                - modPicQuarterPixelColor.getAlpha());
                    }
                }
                if ((double) difference * 100 / 1020 > percentageOfDiffPixels) {
                    Graphics2D redRectangle = modifiedPic.createGraphics();
                    redRectangle.setColor(new Color(255, 0, 0, 128));
                    redRectangle.drawRect(i * quarterX, j * quarterY, quarterX - 1, quarterY - 1);
                }
            }
        }
    }

    protected void countPercentageOfDiffPixels() {
        diffPixelsNumber = listOfDiffPixels.size();
        percentageOfDiffPixels = ((double) diffPixelsNumber * 100) /
                (startPic.getWidth() * startPic.getHeight());
    }

    //quarter size calculation
    protected void calculateQuarterSize(){
        quarterX = (startPic.getWidth()/horizontalQuartersQuantity);
        quarterY = (startPic.getHeight()/verticalQuarterQuantity);
    }

    //quarter count calculation
    protected void calculateQuarterCount(){
        horizontalQuartersQuantity = (startPic.getWidth() / quarterX);
        verticalQuarterQuantity = (startPic.getHeight() / quarterY);
    }

    //loading images into Buffer (can make static for speed optimization if is necessary)
    protected void loadImagesIntoBuffer() {
        try {
            startPic = ImageIO.read(new File
                    ("images/" + startPicFileName));
            modifiedPic = ImageIO.read(new File
                    ("images/" + modifiedPicFileName));
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //saving modified image to "images" directory
    protected void saveModifiedImage() {
        try {
            ImageIO.write(modifiedPic, "png", new File("images/imageDifferences.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
