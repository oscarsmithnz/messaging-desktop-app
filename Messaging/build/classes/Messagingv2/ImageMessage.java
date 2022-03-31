/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Messagingv2;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.imageio.ImageIO;

/**
 *
 * @author Oscar
 */
public class ImageMessage extends Message implements Serializable {

    private BufferedImage image;

    public ImageMessage(BufferedImage i) {
        this.image = i;
    }

    public BufferedImage getImage() {
        return image;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
//        if (image != null) {
            out.writeBoolean(true);
            ImageIO.write(image, "png", out);
//        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
//        if (in.readBoolean()) {
            image = ImageIO.read(in);
//        }
    }
}
