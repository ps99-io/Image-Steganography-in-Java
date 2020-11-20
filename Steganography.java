/*
002
 *@author  William_Wilson
003
 *@version 1.6
004
 *Created: May 8, 2007
005
 */

/*
008
 *import list
009
 */

import java.io.File;

import java.awt.Point;

import java.awt.Graphics2D;

import java.awt.image.BufferedImage;

import java.awt.image.WritableRaster;

import java.awt.image.DataBufferByte;

import javax.imageio.ImageIO;

import javax.swing.JOptionPane;

/*
022
 *Class Steganography
023
 */

public class Steganography

{

    /*
     * 028 Steganography Empty Constructor 029
     */

    public Steganography()

    {

    }

    /*
     * 035 Encrypt an image with text, the output file will be of type .png 036
     * 
     * @param path The path (folder) containing the image to modify 037
     * 
     * @param original The name of the image to modify 038
     * 
     * @param ext1 The extension type of the image to modify (jpg, png) 039
     * 
     * @param stegan The output name of the file 040
     * 
     * @param message The text to hide in the image 041
     * 
     * @param type integer representing either basic or advanced encoding 042
     */

    public boolean encode(String path, String original, String ext1, String stegan, String message)

    {

        String file_name = image_path(path, original, ext1);

        BufferedImage image_orig = getImage(file_name);

        // user space is not necessary for Encrypting

        BufferedImage image = user_space(image_orig);

        image = add_text(image, message);

        return (setImage(image, new File(image_path(path, stegan, "png")), "png"));

    }

    /*
     * 056 Decrypt assumes the image being used is of type .png, extracts the hidden
     * text from an image 057
     * 
     * @param path The path (folder) containing the image to extract the message
     * from 058
     * 
     * @param name The name of the image to extract the message from 059
     * 
     * @param type integer representing either basic or advanced encoding 060
     */

    public String decode(String path, String name)

    {

        byte[] decode;

        try

        {

            // user space is necessary for decrypting

            BufferedImage image = user_space(getImage(image_path(path, name, "png")));

            decode = decode_text(get_byte_data(image));

            return (new String(decode));

        }

        catch (Exception e)

        {

            JOptionPane.showMessageDialog(null,

                    "There is no hidden message in this image!", "Error",

                    JOptionPane.ERROR_MESSAGE);

            return "";

        }

    }

    /*
     * 081 Returns the complete path of a file, in the form: path\name.ext 082
     * 
     * @param path The path (folder) of the file 083
     * 
     * @param name The name of the file 084
     * 
     * @param ext The extension of the file 085
     * 
     * @return A String representing the complete path of a file 086
     */

    private String image_path(String path, String name, String ext)

    {

        return path + "/" + name + "." + ext;

    }

    /*
     * 093 Get method to return an image file 094
     * 
     * @param f The complete path name of the image. 095
     * 
     * @return A BufferedImage of the supplied file path 096
     * 
     * @see Steganography.image_path 097
     */

    private BufferedImage getImage(String f)

    {

        BufferedImage image = null;

        File file = new File(f);

        try

        {

            image = ImageIO.read(file);

        }

        catch (Exception ex)

        {

            JOptionPane.showMessageDialog(null,

                    "Image could not be read!", "Error", JOptionPane.ERROR_MESSAGE);

        }

        return image;

    }

    /*
     * 116 Set method to save an image file 117
     * 
     * @param image The image file to save 118
     * 
     * @param file File to save the image to 119
     * 
     * @param ext The extension and thus format of the file to be saved 120
     * 
     * @return Returns true if the save is succesful 121
     */

    private boolean setImage(BufferedImage image, File file, String ext)

    {

        try

        {

            file.delete(); // delete resources used by the File

            ImageIO.write(image, ext, file);

            return true;

        }

        catch (Exception e)

        {

            JOptionPane.showMessageDialog(null,

                    "File could not be saved!", "Error", JOptionPane.ERROR_MESSAGE);

            return false;

        }

    }

    /*
     * 139 Handles the addition of text into an image 140
     * 
     * @param image The image to add hidden text to 141
     * 
     * @param text The text to hide in the image 142
     * 
     * @return Returns the image with the text embedded in it 143
     */

    private BufferedImage add_text(BufferedImage image, String text)

    {

        // convert all items to byte arrays: image, message, message length

        byte img[] = get_byte_data(image);

        byte msg[] = text.getBytes();

        byte len[] = bit_conversion(msg.length);

        try

        {

            encode_text(img, len, 0); // 0 first positiong

            encode_text(img, msg, 32); // 4 bytes of space for length: 4bytes*8bit = 32 bits

        }

        catch (Exception e)

        {

            JOptionPane.showMessageDialog(null,

                    "Target File cannot hold message!", "Error", JOptionPane.ERROR_MESSAGE);

        }

        return image;

    }

