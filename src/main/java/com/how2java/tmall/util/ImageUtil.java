package com.how2java.tmall.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;

/**
 * 上传图片工具类
 */
public class ImageUtil {
    public static BufferedImage change2jpg(File f){
        try {
            Image image=Toolkit.getDefaultToolkit().createImage(f.getAbsolutePath());
            PixelGrabber pixelGrabber=new PixelGrabber(image,0,0,-1,-1,true);
            pixelGrabber.grabPixels();
            int width=pixelGrabber.getWidth();
            int height=pixelGrabber.getHeight();
            final int[] RGB_MASKS={0xFF0000, 0xFF00, 0xFF};
            final ColorModel RGB_OPQUE=new DirectColorModel(32,RGB_MASKS[0],RGB_MASKS[1],RGB_MASKS[2]);
            DataBuffer buffer=new DataBufferInt((int[]) pixelGrabber.getPixels(), pixelGrabber.getWidth() * pixelGrabber.getHeight());
            WritableRaster raster=Raster.createPackedRaster(buffer,width,height,width,RGB_MASKS,null);
            BufferedImage img=new BufferedImage(RGB_OPQUE,raster,false,null);
            return img;
        }catch (Exception e){
            e.printStackTrace();;
            return null;
        }
    }
    public static void resizeImage(File srcFile,int width,int height,File destFile){
        try {
            if (!destFile.getParentFile().exists()){
                destFile.getParentFile().mkdirs();
            }
            Image image= ImageIO.read(srcFile);
            image=resizeImage(image,width,height);
            ImageIO.write((RenderedImage)image ,"jpg",destFile);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static Image resizeImage(Image image, int width, int height) {
        try {
            BufferedImage bufferedImage=null;
            bufferedImage=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
            bufferedImage.getGraphics().drawImage(image.getScaledInstance(width,height,Image.SCALE_SMOOTH),0,0,null);
            return bufferedImage;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
