from http.server import BaseHTTPRequestHandler, HTTPServer

port = None


class Handler(BaseHTTPRequestHandler):
    def do_POST(self):
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()
        self.wfile.write(bytes('Ok', "utf8"))

    def log_message(self, format, *args):
        pass


def run(server_class=HTTPServer, handler_class=Handler):
    global port
    port = int(input('Client port: '))
    server_address = ('', port)
    httpd = server_class(server_address, handler_class)
    httpd.serve_forever()


if __name__ == '__main__':
    run()
