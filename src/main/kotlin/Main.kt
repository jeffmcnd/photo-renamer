import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.exif.ExifSubIFDDirectory
import java.io.File
import java.nio.file.Files
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun main(args: Array<String>) {

    val inputPath = args.firstOrNull() ?: throw IllegalArgumentException("Must provide a folder path")

    val folder = File(inputPath)
    if (folder.exists().not()) throw NoSuchFileException(folder)
    if (folder.isDirectory.not()) throw IllegalArgumentException("Path must be a directory")

    val files = folder.listFiles() ?: throw IllegalArgumentException("Directory is empty")

    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ssZ", Locale.US)
    files.forEach { renameFileWithDatePrefix(formatter, it)}
}

private fun renameFileWithDatePrefix(formatter: DateFormat, file: File) {
    val date = getImageCreationDate(file)
    val prefix = formatter.format(date)
    val path = file.toPath()
    val filename = "$prefix-${path.fileName}"
    val target = path.resolveSibling(filename)
    Files.move(path, target)
}

private fun getImageCreationDate(file: File): Date {
    val metadata = ImageMetadataReader.readMetadata(file)
    val directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory::class.java)
    return directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_DIGITIZED)
}