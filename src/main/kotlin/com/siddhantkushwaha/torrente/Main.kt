package com.siddhantkushwaha.torrente

import com.siddhantkushwaha.todd.gdrive.GDrive
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Paths

private val logger: Logger = LoggerFactory.getLogger("Main")

fun main(args: Array<String>) {
    val torrentPath = Paths.get(args[0]).toString()
    val downloadsDir = Paths.get(args.getOrNull(1) ?: "downloads").toAbsolutePath().toString()
    val uploadToDrive = args.getOrNull(2) ?: "no"
    val driveParentFolderId = args.getOrNull(3)
    val deleteAfterUploaded = args.getOrNull(4) ?: "no"

    logger.info("Received args: ")
    logger.info("Torrent file path: $torrentPath")
    logger.info("Download directory: $downloadsDir")
    logger.info("Upload to drive: $uploadToDrive")
    logger.info("Parent folder id on Google Drive: $driveParentFolderId")

    val torrente = Torrente()
    if (uploadToDrive == "yes")
        torrente.gDrive = GDrive()
    torrente.processTorrent(torrentPath, downloadsDir, driveParentFolderId, deleteAfterUploaded)
}