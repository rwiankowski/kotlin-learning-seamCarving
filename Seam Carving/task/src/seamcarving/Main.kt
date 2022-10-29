 package seamcarving

 import java.io.File
 import javax.imageio.ImageIO

fun main(args: Array<String>) {
    val imageGenerator = ImageGenerator()

    val inputImageFile = File(args[1])
    val image = ImageIO.read(inputImageFile)
    val outputImage = imageGenerator.calculateImageEnergy(image)
    val outputImageFile = File(args[3])
    ImageIO.write(outputImage, "png", outputImageFile)

}
            