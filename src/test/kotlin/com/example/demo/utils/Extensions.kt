package com.example.demo.utils

import org.testcontainers.shaded.com.google.common.io.Resources.getResource
import org.testcontainers.shaded.com.google.common.io.Resources.toByteArray
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.toList

fun readFolder(folderName: String) =
    Files.walk(Paths.get(File("src/test/resources/json/$folderName").absolutePath)).use { stream ->
        stream.filter { Files.isRegularFile(it) }
            .map { toByteArray(getResource("json/$folderName/${it.fileName}")) }
            .toList()
    }