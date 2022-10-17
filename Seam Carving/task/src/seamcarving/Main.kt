 package seamcarving

 import java.io.File
 import javax.imageio.ImageIO

fun main(args: Array<String>) {
    val imageGenerator = ImageGenerator()

    val imageFile = File(args[1])
    val image = ImageIO.read(imageFile)
    val negativeImage = imageGenerator.createImageNegative(image)
    val negativeImageFile = File(args[3])
    ImageIO.write(negativeImage, "png", negativeImageFile)

}
            