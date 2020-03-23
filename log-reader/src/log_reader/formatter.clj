(ns log-reader.formatter)

(defprotocol Formatter
  (format-line [this line]))
