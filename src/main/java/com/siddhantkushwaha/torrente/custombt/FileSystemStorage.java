package com.siddhantkushwaha.torrente.custombt;

/*

Modified getUnit()

*/

import bt.data.StorageUnit;
import bt.data.Storage;
import bt.metainfo.Torrent;
import bt.metainfo.TorrentFile;

import java.nio.file.Path;

public class FileSystemStorage implements Storage {

    public final Path rootDirectory;
    public final PathNormalizer pathNormalizer;

    public FileSystemStorage(Path rootDirectory) {
        this.rootDirectory = rootDirectory;
        this.pathNormalizer = new PathNormalizer(rootDirectory.getFileSystem());
    }

    @Override
    public StorageUnit getUnit(Torrent torrent, TorrentFile torrentFile) {
        String normalizedName = pathNormalizer.normalize(torrent.getName());
        Path torrentDirectory = rootDirectory.resolve(normalizedName);

        String normalizedPath = pathNormalizer.normalize(torrentFile.getPathElements());
        return new FileSystemStorageUnit(torrentDirectory, normalizedPath, torrentFile.getSize());
    }

    public String normalize(String path) {
        return pathNormalizer.normalize(path);
    }
}
