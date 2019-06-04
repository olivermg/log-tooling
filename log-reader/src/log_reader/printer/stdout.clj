(ns log-reader.printer.stdout
  (:require [log-reader.printer :as p]))

(defrecord StdoutPrinter []

  p/Printer

  (print-lines [this lines]
    (doseq [line lines]
      (println line))))

(defn construct []
  (map->StdoutPrinter {}))
