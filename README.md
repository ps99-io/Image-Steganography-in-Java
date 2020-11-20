# Image-Steganography-in-Java

Steganography: “A method of hiding a secret message inside of other data.”  

Essentially the difference is that, while both hide a message, steganography is meant to make the message invisible, while cryptography changes the message’s form, by means of replacement and/or algorithm.  

This code is written in Java, and the following topics will need to be understood, before properly understanding how this method works: 

Bytes: individually as integers and as arrays 
Bit Operations: Logical AND (&), OR(|) and how they work 
Images: BufferedImage specifically 
ImageIO: how image files are opened and saved 
Graphics2D: accessing user space image properties 
Raster: specifically WritableRaster allows access to the buffer 
DataBufferByte: Buffer used with BufferedImage 

*These are the major topics needed to understand Steganography, but there are others used and assumed to be understood, as this topic is not meant for those inexperienced with the Java language*.
