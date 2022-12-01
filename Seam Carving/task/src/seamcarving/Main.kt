 package seamcarving

 import java.io.File
 import javax.imageio.ImageIO

fun main(args: Array<String>) {
    val imageGenerator = ImageGenerator()

    val inputImageFile = File(args[1])
    val outputImageFile = File(args[3])
    val widthToResize = args[5].toInt()
    val heightToResize = args[7].toInt()

    val image = ImageIO.read(inputImageFile)
    val intermediateImage = imageGenerator.removeVerticalSeams(image, widthToResize)
    val outputImage = imageGenerator.removeHorizontalSeams(intermediateImage, heightToResize)

    ImageIO.write(outputImage, "png", outputImageFile)

}
            