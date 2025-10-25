import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * SlideShow.java
 *
 * A Java Swing application that displays a slideshow of top 5 destinations.
 * Navigation is controlled using "Previous" and "Next" buttons, with wrap-around cycling between slides.
 */
public class SlideShow extends JFrame {

    private static final long serialVersionUID = 1L; // Prevents serialization warning

    // Declare Variables
    private JPanel slidePane;
    private JPanel textPane;
    private JPanel buttonPane;
    private CardLayout card;
    private CardLayout cardText;
    private JButton btnPrev;
    private JButton btnNext;
    private JLabel lblSlide;
    private JLabel lblTextArea;
    private int currentSlideIndex = 1; // Track the current slide index

    /**
     * Create the application.
     */
    public SlideShow() throws HeadlessException {
        initComponent();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initComponent() {
        // Initialize variables
        card = new CardLayout();
        cardText = new CardLayout();
        slidePane = new JPanel();
        textPane = new JPanel();
        buttonPane = new JPanel();
        btnPrev = new JButton("Previous");
        btnNext = new JButton("Next");
        lblSlide = new JLabel();
        lblTextArea = new JLabel();

        // Setup frame attributes
        setSize(800, 600);
        setLocationRelativeTo(null);
        setTitle("Top 5 Destinations SlideShow");
        getContentPane().setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Setting layouts for panels
        slidePane.setLayout(card);
        textPane.setLayout(cardText);
        textPane.setBackground(getBackgroundColor(currentSlideIndex)); // Set initial color

        // Add slides and descriptions
        for (int i = 1; i <= 5; i++) {
            lblSlide = new JLabel(getResizeIcon(i), JLabel.CENTER);
            lblTextArea = new JLabel(getTextDescription(i), JLabel.CENTER);
            slidePane.add(lblSlide, "card" + i);
            textPane.add(lblTextArea, "cardText" + i);
        }

        // Add panels to the frame
        getContentPane().add(slidePane, BorderLayout.CENTER);
        getContentPane().add(textPane, BorderLayout.SOUTH);

        // Button pane setup
        buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPrev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goPrevious();
            }
        });
        btnNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goNext();
            }
        });

        buttonPane.add(btnPrev);
        buttonPane.add(btnNext);
        getContentPane().add(buttonPane, BorderLayout.NORTH);
    }

    /**
     * Previous Button Functionality
     */
    private void goPrevious() {
        card.previous(slidePane);
        cardText.previous(textPane);
        currentSlideIndex = (currentSlideIndex - 1 < 1) ? 5 : currentSlideIndex - 1; // Cycle backwards
        textPane.setBackground(getBackgroundColor(currentSlideIndex)); // Update background color
    }

    /**
     * Next Button Functionality
     */
    private void goNext() {
        card.next(slidePane);
        cardText.next(textPane);
        currentSlideIndex = (currentSlideIndex % 5) + 1; // Cycle through slides (1 to 5)
        textPane.setBackground(getBackgroundColor(currentSlideIndex)); // Update background color
    }

    /**
     * Method to get the images
     */
    private String getResizeIcon(int i) {
        String image = "";
        if (i == 1) {
            image = "<html><body><img width='800' height='500' src='" + getClass().getResource("/resources/Tokyo.jpg") + "'></body></html>";
        } else if (i == 2) {
            image = "<html><body><img width='800' height='500' src='" + getClass().getResource("/resources/WDW.jpg") + "'></body></html>";
        } else if (i == 3) {
            image = "<html><body><img width='800' height='500' src='" + getClass().getResource("/resources/WashingtonDC.jpg") + "'></body></html>";
        } else if (i == 4) {
            image = "<html><body><img width='800' height='500' src='" + getClass().getResource("/resources/Paris.jpg") + "'></body></html>";
        } else if (i == 5) {
            image = "<html><body><img width='800' height='500' src='" + getClass().getResource("/resources/Egypt.jpg") + "'></body></html>";
        }
        return image;
    }

    /**
     * Method to get the text values
     */
    private String getTextDescription(int i) {
        String text = "";
        if (i == 1) {
            text = "<html><body><font size='5'>#1 Tokyo</font> <br>Tokyo is Japan's busiest city and provides an incredible nightlife with its bright and vibrant neon signs.</body></html>";
        } else if (i == 2) {
            text = "<html><body><font size='5'>#2 Disney World</font> <br>Disney World provides attractions for both children and adults. There are four theme parks that are sure to entertain people of all ages.</body></html>";
        } else if (i == 3) {
            text = "<html><body><font size='5'>#3 Washington DC</font> <br>Washington DC is the Capital of the United States and provides many historical monuments and museums for history buffs.</body></html>";
        } else if (i == 4) {
            text = "<html><body><font size='5'>#4 Paris</font> <br>Paris is home of the iconic Eiffel Tower and provides a romantic atmosphere.</body></html>";
        } else if (i == 5) {
            text = "<html><body><font size='5'>#5 Egypt</font> <br>Egypt is home to pyramids and ancient temples.</body></html>";
        }
        return text;
    }

    /**
     * Method to get background color for each slide
     */
    private Color getBackgroundColor(int slideIndex) {
        switch (slideIndex) {
            case 1:
                return Color.PINK;
            case 2:
                return Color.GREEN;
            case 3:
                return Color.YELLOW;
            case 4:
                return Color.ORANGE;
            case 5:
                return Color.RED;
            default:
                return Color.GRAY;
        }
    }

    /**
     * Getter for testing current slide index
     */
    public int getCurrentSlideIndex() {
        return currentSlideIndex;
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                SlideShow ss = new SlideShow();
                ss.setVisible(true);
            }
        });
    }
}
