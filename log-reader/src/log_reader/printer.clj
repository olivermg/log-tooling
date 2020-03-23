(ns log-reader.printer)

(defprotocol Printer
  (print-line [this line]))
