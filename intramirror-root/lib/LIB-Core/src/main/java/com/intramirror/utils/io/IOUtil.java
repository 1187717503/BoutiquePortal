/* 
 * Created : 7 mar 2011
 * 
 * Copyright (c) 2011 Ericsson AB, Sweden. 
 * All rights reserved. 
 * The Copyright to the computer program(s) herein is the property of Ericsson AB, Sweden. 
 * The program(s) may be used and/or copied with the written permission from Ericsson AB 
 * or in accordance with the terms and conditions stipulated in the agreement/contract 
 * under which the program(s) have been supplied. 
 */
package com.intramirror.utils.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IOUtil {
    private static final Logger logger = LoggerFactory.getLogger(IOUtil.class);

    private IOUtil() {
    }

    /**
     * Copy the data from the input stream to the output stream. <br>
     * Data will be read until the EOF stream marker is provided or an exception occurs.
     *
     * @param istream
     *         Copy from
     * @param ostream
     *         Copy to
     * @return The number of bytes copied
     * @throws IOException
     */
    public static long copy(InputStream istream, OutputStream ostream) throws IOException {
        return copy(istream, ostream, false);
    }

    /**
     * Copy the data from the input stream to the output stream. <br>
     * Data will be read until the EOF stream marker is provided or an exception occurs.
     *
     * @param istream
     *         Copy from
     * @param ostream
     *         Copy to
     * @param forceClose
     *         If the in/out streams shall be closed
     * @return The number of bytes copied
     * @throws IOException
     */
    public static long copy(InputStream istream, OutputStream ostream, boolean forceClose) throws IOException {

        int totalData = 0;
        try {
            int read = 0;
            byte[] buffer = new byte[1024];
            while ((read = istream.read(buffer)) > 0) {
                ostream.write(buffer, 0, read);
                totalData += read;
            }
            ostream.flush();
        } finally {
            if (forceClose) {
                closeNoException(istream);
                closeNoException(ostream);
            }
        }
        return totalData;
    }

    /**
     * Copy the data from the reader to the writer. <br>
     * Data will be read until the EOF stream marker is provided or an exception occurs.
     *
     * @param reader
     *         Copy from
     * @param writer
     *         Copy to
     * @return The number of chars copied
     * @throws IOException
     */
    public static long copy(Reader reader, Writer writer) throws IOException {
        return copy(reader, writer, false);
    }

    /**
     * Copy the data from the reader to the writer. <br>
     * Data will be read until the EOF stream marker is provided or an exception occurs.
     *
     * @param reader
     *         Copy from
     * @param writer
     *         Copy to
     * @param forceClose
     *         If the reader/writer shall be closed
     * @return The number of chars copied
     * @throws IOException
     */
    public static long copy(Reader reader, Writer writer, boolean forceClose) throws IOException {
        int totalData = 0;
        try {
            int read = 0;
            char[] buffer = new char[1024];
            while ((read = reader.read(buffer)) > 0) {
                writer.write(buffer, 0, read);
                totalData += read;
            }
        } finally {
            try {
                writer.flush();
            } catch (Exception e) {
            }

            if (forceClose) {
                closeNoException(reader);
                closeNoException(writer);
            }
        }
        return totalData;
    }

    /**
     * Deletes a directory and all its contents. <br>
     * The method will recursively go through the provided directory and delete all files/directories it finds. <br>
     * Should it fail to to delete a file it will log this.
     *
     * @param path
     * @return
     * @since 1.6
     * @deprecated Use the {@link #delete(File)} method
     */
    @Deprecated
    public static boolean deleteDirectory(File path) {
        return delete(path);
    }

    /**
     * Deletes the provided file path. <br>
     * In case the path denotes a directory the method will recursively go through the provided directory and delete all files/directories it finds. <br>
     * Should it fail to to delete a file it will log this.
     *
     * @param path
     * @return <code>true</code> only if the path and all its possible child paths are deleted
     * @since 2.7
     */
    public static boolean delete(File path) {
        // if path is a directory, list and delete each found path
        if (path.isDirectory()) {
            for (File file : path.listFiles()) {
                delete(file);
            }
        }

        // don't try to delete a non-existing file, we deem the result as true as the file does not exist
        boolean deleteResult = path.exists() ? path.delete() : true;
        logger.debug("Attempted to delete path [{}], succeded [{}]", path, deleteResult);
        if (!deleteResult) {
            logger.warn("Failed to delete path [{}]", path);
        }

        return deleteResult;
    }

    /**
     * Attempts to delete all files in the provided directory matching the provided filter.
     *
     * @param dir
     *         The directory
     * @param filter
     *         The filter to match files against
     * @since 2.11
     */
    public static void delete(File dir, FilenameFilter filter) {
        // only list/filter files if it's actually an existing directory, otherwise use an empty list
        File[] files2delete = dir.isDirectory() ? dir.listFiles(filter) : new File[0];
        logger.debug("Found [{}] files to delete in [{}]", files2delete.length, dir);
        for (File file : files2delete) {
            delete(file);
        }
    }

    /**
     * Attempts to delete all files in the provided directory matching the provided filter.
     *
     * @param dir
     *         The directory
     * @param filter
     *         The filter to match files against
     * @since 2.11
     */
    public static void delete(File dir, FileFilter filter) {
        // only list/filter files if it's actually an existing directory, otherwise use an empty list
        File[] files2delete = dir.isDirectory() ? dir.listFiles(filter) : new File[0];
        logger.debug("Found [{}] files to delete in [{}]", files2delete.length, dir);
        for (File file : files2delete) {
            delete(file);
        }
    }

    /**
     * Closes the provided {@link InputStream}. <br>
     * Any exceptions are caught and ignored.
     *
     * @param istream
     * @since 1.16
     */
    public static void closeNoException(InputStream istream) {
        closeNoException((Closeable) istream);
    }

    /**
     * Closes the provided {@link OutputStream}. <br>
     * Any exceptions are caught and ignored.
     *
     * @param ostream
     * @since 1.16
     */
    public static void closeNoException(OutputStream ostream) {
        closeNoException((Closeable) ostream);
    }

    /**
     * Closes the provided {@link Reader}. <br>
     * Any exceptions are caught and ignored.
     *
     * @param istream
     * @since 1.16
     */
    public static void closeNoException(Reader reader) {
        closeNoException((Closeable) reader);
    }

    /**
     * Closes the provided {@link Writer}. <br>
     * Any exceptions are caught and ignored.
     *
     * @param ostream
     * @since 1.16
     */
    public static void closeNoException(Writer writer) {
        closeNoException((Closeable) writer);
    }

    /**
     * Closes the provided {@link Closeable}. <br>
     * Any exceptions are caught and logged.
     *
     * @param closeable
     *         The closeable to close, <code>null</code> values are ignored
     * @since 2.7
     */
    public static void closeNoException(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                logger.warn("Failed to close Closeable", e);
            }
        }
    }

    /**
     * Get the temporary directory as specified by the system property <tt>java.io.tmpdir</tt>.
     *
     * @return
     * @since 2.4
     */
    public static File getTempDir() {
        return new File(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Get a specified directory in the temporary directory as specified by the system property <tt>java.io.tmpdir</tt>.<br>
     * E.g <tt>/tmp/[childDir]</tt>
     *
     * @param childDir
     *         The child directory
     * @return
     * @since 2.4
     */
    public static File getTempDir(String childDir) {
        return getTempDir(childDir, false);
    }

    /**
     * Get a specified directory in the temporary directory as specified by the system property <tt>java.io.tmpdir</tt>. <br>
     * The methods appends if requested the time stamp in millis to the name thus making the path unique. E.g <tt>/tmp/[childDir]-[timestamp]</tt>
     *
     * @param childDir
     *         The child directory
     * @param appendTimeStamp
     *         If the time stamp shall be appended to the name
     * @return
     * @since 2.9
     */
    public static File getTempDir(String childDir, boolean appendTimeStamp) {
        if (appendTimeStamp) {
            return new File(getTempDir(), childDir + "-" + System.currentTimeMillis());
        }
        return new File(getTempDir(), childDir);
    }

    /**
     * Read the contents of the provided stream to a string. <br>
     * Same as <tt>streamToString(InputStream, false)</tt>
     *
     * @param stream
     *         The stream
     * @return The string content of the file
     * @throws IOException
     * @see {@link #streamToString(InputStream, boolean)}
     * @since 1.12
     */
    public static String streamToString(InputStream stream) throws IOException {
        return streamToString(stream, false);
    }

    /**
     * Read the contents of the provided stream to a string.
     *
     * @param stream
     *         The stream
     * @param close
     *         If the stream is to be closed after it has been read
     * @return The string content of the file
     * @throws IOException
     * @since 1.12
     */
    public static String streamToString(InputStream stream, boolean close) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("The stream cannot be null");
        }

        StringBuilder sb = new StringBuilder();

        byte[] data = new byte[512];
        int read = 0;
        while ((read = stream.read(data)) > 0) {
            sb.append(new String(data, 0, read));
        }

        if (close) {
            stream.close();
        }

        return sb.toString();
    }

}
