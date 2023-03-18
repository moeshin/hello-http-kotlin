import com.sun.net.httpserver.HttpServer
import java.io.ByteArrayOutputStream
import java.net.InetSocketAddress
import kotlin.collections.HashSet
import kotlin.system.exitProcess

var aHost = "127.0.0.1"
var aPort = 8080
var aAllowedMethods: Set<String>? = null
var aDisallowedMethods: Set<String>? = null

private fun parseMethods(str: String): Set<String> = HashSet<String>().apply {
    for (sub in str.split(',')) {
        var s = sub.trim()
        if (s == "") {
            continue
        }
        s = s.uppercase()
        add(s)
    }
}

fun parseArgs(args: Array<String>) {
    var last = 0
    for ((i, arg) in args.withIndex()) {
        if (i == 0) {
            continue
        }
        val m = i % 2
        if (m == 1) {
            if (!arg.startsWith('-')) {
                throw Exception("It is not a option: $arg")
            }
            if (arg == "--help") {
                println("""Usage: ${args[0]} [options]

Options:
  -h, --host <host>
        Listen host.
        If 0.0.0.0 will listen IPv4 only.
        If :: or [::] will listen IPv6,
        and you system enable dual-stack socket,
        it may also listen IPv4.
        (default "$aHost")
  -p, --port <port>
        Listen port. If 0 is random. (default $aPort)
  -m, --allowed-methods <method>[,<methods>...]
        Disallowed methods.
  -d, --disallowed-methods <method>[,<methods>...]
        Allowed methods.
  --help
        Print help.
""")
                exitProcess(0)
            }
            last = i
            continue
        }
        last = 0
        when (val name = args[i - 1]) {
            "-h", "--host" -> {
                aHost = arg
            }
            "-p", "--port" -> {
                aPort = arg.toInt()
            }
            "-m", "--allowed-methods" -> {
                aAllowedMethods = parseMethods(arg)
            }
            "-d", "--disallowed-methods" -> {
                aDisallowedMethods = parseMethods(arg)
            }
            else -> throw Exception("Unknown option: $name")
        }
    }

    if (last > 0) {
        throw Exception("No found ${args[last]} option value")
    }
}

fun main(args: Array<String>) {
    parseArgs(args)

    if (aHost == "0.0.0.0") {
        System.setProperty("java.net.preferIPv4Stack", "true")
    }

    val addr = InetSocketAddress(aHost, aPort)
    println("Listening $addr")
    val server = HttpServer.create(addr, 0)

    server.createContext("/") { t ->
        val method = t.requestMethod
        val requestLine = "$method: ${t.requestURI} ${t.protocol}"
        println(requestLine)

        if (aDisallowedMethods?.contains(method) == true || aAllowedMethods?.contains(method) == false) {
            t.sendResponseHeaders(405, -1)
            return@createContext
        }

        t.responseHeaders.set("Content-Type", "text/plain; charset=utf-8")

        if (method == "HEAD") {
            t.sendResponseHeaders(200, -1)
            return@createContext
        }

        val os = ByteArrayOutputStream(1024)
        os.write("Hello HTTP\n\n$requestLine\r\n".toByteArray())
        for (header in t.requestHeaders) {
            for (value in header.value) {
                os.write("${header.key}: $value\n".toByteArray())
            }
        }
        t.requestBody.copyTo(os)
        t.sendResponseHeaders(200, os.size().toLong())
        t.responseBody.use {
            os.writeTo(it)
        }
    }
    server.start()
}