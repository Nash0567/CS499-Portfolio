import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SlideShowTest {

    private SlideShow slideShow;

    @BeforeEach
    void setUp() {
        slideShow = new SlideShow();
    }

    @Test
    void testInitialSlideIndex() {
        assertEquals(0, slideShow.getCurrentSlideIndex(), "Initial slide index should be 0 (Tokyo).");
    }

    @Test
    void testGoNext() {
        slideShow.goNext();
        assertEquals(1, slideShow.getCurrentSlideIndex(), "After the next button is clicked once, slide index should be 1 (Osaka).");
    }

    @Test
    void testGoPreviousWrapAround() {
        slideShow.goPrevious();
        assertEquals(9, slideShow.getCurrentSlideIndex(), "From index 0, clicking previous should wrap to 9 (Yokohama).");
    }

    @Test
    void testGoNextWrapAround() {
        for (int i = 0; i < 10; i++) {
            slideShow.goNext();
        }
        assertEquals(0, slideShow.getCurrentSlideIndex(), "After 10 nexts, index should wrap around to 0 (Tokyo).");
    }
}