package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage

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
}
