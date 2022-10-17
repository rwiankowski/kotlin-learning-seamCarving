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
}
