"""Desktop console client for the Lab 1 servlet (no external packages)."""

import argparse
import sys
from urllib.error import HTTPError, URLError
from urllib.parse import urlencode
from urllib.request import Request, urlopen


def invoke_servlet(value, url):
    data = urlencode({"value": value}).encode("utf-8")
    request = Request(
        url,
        data=data,
        method="POST",
        headers={
            "Accept": "text/plain",
            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
            "User-Agent": "Lab1-PythonDesktopClient/1.0",
            "Accept-Language": "ro, en;q=0.9",
        },
    )
    with urlopen(request, timeout=5) as response:
        text = response.read().decode(response.headers.get_content_charset() or "utf-8")
        if response.headers.get_content_type() != "text/plain":
            raise ValueError("Servletul nu a returnat text/plain. Verifica URL-ul aplicatiei.")
        if text != value:
            raise ValueError("Raspunsul servletului nu coincide cu parametrul trimis.")
        return text


def main():
    parser = argparse.ArgumentParser(description="Trimite valoarea 1 sau 2 la servlet.")
    parser.add_argument("value", choices=("1", "2"), help="Valoarea parametrului")
    parser.add_argument(
        "url",
        nargs="?",
        default="http://localhost:8080/LAB1_war_exploded/controller",
        help="URL-ul servletului (implicit: configuratia IntelliJ)",
    )
    args = parser.parse_args()
    try:
        print(invoke_servlet(args.value, args.url))
    except HTTPError as error:
        detail = error.read().decode("utf-8", errors="replace")
        print(f"Eroare HTTP {error.code}: {detail}", file=sys.stderr)
        return 1
    except (URLError, TimeoutError, ValueError, OSError) as error:
        print(f"Eroare: {error}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
