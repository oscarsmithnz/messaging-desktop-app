/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Messagingv2;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.imageio.ImageIO;

/**
 *
 * @author Oscar
 */
public class UserMessage extends Message {

    private final String sender;
    private boolean hasImage;
    private ImageToSend image;

    public UserMessage(String c, String to, String from) {
        this.content = c;
        this.sendTo = to;
        this.sender = from;
    }

    public UserMessage(String c, String to, String from, boolean hasImage) {
        this.content = c;
        this.sendTo = to;
        this.sender = from;
        this.hasImage = hasImage;
    }

    public void setImage(BufferedImage i) {
        image = new ImageToSend(i);
        hasImage = true;
    }

    public BufferedImage getImage() {
        return image.getImage();
    }

    public boolean getHasImage() {
        return hasImage;
    }

    public String getSender() {
        return sender;
    }
    private class ImageToSend implements Serializable{
        private int[] pixels;
        private int[] newPixels;
        private int width;
        private int height;

        public ImageToSend(BufferedImage i){
            width = i.getWidth();
            height = i.getHeight();
            pixels = new int[width*height];
            newPixels = i.getRGB(0, 0, width, height, pixels, 0, width);
        }

    //https://stackoverflow.com/questions/14416107/int-array-to-bufferedimage
        public BufferedImage getImage() {
            int[] bitMasks = new int[]{0xFF0000, 0xFF00, 0xFF, 0xFF000000};
            SinglePixelPackedSampleModel sm = new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT, width, height, bitMasks);
            DataBufferInt db = new DataBufferInt(newPixels, newPixels.length);
            WritableRaster wr = Raster.createWritableRaster(sm, db, new Point());
            BufferedImage image = new BufferedImage(ColorModel.getRGBdefault(), wr, false, null);
            
            return image;
        }

//        private void writeObject(ObjectOutputStream out) throws IOException {
//            out.defaultWriteObject();
//            out.writeBoolean(true);
//            ImageIO.write(image, "png", out);
//        }
//        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
//            in.defaultReadObject();
//            image = ImageIO.read(in);
//        }
    }
}
