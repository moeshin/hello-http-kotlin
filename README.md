# Hello HTTP

Build an HTTP server based on the standard library and respond to the request message.

## Usage

```text
Usage: hello-http [options]

Options:
  -h, --host <host>
        Listen host.
        If 0.0.0.0 will listen IPv4 only.
        If :: or [::] will listen IPv6,
        and you system enable dual-stack socket,
        it may also listen IPv4.
        (default "127.0.0.1")
  -p, --port <port>
        Listen port. If 0 is random. (default 8080)
  -m, --allowed-methods <method>[,<methods>...]
        Disallowed methods.
  -d, --disallowed-methods <method>[,<methods>...]
        Allowed methods.
  --help
        Print help.
```

Run

```shell
./hello-http
```

Hello HTTP output

```text
Listening /127.0.0.1:8080
```

Test with cURL

```shell
curl http://127.0.0.1:8080
```

cURL output

```text
Hello HTTP

GET: / HTTP/1.1
Accept: */*
Host: 127.0.0.1:8080
User-agent: curl/7.74.0

```

Hello HTTP output

```text
GET / HTTP/1.1
```
