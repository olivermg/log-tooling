(ns log-reader.printer)

(defprotocol Printer
  (print-lines [this lines]))
