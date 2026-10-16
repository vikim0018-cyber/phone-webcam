package com.phonewebcam.app

import java.io.BufferedOutputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Minimal MJPEG-over-HTTP server.
 * Phone browser/PC can open:  http://<phone-ip>:8080/video
 * and see a live motion-jpeg stream (works directly in any browser,
 * and can be read by OpenCV's cv2.VideoCapture(url) on PC).
 */
class MjpegServer(private val port: Int = 8080) {

    private val running = AtomicBoolean(false)
    private var serverSocket: ServerSocket? = null
    private val clientStreams = CopyOnWriteArrayList<OutputStream>()
    private val BOUNDARY = "frameboundary"

    fun start() {
        if (running.get()) return
        running.set(true)
        Thread {
            try {
                serverSocket = ServerSocket(port)
                while (running.get()) {
                    val client: Socket = serverSocket!!.accept()
                    Thread { handleClient(client) }.start()
                }
            } catch (e: Exception) {
                // server stopped or socket error
            }
        }.start()
    }

    private fun handleClient(client: Socket) {
        try {
            // Read and discard the HTTP request line(s) - we only serve one endpoint
            client.getInputStream().bufferedReader().readLine()

            val out = BufferedOutputStream(client.getOutputStream())
            val header = "HTTP/1.0 200 OK\r\n" +
                    "Server: PhoneWebcam\r\n" +
                    "Connection: close\r\n" +
                    "Cache-Control: no-cache\r\n" +
                    "Content-Type: multipart/x-mixed-replace; boundary=$BOUNDARY\r\n\r\n"
            out.write(header.toByteArray())
            out.flush()

            clientStreams.add(out)
            // Keep thread alive while this client is connected (writes happen in pushFrame)
            while (running.get() && !client.isClosed) {
                Thread.sleep(500)
            }
        } catch (e: Exception) {
            // client disconnected
        } finally {
            try {
                clientStreams.removeIf { it === client.getOutputStream() }
            } catch (_: Exception) {
            }
            try {
                client.close()
            } catch (_: Exception) {
            }
        }
    }

    /** Call this with each new JPEG frame (ByteArray) from the camera. */
    fun pushFrame(jpegBytes: ByteArray) {
        if (clientStreams.isEmpty()) return
        val header = ("--$BOUNDARY\r\n" +
                "Content-Type: image/jpeg\r\n" +
                "Content-Length: ${jpegBytes.size}\r\n\r\n").toByteArray()
        val footer = "\r\n".toByteArray()

        val dead = mutableListOf<OutputStream>()
        for (stream in clientStreams) {
            try {
                synchronized(stream) {
                    stream.write(header)
                    stream.write(jpegBytes)
                    stream.write(footer)
                    stream.flush()
                }
            } catch (e: Exception) {
                dead.add(stream)
            }
        }
        clientStreams.removeAll(dead)
    }

    fun stop() {
        running.set(false)
        try {
            serverSocket?.close()
        } catch (_: Exception) {
        }
        clientStreams.clear()
    }
}
