package com.safetytrick;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;

import com.safetytrick.sql.avro.QueryActivityStatus;
import com.safetytrick.sql.avro.RuntimeMetrics;
import com.safetytrick.sql.avro.SqlProfile;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.conf.PlainParquetConfiguration;
import org.apache.parquet.hadoop.ParquetFileWriter;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.io.LocalInputFile;
import org.apache.parquet.io.LocalOutputFile;

public class Main {
    public static void main(String[] args) throws IOException {
        SqlProfile.Builder profileBuilder = SqlProfile.newBuilder();
        profileBuilder.setId("some-id")
            .setQuery("select 1")
            .setTables(Collections.emptyList())
            .setType("SELECT")
            .setJoinColumns(Collections.emptyList())
            .setCriteriaColumns(Collections.emptyList())
            .setGeneratedSource("select 1")
            .setTruncated(false)
            .setAnalysisFailed(false)
            .setAnalysisSkipped(false)
            .setIsDml(false)
            .setRuntimeMetrics(RuntimeMetrics.newBuilder()
                .setFullTableScan(false)
                .setExecuted(1L)
                .setErrors(0L)
                .setWarnings(0L)
                .setTotalTime(BigDecimal.ONE)
                .setMaxTime(BigDecimal.ONE)
                .setAverageTime(BigDecimal.ONE)
                .setRowsSent(1L)
                .setAverageRowsSent(1L)
                .setRowsScanned(1L)
                .setAverageRowsScanned(1L)
                .setTempTables(0L)
                .setTempDiskTables(0L)
                .setRowsSorted(0L)
                .setSortMergePasses(0L)
                .setDigest("some-digest")
                .setLastSeen(LocalDateTime.now())
                .setFirstSeen(LocalDateTime.now())
                .setLockTime(BigDecimal.ZERO)
                .setRowsAffected(0L)
                .setAverageRowsAffected(0L)
                .setDigestList(Collections.singletonList( "some-digest"))
                .build())
            .setDigest("some-digest")
            .setNormalizedDigest("some-normalized-digest")
            .setSchema$("dual")
            .setMismatchedJoins(Collections.emptyList())
            .setLineNumber(1)
            .setSourcedFiles(Collections.emptyList())
            .setQueryActivityStatus(QueryActivityStatus.UNKNOWN)
            .setModifiedForAnalysis(false)
            .setIsMultiSchema(false);

        SqlProfile sqlProfile = profileBuilder.build();

        Path parquetFile = Path.of("some-file.par");

        AvroParquetWriter.Builder<Object> parquetWriter =
            AvroParquetWriter.builder(new LocalOutputFile(parquetFile));

        try (ParquetWriter<Object> objectWriter = parquetWriter.withSchema(sqlProfile.getSchema())
            .withDictionaryEncoding(true)
            .withConf(
                new Configuration(false)
            )
            .withWriteMode(ParquetFileWriter.Mode.OVERWRITE)
            .build()) {

            objectWriter.write(sqlProfile);
        }

        try (ParquetReader<SqlProfile> parquetReader = AvroParquetReader.<SqlProfile>builder(
                new LocalInputFile(parquetFile), new PlainParquetConfiguration())
            .withDataModel(sqlProfile.getSpecificData())
            .build()) {

            System.out.println(parquetReader.read());
        }


    }
}