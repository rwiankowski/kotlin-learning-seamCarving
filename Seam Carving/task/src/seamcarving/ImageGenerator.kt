package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.*

class ImageGenerator {

    fun generateImageWithTwoRedLines(width: Int, height: Int) : BufferedImage {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val lines = image.createGraphics()
        lines.color = Color.RED
        lines.drawLine(0 , 0, width - 1, height - 1)
        lines.drawLine(0 , height - 1, width - 1, 0)
        return image
    }

    fun createImageNegative(image: BufferedImage): BufferedImage {

        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                val color = Color(image.getRGB(x, y))
                val negative = Color(255 - color.red, 255 - color.green, 255 - color.blue)
                image.setRGB(x, y, negative.rgb)
            }
        }

        return image

    }

    fun calculateImageEnergy(image: BufferedImage): BufferedImage {

        val energies = mutableListOf<MutableList<Double>>()

        for (x in 0 until image.width) {
            energies.add(mutableListOf())
            for (y in 0 until image.height) {

                val xMinusOneRgb = when(x) {
                    0 ->  Color(image.getRGB(0, y))
                    image.width - 1 -> Color(image.getRGB(x - 2, y))
                    else -> Color(image.getRGB(x - 1, y))
                }

                val xPlusOneRgb = when(x) {
                    0 ->  Color(image.getRGB(2, y))
                    image.width - 1 -> Color(image.getRGB(x, y))
                    else -> Color(image.getRGB(x + 1, y))
                }
                val xGradient = (xMinusOneRgb.red - xPlusOneRgb.red).toDouble().pow(2.0) + (xMinusOneRgb.green - xPlusOneRgb.green).toDouble().pow(2.0) + (xMinusOneRgb.blue - xPlusOneRgb.blue).toDouble().pow(2.0)


                val yMinusOneRgb = when(y) {
                    0 -> Color(image.getRGB(x, 0))
                    image.height - 1 -> Color(image.getRGB(x, y - 2))
                    else -> Color(image.getRGB(x, y - 1))
                }
                val yPlusOneRgb = when(y) {
                    0 -> Color(image.getRGB(x, 2))
                    image.height - 1 -> Color(image.getRGB(x, y))
                    else -> Color(image.getRGB(x, y + 1))
                }
                val yGradient = (yMinusOneRgb.red - yPlusOneRgb.red).toDouble().pow(2.0) + (yMinusOneRgb.green - yPlusOneRgb.green).toDouble().pow(2.0) + (yMinusOneRgb.blue - yPlusOneRgb.blue).toDouble().pow(2.0)

                energies[x].add(sqrt(xGradient + yGradient))
            }
        }

        var maxEnergy = 0.0
        energies.forEach { x -> x.forEach { if(it >= maxEnergy) maxEnergy = it }}

        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                val intensity = (255.0 * energies[x][y] / maxEnergy).toInt()
                image.setRGB(x, y, Color(intensity, intensity, intensity).rgb)
            }
        }

        return image
    }
}
