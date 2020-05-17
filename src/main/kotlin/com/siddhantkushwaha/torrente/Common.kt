package com.siddhantkushwaha.torrente

import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Files
import java.nio.file.Paths

val DOWNLOADED_TORRENTS_CHECKPOINT = "checkpoints/downloaded_torrents_checkpoint"
val UPLOADED_TORRENTS_CHECKPOINT = "checkpoints/uploaded_torrents_checkpoint"

fun loadCheckpoint(checkpointPath: String): MutableSet<String> {
    val checkpointFile = File(checkpointPath)
    return if (!checkpointFile.exists())
        mutableSetOf()
    else {
        val objectInputStream = ObjectInputStream(checkpointFile.inputStream())
        objectInputStream.readObject() as MutableSet<String>
    }
}

fun addToCheckpoint(item: String, checkpointPath: String) {
    val checkpoint = loadCheckpoint(checkpointPath)
    checkpoint.add(item)

    val checkpointFile = File(checkpointPath)
    Files.createDirectories(Paths.get(checkpointFile.parent))

    val objectOutputStream = ObjectOutputStream(checkpointFile.outputStream())
    objectOutputStream.writeObject(checkpoint)
}