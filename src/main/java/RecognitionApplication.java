import capture.FaceCapture;
import org.bytedeco.javacv.FrameGrabber;

public class RecognitionApplication {
    public static void main(String[] args) throws FrameGrabber.Exception, InterruptedException {
        final FaceCapture faceCapture = new FaceCapture();
        faceCapture.capture();
    }
}
