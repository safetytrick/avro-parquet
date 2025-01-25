package com.safetytrick;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.parquet.io.DelegatingSeekableInputStream;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.io.SeekableInputStream;

public class ParquetInputFile implements InputFile {
    private final Path inputFile;
    private final byte[] data;

    public ParquetInputFile(Path inputFile) throws IOException {
        this.inputFile = Objects.requireNonNull(inputFile);
        this.data = Files.readAllBytes(inputFile);
    }

    @Override
    public long getLength() {
        return data.length;
    }

    @Override
    public SeekableInputStream newStream() {
        SeekableByteArrayInput inputStream = new SeekableByteArrayInput(this.data);
        return new DelegatingSeekableInputStream(inputStream) {
            @Override
            public long getPos() throws IOException {
                return inputStream.tell();
            }

            @Override
            public void seek(long l) throws IOException {
                inputStream.seek(l);
            }
        };
    }

    @Override
    public String toString() {
        return "ParquetInputFile{" +
            "inputFile=" + inputFile +
            '}';
    }
}
