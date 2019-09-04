/*
 * UNLICENSE
 *
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to [Unlicense](http://unlicense.org).
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * paralel process conection
 * @author Ignacio Rivera
 * @version 0.0.1
 */
public class RunConection implements Runnable{

    /**
     * The Logger
     */
    private static final Logger log = LoggerFactory.getLogger(RunConection.class);

    /**
     * the socket of the request
     */
    private final Socket socket;

    /**
     * the constructor
      * @param socket
     */
    public RunConection(Socket socket) {
        this.socket = socket;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see     java.lang.Thread#run()
     */
    @Override
    public void run() {
        try {

            // The remote connection address.
            final InetAddress address = socket.getInetAddress();

            log.debug("========================================================================================");
            log.debug("Connection from {} in port {}.", address.getHostAddress(), socket.getPort());
            processConnection(socket);
            socket.close();

        } catch (IOException e) {
            log.error("Error", e);

        }
    }

    /**
     * Process the connection.
     * cita: Urrutia, D. (2019). server (0.0.1) [Software] obtenido de http://durrutia.cl.
     * @param socket to use as source of data.
     */
    private void processConnection(final Socket socket) throws IOException {

        // Reading the inputstream
        final List<String> lines = this.readInputStreamByLines(socket);

        final String request = lines.get(0);
        log.debug("Request: {}", request);

        final PrintWriter pw = new PrintWriter(socket.getOutputStream());
        pw.println("HTTP/1.1 200 OK");
        pw.println("Server: DSM v0.0.1");
        pw.println("Date: " + new Date());
        pw.println("Content-Type: text/html; charset=UTF-8");
        // pw.println("Content-Type: text/plain; charset=UTF-8");
        pw.println();
        pw.println("<html><body>The Date: <strong>" + new Date() + "</strong></body></html>");
        pw.flush();

        log.debug("Process ended.");

    }

    /**
     * Read all the input stream.
     * cita: Urrutia, D. (2019). server (0.0.1) [Software] obtenido de http://durrutia.cl.
     * @param socket to use to read.
     * @return all the string readed.
     */
    private List<String> readInputStreamByLines(final Socket socket) throws IOException {

        final InputStream is = socket.getInputStream();

        // The list of string readed from inputstream.
        final List<String> lines = new ArrayList<>();

        // The Scanner
        final Scanner s = new Scanner(is).useDelimiter("\\A");
        log.debug("Reading the Inputstream ..");

        while (true) {

            final String line = s.nextLine();
            // log.debug("Line: [{}].", line);

            if (line.length() == 0) {
                break;
            } else {
                lines.add(line);
            }
        }
        // String result = s.hasNext() ? s.next() : "";

        // final List<String> lines = IOUtils.readLines(is, StandardCharsets.UTF_8);
        return lines;

    }
}
