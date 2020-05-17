package com.siddhantkushwaha.torrente

import bt.Bt
import bt.dht.DHTConfig
import bt.dht.DHTModule
import bt.runtime.Config
import bt.torrent.TorrentSessionState
import com.siddhantkushwaha.todd.gdrive.GDrive
import com.siddhantkushwaha.torrente.custombt.FileSystemStorage
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class Torrente {
    internal lateinit var gDrive: GDrive

    var config: Config = object : Config() {
        override fun getNumOfHashingThreads(): Int {
            return Runtime.getRuntime().availableProcessors() * 2;
        }
    }

    var dhtModule = DHTModule(object : DHTConfig() {
        override fun shouldUseRouterBootstrap(): Boolean {
            return true
        }
    })

    private fun uploadToDrive(
        torrentName: String,
        path: String,
        driveParentFolderId: String?,
        deleteAfterUploaded: String
    ) {
        if (this::gDrive.isInitialized) {
            println("Uploading $path at $driveParentFolderId")

            val hash = "$torrentName-$driveParentFolderId"
            if (loadCheckpoint(UPLOADED_TORRENTS_CHECKPOINT).contains(hash))
                println("Already uploaded, skipping.")

            gDrive.upload(filePath = path, parentId = driveParentFolderId)
            addToCheckpoint(hash, UPLOADED_TORRENTS_CHECKPOINT)

            println("Uploaded torrent, deleting..")
            if (deleteAfterUploaded == "yes")
                File(path).deleteRecursively()
        }
    }

    fun processTorrent(
        torrentFilePath: String,
        downloadsDir: String,
        driveParentFolderId: String?,
        deleteAfterUploaded: String
    ) {
        val storage = FileSystemStorage(Paths.get(downloadsDir))
        Files.createDirectories(storage.rootDirectory)

        var torrentName: String? = null
        var alreadyDownloaded = false
        val client = Bt.client()
            .torrent(Paths.get(torrentFilePath).toUri().toURL())
            .afterTorrentFetched { torrent ->

                torrentName = torrent.name
                println("Fetched torrent. $torrentName")

                if (loadCheckpoint(DOWNLOADED_TORRENTS_CHECKPOINT).contains(torrent.name)) {
                    println("Torrent already downloaded, skipping.")
                    alreadyDownloaded = true
                }
            }
            .config(config)
            .autoLoadModules()
            .module(dhtModule)
            .storage(storage)
            .build()

        var downloaded = false
        if (!alreadyDownloaded)
            client.startAsync({ tss: TorrentSessionState ->

                // callback may occur late
                if (alreadyDownloaded) {
                    client.stop()
                }

                println("Pieces complete: ${tss.piecesComplete}/${tss.piecesTotal}, remaining: ${tss.piecesRemaining}")
                if (tss.piecesRemaining == 0) {
                    downloaded = true
                    client.stop()
                }
            }, 2000).join()

        println("State: $downloaded, GDrive: ${this::gDrive.isInitialized}")
        if (downloaded) {
            torrentName?.let {
                addToCheckpoint(it, DOWNLOADED_TORRENTS_CHECKPOINT)
                uploadToDrive(
                    it,
                    storage.rootDirectory.resolve(storage.normalize(it)).toString(),
                    driveParentFolderId,
                    deleteAfterUploaded
                )
            }
        }
    }
}