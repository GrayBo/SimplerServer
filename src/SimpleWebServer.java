/***************************************************************
 ********
 * Grayson Boese
 * CSC414
 * Dr. Perkins
 SimpleWebServer.java
 This toy web server is used to illustrate security
 vulnerabilities.
 This web server only supports extremely simple HTTP GET
 requests.
 This file is also available at
 http://www.learnsecurity.com/ntk
 ****************************************************************
 *******/

import java.io.*;
import java.net.*;
import java.util.*;
public class SimpleWebServer {
    // First declare your class variables
    /* Run the HTTP server on this TCP port. */
    private static final int PORT = 8080;
    /* The socket used to process incoming connections
    from web clients */
    private static ServerSocket dServerSocket;

    // Second declare your class methods

    /**+
     * Constructs a SimpleWebServer object
     * @throws Exception
     */
    public SimpleWebServer () throws Exception {
        // class variable
        // instantiate a socket and connect it to the port
        dServerSocket = new ServerSocket (PORT); // from java.net.*
    }

    /**+
     *  Start the server to process requests
     * @throws Exception
     */
    public void run() throws Exception {
        while (true) {
            /* wait for a connection from a client */
            Socket s = dServerSocket.accept(); // from java.net.*
            /* then process the client's request */
            processRequest(s); //class method call
        }
    }

    /**+
     * Reads the HTTP request from the client, and
     * responds with the file the user requested or
     * a HTTP error code.
     * @param s - the socket the request is coming from and going to
     * @throws Exception
     */
    public void processRequest(Socket s) throws Exception {
        // declare local variables
        /* used to read data from the client */
        BufferedReader br = new BufferedReader
                (new InputStreamReader (s.getInputStream())); // from java.io.*
        /* used to write data to the client */
        OutputStreamWriter osw = new OutputStreamWriter (s.getOutputStream()); // from java.io.*

        /* read the HTTP request from the client */
        String request = br.readLine(); // from java.io.*
        String command = null;
        String pathname = null;

        /* parse the HTTP request by spaces */
        StringTokenizer st = new StringTokenizer (request, " "); // from java.util.*
        //type of HTTP request
        command = st.nextToken(); // from java.util.*
        //the path to the file the client is requesting
        pathname = st.nextToken(); //from java.util.*
        if (command.equals("GET")) {
            /* if the request is a GET
            try to respond with the file
            the user is requesting */
            serveFile (osw,pathname); // class method call
        }
        else {
            /* if the request is a NOT a GET,
            return an error saying this server
            does not implement the requested command */
            osw.write ("HTTP/1.0 501 Not Implemented\n\n"); // from java.io.*
        }

        /* close the connection to the client */
        osw.close();
    }

    /**+
     *
     * @param osw - the output stream you want written to
     * @param pathname - the path to the file you want to read from
     * @throws Exception
     */
    public void serveFile (OutputStreamWriter osw,
                           String pathname) throws Exception {
        // declare local variables
        FileReader fr=null; // from java.io.*
        int c=-1;
        StringBuffer sb = new StringBuffer(); //from java.io.*

        /* remove the initial slash at the beginning
        of the pathname in the request */

        if (pathname.charAt(0)=='/')
            pathname=pathname.substring(1);

        /* if there was no filename specified by the
        client, serve the "index.html" file */
        if (pathname.equals(""))
            pathname="index.html";
        /* try to open file specified by pathname */
        try {
            fr = new FileReader (pathname);
            // copy the next character from the file
            c = fr.read(); // java.io.*
        }
        catch (Exception e) {
        /* if the file is not found,return the
        appropriate HTTP response code */
            osw.write ("HTTP/1.0 404 Not Found\n\n");
            return;
        }
        /* if the requested file can be successfully opened
        and read, then return an OK response code and
        send the contents of the file */
        osw.write ("HTTP/1.0 200 OK\n\n");

        /* copy the rest of the contents of the
         * file into the buffer */
        while (c != -1) {
            sb.append((char)c);
            c = fr.read(); // java.io.*
        }
        // write the buffer contents to the output stream
        osw.write (sb.toString());
    }
    /* This method is called when the program is run from
    the command line. */
    public static void main (String argv[]) throws Exception {
        /* Create a SimpleWebServer object, and run it */
        SimpleWebServer sws = new SimpleWebServer();
        sws.run();
    }
}