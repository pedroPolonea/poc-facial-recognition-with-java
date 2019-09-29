package capture;

import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import java.awt.event.KeyEvent;
import java.util.Scanner;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.*;


public class FaceCapture {

    private static final String FRONTAL_FACE = "src/main/resources/haarcascade-frontalface-alt.xml";
    private static final int NUMERO_AMOSTRA = 25;

    private OpenCVFrameGrabber webCan;
    private KeyEvent keyEvent;
    private OpenCVFrameConverter.ToMat converteMat;
    private CanvasFrame canvasFrame;
    private CascadeClassifier faceDetector;
    private Mat colorPicture;

    public FaceCapture() {
        webCan = new OpenCVFrameGrabber(0);
        canvasFrame = new CanvasFrame("Preview", getGama());
        converteMat = new OpenCVFrameConverter.ToMat();
        faceDetector = new CascadeClassifier(FRONTAL_FACE);
        colorPicture = new Mat();
        keyEvent = null;
    }

    public void capture() throws FrameGrabber.Exception, InterruptedException {
        webCan.start();

        Frame frameCapturado = null;
        int sample = 1;
        System.out.println("Digite seu id: ");

        Scanner register = new Scanner(System.in);
        int idPerson = register.nextInt();

        while ((frameCapturado = webCan.grab()) != null) {

            colorPicture = converteMat.convert(frameCapturado);
            Mat imagemCinza = new Mat();
            cvtColor(colorPicture, imagemCinza, COLOR_BGRA2GRAY);
            RectVector facesDetected = new RectVector();
            faceDetector.detectMultiScale(imagemCinza, facesDetected, 1.1, 1, 0, new Size(150,150), new Size(500,500));

            defineKey(5);

            for (int i=0; i < facesDetected.size(); i++) {
                Rect dadosFace = facesDetected.get(0);
                rectangle(colorPicture, dadosFace, new Scalar(0,0,255, 0));
                Mat faceCapturada = new Mat(imagemCinza, dadosFace);
                resize(faceCapturada, faceCapturada, new Size(160,160));

                defineKey(5);
                sample = createPhoto(sample, idPerson, faceCapturada);
            }

            defineKey(20);
            if (canvasFrame.isVisible()) {
                canvasFrame.showImage(frameCapturado);
            }

            if (sample > NUMERO_AMOSTRA) {
                break;
            }
        }

        canvasFrame.dispose();
        webCan.stop();
   }

    private int createPhoto(final int sample, final int idPerson, final Mat faceCapturada) {
        if (keyEvent != null) {
            if (keyEvent.getKeyChar() == 'q') {
                if (sample <= NUMERO_AMOSTRA) {
                    imwrite("src/main/resources/photo/pessoa." + idPerson + "." + sample + ".jpg", faceCapturada);
                    System.out.println("Foto " + sample + " capturada\n");
                    return sample+1;
                }
            }
            keyEvent = null;
        }

        return sample;
    }

    private double getGama(){
        return CanvasFrame.getDefaultGamma() / webCan.getGamma();
    }

    private void defineKey(final int delay) throws InterruptedException {
        if (keyEvent == null) {
            keyEvent = canvasFrame.waitKey(delay);
        }
    }
}
