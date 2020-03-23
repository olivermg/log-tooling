(ns log-reader.printer.stdout
  (:require [log-reader.printer :as p]))

(defrecord StdoutPrinter []

  p/Printer

  (print-line [this line]
    (println line)))

(defn construct []
  (map->StdoutPrinter {}))
