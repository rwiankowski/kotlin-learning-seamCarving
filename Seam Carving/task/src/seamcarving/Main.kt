 package seamcarving

 import java.io.File
 import javax.imageio.ImageIO

fun main() {

    val imageGenerator = ImageGenerator()

    println("Enter rectangle width:")
    val width = readln().toInt()
    println("Enter rectangle height:")
    val height = readln().toInt()
    println("Enter output image name:")
    val fileName = readln()

    val image = imageGenerator.generateImageWithTwoRedLines(width, height)
    val imageFile = File(fileName)
    ImageIO.write(image, "png", imageFile)

}
            