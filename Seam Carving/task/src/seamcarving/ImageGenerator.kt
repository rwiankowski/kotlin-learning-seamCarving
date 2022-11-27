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

    fun createEnergyImage(image: BufferedImage): BufferedImage {

        val energies = calculateImageEnergy(image)

        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                val intensity = energies[x][y].toInt()
                image.setRGB(x, y, Color(intensity, intensity, intensity).rgb)
            }
        }

        return image
    }

    /* fun findSeamWithLowestEnergy(image: BufferedImage): BufferedImage {

        val energies = calculateImageEnergy(image)
        val pixelPaths = mutableListOf<MutableList<Int>>()
        val seamEnergies = mutableListOf<Double>()

        for (y in 0 until image.height) {
                for (x in 0 until image.width) print("${energies[x][y]} ")
               print("\n")
        }

        for (x in 0 until image.width) {
            pixelPaths.add(mutableListOf())
            pixelPaths[x].add(x)
            seamEnergies.add(energies[x][0])
            for (y in 1 until image.height) {
                val start = pixelPaths[x].last()
                pixelPaths[x].add((start - 1).coerceAtLeast(0))
                for (index in start  .. (start + 1).coerceAtMost(image.width - 1)) {
                    if (energies[index][y] < energies[pixelPaths[x][y]][y]) pixelPaths[x][y] = index
                }
                seamEnergies[x] += energies[pixelPaths[x][y]][y]
            }
        }

        val lowestEnergy = seamEnergies.minOf { it }
        val pathToFollow = seamEnergies.indexOf(lowestEnergy)


        for (y in 0 until image.height) {
            image.setRGB(pixelPaths[pathToFollow][y], y, Color(255, 0, 0).rgb
            )
        }

        return image
    }
    */

    fun findSeamWithLowestEnergy(image: BufferedImage): BufferedImage {

        val energies = calculateImageEnergy(image)

        val seamEnergies = mutableListOf<MutableList<Double>>()
        val seamPath = mutableListOf<MutableList<Int>>()

        seamEnergies.add(mutableListOf())
        seamPath.add(mutableListOf())
        for (x in 0 until image.width) {
            seamEnergies[0].add(energies[x][0])
            seamPath[0].add(x)
        }

        for (y in 1 until image.height) {
            seamEnergies.add(mutableListOf())
            seamPath.add(mutableListOf())

            for (x in 0 until image.width) {

                val start = (x - 1).coerceAtLeast(0)
                val end = (x + 1).coerceAtMost(image.width - 1)
                var lowestEnergyValue = seamEnergies[y - 1][start]
                var lowestEnergyIndex = start
                for (index in start .. end) {
                    if (seamEnergies[y - 1][index] < lowestEnergyValue) {
                        lowestEnergyValue = seamEnergies[y - 1][index]
                        lowestEnergyIndex = index
                    }
                }
                seamEnergies[y].add(energies[x][y] + lowestEnergyValue)
                seamPath[y].add(lowestEnergyIndex)
            }
        }

        val smallestEnergySeam = seamEnergies.last().minOf { it }
        val smallestEnergyIndex = seamEnergies.last().indexOf(smallestEnergySeam)

        var nextIndexInSeam = smallestEnergyIndex

        for (y in image.height - 1 downTo 0 ) {

            image.setRGB(nextIndexInSeam, y, Color.RED.rgb)
            if (y != 0) nextIndexInSeam = seamPath[y][nextIndexInSeam]
        }

        return image
    }

    private fun calculateImageEnergy(image: BufferedImage): MutableList<MutableList<Double>> {

        val energies = mutableListOf<MutableList<Double>>()

        for (x in 0 until image.width) {
            energies.add(mutableListOf())
            for (y in 0 until image.height) {

                val xMinusOneRgb = when (x) {
                    0 -> Color(image.getRGB(0, y))
                    image.width - 1 -> Color(image.getRGB(x - 2, y))
                    else -> Color(image.getRGB(x - 1, y))
                }

                val xPlusOneRgb = when (x) {
                    0 -> Color(image.getRGB(2, y))
                    image.width - 1 -> Color(image.getRGB(x, y))
                    else -> Color(image.getRGB(x + 1, y))
                }
                val xGradient = (xMinusOneRgb.red - xPlusOneRgb.red).toDouble()
                    .pow(2.0) + (xMinusOneRgb.green - xPlusOneRgb.green).toDouble()
                    .pow(2.0) + (xMinusOneRgb.blue - xPlusOneRgb.blue).toDouble().pow(2.0)


                val yMinusOneRgb = when (y) {
                    0 -> Color(image.getRGB(x, 0))
                    image.height - 1 -> Color(image.getRGB(x, y - 2))
                    else -> Color(image.getRGB(x, y - 1))
                }
                val yPlusOneRgb = when (y) {
                    0 -> Color(image.getRGB(x, 2))
                    image.height - 1 -> Color(image.getRGB(x, y))
                    else -> Color(image.getRGB(x, y + 1))
                }
                val yGradient = (yMinusOneRgb.red - yPlusOneRgb.red).toDouble()
                    .pow(2.0) + (yMinusOneRgb.green - yPlusOneRgb.green).toDouble()
                    .pow(2.0) + (yMinusOneRgb.blue - yPlusOneRgb.blue).toDouble().pow(2.0)

                energies[x].add(sqrt(xGradient + yGradient))
            }
        }

        var maxEnergy = 0.0
        energies.forEach { x -> x.forEach { if(it >= maxEnergy) maxEnergy = it }}

        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                energies[x][y] = (255.0 * energies[x][y] / maxEnergy)
            }
        }

        return energies
    }
}



