import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.*;

/**
 * SlideShow.java
 *
 * A Java Swing application that displays a slideshow of the top 10
 * cities and prefectures in Japan.
 * 
 * Navigation is controlled using "Previous" and "Next" buttons,
 * with wrap-around cycling between slides.
 */

public class SlideShow extends JFrame {
	
	private static final long serialVersionUID = 1L; // for serialization compatibility
	
	private JPanel slidePane;   // Panel for image slides
	private JPanel textPane;    // Panel for text descriptions
	private JPanel buttonPane;  // Panel for navigation buttons
	private CardLayout card;    // Layout for image panel
	private CardLayout cardText;// Layout for text panel
	private JButton btnPrev;    // "Previous" button
	private JButton btnNext;    // "Next" button
	private int currentSlideIndex = 0; // Tracks current slide index

    // --- Top 10 cities/prefectures in Japan ---
    private final String[] images = {
        "/resources/Tokyo.jpg",
        "/resources/Osaka.jpg",
        "/resources/Kyoto.jpg",
        "/resources/Kobe.jpg",
        "/resources/Nara.jpg",
        "/resources/Sapporo.jpg",
        "/resources/Himeji.jpg",
        "/resources/Okinawa.jpg",
        "/resources/Hiroshima.jpg",
        "/resources/Yokohama.jpg"
    };

    // --- Descriptions for each slide ---
    private final String[] descriptions = {
        "<html><body><font size='5'>#1 Tokyo</font> <br>Japan’s bustling capital, famous for its bustling streets in Shibuya, iconic nightlife like Golden Gai located in Shinjuku, Akihabara’s anime/otaku culture, and historic temples like Senso-ji Asakusa, it truly is the best combination of historical and modern attractions alike.</body></html>",
        "<html><body><font size='5'>#2 Osaka</font> <br>Known as Japan’s kitchen, Osaka offers takoyaki and okonomiyaki, Osaka Castle, Universal Studios Japan, and quite its own nightlife in places like Dotonbori.</body></html>",
        "<html><body><font size='5'>#3 Kyoto</font> <br>The cultural heart of Japan, filled with temples, shrines, torii  gates, and geisha, as well as serene scenic places like Arashiyama Bamboo Forest.</body></html>",
        "<html><body><font size='5'>#4 Kobe</font> <br>Famous for the most prized beef in the world, Kobe beef,  beautiful harbor, and its blend of Japanese and Western architecture, the steak itself is worth the trip.</body></html>",
        "<html><body><font size='5'>#5 Nara</font> <br>Known for its parks, templs, and shrines, Nara is home to friendly bowing deer, the giant Buddha at Todai-ji Temple, and tranquil Japanese gardens.</body></html>",
        "<html><body><font size='5'>#6 Sapporo</font> <br>Known for the Sapporo Snow Festival, skiing in nearby resorts, fresh seafood, onsens, and Sapporo beer.</body></html>",
        "<html><body><font size='5'>#7 Himeji</font> <br>Home to Himeji Castle, Japan’s most spectacular and well-preserved feudal-era castle, and its delicious hole-in-the-wall spots to eat.</body></html>",
        "<html><body><font size='5'>#8 Okinawa</font> <br>Tropical islands with stunning beaches, coral reef diving, unique Ryukyu culture, and Okinawan cuisine.</body></html>",
        "<html><body><font size='5'>#9 Hiroshima</font> <br>Peace Memorial Park and Museum, Hiroshima-style okonomiyaki, and the famous floating torii gate of Miyajima.</body></html>",
        "<html><body><font size='5'>#10 Yokohama</font> <br>Japan’s second-largest city, known for its Chinatown, Cup Noodles Museum, Landmark Tower, beautiful waterfront views, and it also has quite the nightlife.</body></html>"
    };

    // --- Background colors for each description panel ---
    private final Color[] colors = {
        Color.PINK, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.RED,
        Color.CYAN, Color.MAGENTA, Color.LIGHT_GRAY, Color.WHITE, Color.GRAY
    };
    
    /** 
     * Constructor initializes the slideshow application by calling initComponent(). 
     */
    public SlideShow() throws HeadlessException {
        initComponent();
    }

    /**
     * Initializes the GUI components, panels, layouts, and buttons.
     */
    private void initComponent() {
        card = new CardLayout();
        cardText = new CardLayout();
        slidePane = new JPanel(card);
        textPane = new JPanel(cardText);
        buttonPane = new JPanel();
        btnPrev = new JButton("Previous");
        btnNext = new JButton("Next");

        // Frame setup
        setSize(800, 600);
        setLocationRelativeTo(null);
        setTitle("Top 10 Places to Visit in Japan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        // Add slides dynamically
        for (int i = 0; i < images.length; i++) {
            JLabel lblSlide = new JLabel(getResizeIcon(images[i]), JLabel.CENTER);
            JLabel lblTextArea = new JLabel(descriptions[i], JLabel.CENTER);
            slidePane.add(lblSlide, "card" + i);
            textPane.add(lblTextArea, "cardText" + i);
        }

        textPane.setBackground(colors[currentSlideIndex]);

        getContentPane().add(slidePane, BorderLayout.CENTER);
        getContentPane().add(textPane, BorderLayout.SOUTH);

        // Buttons
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

    public void goPrevious() {
        card.previous(slidePane);
        cardText.previous(textPane);
        currentSlideIndex = (currentSlideIndex - 1 < 0) ? images.length - 1 : currentSlideIndex - 1;
        textPane.setBackground(colors[currentSlideIndex]);
    }

    public void goNext() {
        card.next(slidePane);
        cardText.next(textPane);
        currentSlideIndex = (currentSlideIndex + 1) % images.length;
        textPane.setBackground(colors[currentSlideIndex]);
    }
    
    //Getter for testing
    public int getCurrentSlideIndex() {
        return currentSlideIndex;
    }

    private String getResizeIcon(String path) {
        return "<html><body><img width='800' height='500' src='" + getClass().getResource(path) + "'</body></html>";
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            SlideShow ss = new SlideShow();
            ss.setVisible(true);
        });
    }
}
