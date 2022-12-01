package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.*

class ImageGenerator {

    fun removeHorizontalSeams(image: BufferedImage, heightToResize: Int): BufferedImage {

        var newImage = image

        repeat(heightToResize) {
            val energies = calculateImageEnergy(newImage)
            val seamEnergies = mutableListOf<MutableList<Double>>()
            val seamPath = mutableListOf<MutableList<Int>>()
            val resizedImage = BufferedImage(newImage.width, newImage.height  - 1, BufferedImage.TYPE_INT_RGB)

            seamEnergies.add(mutableListOf())
            seamPath.add(mutableListOf())
            for (y in 0 until newImage.height) {
                seamEnergies[0].add(energies[0][y])
                seamPath[0].add(y)
            }

            for (x in 1 until newImage.width) {
                seamEnergies.add(mutableListOf())
                seamPath.add(mutableListOf())

                for (y in 0 until newImage.height) {

                    val start = (y - 1).coerceAtLeast(0)
                    val end = (y + 1).coerceAtMost(newImage.height - 1)
                    var lowestEnergyValue = seamEnergies[x - 1][start]
                    var lowestEnergyIndex = start
                    for (index in start .. end) {
                        if (seamEnergies[x - 1][index] < lowestEnergyValue) {
                            lowestEnergyValue = seamEnergies[x - 1][index]
                            lowestEnergyIndex = index
                        }
                    }
                    seamEnergies[x].add(energies[x][y] + lowestEnergyValue)
                    seamPath[x].add(lowestEnergyIndex)
                }
            }

            val smallestEnergySeam = seamEnergies.last().minOf { it }
            val smallestEnergyIndex = seamEnergies.last().indexOf(smallestEnergySeam)

            var nextIndexInSeam = smallestEnergyIndex
            var modifier = 1

            for (x in resizedImage.width - 1 downTo 0) {
                for (y in resizedImage.height - 1 downTo 0) {
                    if (y + modifier == nextIndexInSeam) modifier = 0
                    resizedImage.setRGB(x, y, newImage.getRGB(x, y + modifier))
                }
                modifier = 1
                if (x != 0 ) nextIndexInSeam = seamPath[x][nextIndexInSeam]
            }
            newImage = resizedImage
        }
        return newImage
    }

    fun removeVerticalSeams(image: BufferedImage, widthToResize: Int): BufferedImage {

        var newImage = image

        repeat(widthToResize) {

            val energies = calculateImageEnergy(newImage)
            val seamEnergies = mutableListOf<MutableList<Double>>()
            val seamPath = mutableListOf<MutableList<Int>>()

            seamEnergies.add(mutableListOf())
            seamPath.add(mutableListOf())

            for (x in 0 until newImage.width) {
                seamEnergies[0].add(energies[x][0])
                seamPath[0].add(x)
            }

            for (y in 1 until newImage.height) {
                seamEnergies.add(mutableListOf())
                seamPath.add(mutableListOf())

                for (x in 0 until newImage.width) {

                    val start = (x - 1).coerceAtLeast(0)
                    val end = (x + 1).coerceAtMost(newImage.width - 1)
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

            val resizedImage = BufferedImage(newImage.width - 1, newImage.height, BufferedImage.TYPE_INT_RGB)
            var modifier = 1

            for (y in resizedImage.height - 1 downTo 0 ) {
                for (x in resizedImage.width - 1 downTo 0) {
                    if ( x + modifier == nextIndexInSeam ) modifier = 0
                    resizedImage.setRGB(x, y, newImage.getRGB(x + modifier, y))
                }
                modifier = 1
                if (y != 0) nextIndexInSeam = seamPath[y][nextIndexInSeam]
            }
            newImage = resizedImage
        }

        return newImage
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