    /*
     * 164 Creates a user space version of a Buffered Image, for editing and saving
     * bytes 165
     * 
     * @param image The image to put into user space, removes compression
     * interferences 166
     * 
     * @return The user space version of the supplied image 167
     */

    private BufferedImage user_space(BufferedImage image)

    {

        // create new_img with the attributes of image

        BufferedImage new_img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

        Graphics2D graphics = new_img.createGraphics();

        graphics.drawRenderedImage(image, null);

        graphics.dispose(); // release all allocated memory for this image

        return new_img;

    }

    /*
     * 179 Gets the byte array of an image 180
     * 
     * @param image The image to get byte data from 181
     * 
     * @return Returns the byte array of the image supplied 182
     * 
     * @see Raster 183
     * 
     * @see WritableRaster 184
     * 
     * @see DataBufferByte 185
     */

    private byte[] get_byte_data(BufferedImage image)

    {

        WritableRaster raster = image.getRaster();
        System.out.println(raster);

        DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
        // System.out.println(buffer.getData().toString());

        return buffer.getData();

    }

    /*
     * 194 Gernerates proper byte format of an integer 195
     * 
     * @param i The integer to convert 196
     * 
     * @return Returns a byte[4] array converting the supplied integer into bytes
     * 197
     */

    private byte[] bit_conversion(int i)

    {

        // originally integers (ints) cast into bytes

        // byte byte7 = (byte)((i & 0xFF00000000000000L) >>> 56);

        // byte byte6 = (byte)((i & 0x00FF000000000000L) >>> 48);

        // byte byte5 = (byte)((i & 0x0000FF0000000000L) >>> 40);

        // byte byte4 = (byte)((i & 0x000000FF00000000L) >>> 32);

        // only using 4 bytes

        byte byte3 = (byte) ((i & 0xFF000000) >>> 24); // 0

        byte byte2 = (byte) ((i & 0x00FF0000) >>> 16); // 0

        byte byte1 = (byte) ((i & 0x0000FF00) >>> 8); // 0

        byte byte0 = (byte) ((i & 0x000000FF));

        // {0,0,0,byte0} is equivalent, since all shifts >=8 will be 0

        return (new byte[] { byte3, byte2, byte1, byte0 });

    }

    /*
     * 216 Encode an array of bytes into another array of bytes at a supplied offset
     * 217
     * 
     * @param image Array of data representing an image 218
     * 
     * @param addition Array of data to add to the supplied image data array 219
     * 
     * @param offset The offset into the image array to add the addition data 220
     * 
     * @return Returns data Array of merged image and addition data 221
     */

    private byte[] encode_text(byte[] image, byte[] addition, int offset)

    {

        // check that the data + offset will fit in the image

        if (addition.length + offset > image.length)

        {

            throw new IllegalArgumentException("File not long enough!");

        }

        // loop through each addition byte

        for (int i = 0; i < addition.length; ++i)

        {

            // loop through the 8 bits of each byte

            int add = addition[i];

            for (int bit = 7; bit >= 0; --bit, ++offset) // ensure the new offset value carries on through both loops

            {

                // assign an integer to b, shifted by bit spaces AND 1

                // a single bit of the current byte

                int b = (add >>> bit) & 1;

                // assign the bit by taking: [(previous byte value) AND 0xfe] OR bit to add

                // changes the last bit of the byte in the image to be the bit of addition

                image[offset] = (byte) ((image[offset] & 0xFE) | b);

            }

        }

        return image;

    }

    /*
     * 
     * Retrieves hidden text from an image
     * 
     * @param image Array of data, representing an image
     * 
     * @return Array of data which contains the hidden text
     * 
     */

    private byte[] decode_text(byte[] image)

    {

        int length = 0;

        int offset = 32;

        // loop through 32 bytes of data to determine text length

        for (int i = 0; i < 32; ++i) // i=24 will also work, as only the 4th byte contains real data

        {

            length = (length << 1) | (image[i] & 1);

        }

        byte[] result = new byte[length];

        // loop through each byte of text

        for (int b = 0; b < result.length; ++b)

        {

            // loop through each bit within a byte of text

            for (int i = 0; i < 8; ++i, ++offset)

            {

                // assign bit: [(new byte value) << 1] OR [(text byte) AND 1]

                result[b] = (byte) ((result[b] << 1) | (image[offset] & 1));

            }

        }

        return result;

    }

}
