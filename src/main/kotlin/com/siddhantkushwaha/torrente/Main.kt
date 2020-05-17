package com.siddhantkushwaha.torrente

import com.siddhantkushwaha.todd.gdrive.GDrive
import java.nio.file.Paths


fun main(args: Array<String>) {
    val torrentPath = Paths.get(args[0]).toString()
    val downloadsDir = Paths.get(args.getOrNull(1) ?: "downloads").toAbsolutePath().toString()
    val uploadToDrive = args.getOrNull(2) ?: "no"
    val driveParentFolderId = args.getOrNull(3)
    val deleteAfterUploaded = args.getOrNull(4) ?: "no"

    println("Received args: ")
    println("Torrent file path: $torrentPath")
    println("Download directory: $downloadsDir")
    println("Upload to drive: $uploadToDrive")
    println("Parent folder id on Google Drive: $driveParentFolderId")

    val torrente = Torrente()
    if (uploadToDrive == "yes")
        torrente.gDrive = GDrive()
    torrente.processTorrent(torrentPath, downloadsDir, driveParentFolderId, deleteAfterUploaded)
}