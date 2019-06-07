(ns log-reader.printer)

(defprotocol Printer
  (print-line-xf [this]))
