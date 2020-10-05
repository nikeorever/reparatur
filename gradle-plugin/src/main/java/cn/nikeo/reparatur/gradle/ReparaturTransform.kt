package cn.nikeo.reparatur.gradle

import cn.nikeo.reparatur.bytecodewriter.write
import cn.nikeo.transformer.jar.isClassFile
import cn.nikeo.transformer.jar.transformJar
import cn.nikeo.transformer.jar.transformJarDsl
import com.android.SdkConstants
import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import java.io.File
import java.io.FileOutputStream

class ReparaturTransform(private val crashWebViewsExtension: CrashWebViewsExtension) : Transform() {
    override fun getName(): String = "ReparaturTransform"

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> = TransformManager.CONTENT_CLASS

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> = TransformManager.SCOPE_FULL_PROJECT

    override fun isIncremental(): Boolean = true

    override fun transform(invocation: TransformInvocation) {
        if (!invocation.isIncremental) {
            invocation.outputProvider.deleteAll()
        }

        invocation.inputs.forEach { transformInput ->
            transformInput.jarInputs.forEach { jarInput ->
                val jarOutput = invocation.outputProvider.getContentLocation(
                    jarInput.name,
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
                )
                if (invocation.isIncremental) {
                    when (jarInput.status) {
                        Status.ADDED, Status.CHANGED -> {
                            println(
                                """
                                [JarInputs]
                                jarInput: ${jarInput.file}
                                jarOutput: $jarOutput
                            """.trimIndent()
                            )
                            transformJarContents(jarInput.file, jarOutput)
                        }
                        Status.REMOVED -> {
                            jarOutput.delete()
                        }
                        Status.NOTCHANGED -> {
                            // No need to transform.
                        }
                        else -> {
                            error("Unknown status: ${jarInput.status}")
                        }
                    }
                } else {
                    println(
                        """
                        [JarInputs]
                        jarInput: ${jarInput.file}
                        jarOutput: $jarOutput
                            """.trimIndent()
                    )
                    transformJarContents(jarInput.file, jarOutput)
                }
            }

            transformInput.directoryInputs.forEach { directoryInput ->
                val outputDir = invocation.outputProvider.getContentLocation(
                    directoryInput.name,
                    directoryInput.contentTypes,
                    directoryInput.scopes,
                    Format.DIRECTORY
                )
                if (invocation.isIncremental) {
                    directoryInput.changedFiles.forEach { (file, status) ->
                        val outputFile = toOutputFile(outputDir, directoryInput.file, file)
                        when (status) {
                            Status.ADDED, Status.CHANGED ->
                                transformFile(file, outputFile, outputDir)
                            Status.REMOVED -> outputFile.delete()
                            Status.NOTCHANGED -> {
                                // No need to transform.
                            }
                            else -> {
                                error("Unknown status: $status")
                            }
                        }
                    }
                } else {
                    directoryInput.file.walkTopDown().forEach { file ->
                        val outputFile = toOutputFile(outputDir, directoryInput.file, file)
                        transformFile(file, outputFile.parentFile, outputDir)
                    }
                }
            }
        }

    }

    /**
     * Checks if a file is a .class file.
     */
    private fun File.isClassFile() = this.isFile && this.extension == SdkConstants.EXT_CLASS


    private fun toOutputFile(outputDir: File, inputDir: File, inputFile: File) =
        File(outputDir, inputFile.relativeTo(inputDir).path)

    private fun transformJarContents(jarInput: File, jarOutput: File) {
        // TODO
        transformJar(jarInput = jarInput, jarOutput = jarOutput) { inputJarEntry, outputJarEntryInputStream ->
            when {
                inputJarEntry.isClassFile() -> {
                    // TODO
                    val entryName = inputJarEntry.name
                    val contained = crashWebViewsExtension.qualifiedNames.orNull?.map { qualifiedName ->
                        qualifiedName.replace(
                            ".",
                            File.separator
                        )
                    }?.any { qualifiedNamePath ->
                        entryName == qualifiedNamePath + SdkConstants.DOT_CLASS
                    } == true
                    if (contained) {
                        write(outputJarEntryInputStream, entryName.removeSuffix(SdkConstants.DOT_CLASS))
                    } else {
                        outputJarEntryInputStream.readBytes()
                    }
                }
                else -> outputJarEntryInputStream.readBytes()
            }
        }
    }

    private fun transformFile(inputFile: File, outputFileParent: File, outputDir: File) {
        println(
            """
                [DirectoryInputs]
                inputFile: $inputFile 
                outputFileParent: $outputFileParent
                outputDir: $outputDir
                """.trimIndent()
        )

        if (inputFile.isClassFile()) {
            // TODO
            var className = ""
            val contained = crashWebViewsExtension.qualifiedNames.orNull?.map { qualifiedName ->
                qualifiedName.replace(
                    ".",
                    File.separator
                )
            }?.any { qualifiedNamePath ->
                className = qualifiedNamePath
                inputFile.path.contains(qualifiedNamePath + SdkConstants.DOT_CLASS)
            } == true

            if (contained) {
                val bytes = write(inputFile.inputStream(), className)
                outputFileParent.mkdirs()
                File(outputFileParent, inputFile.name).writeBytes(bytes)
            } else {
                outputFileParent.mkdirs()
                inputFile.copyTo(target = File(outputFileParent, inputFile.name), overwrite = true)
            }
        } else if (inputFile.isFile) {
            // Copy all non .class files to the output.
            outputFileParent.mkdirs()
            inputFile.copyTo(target = File(outputFileParent, inputFile.name), overwrite = true)
        }
    }

}