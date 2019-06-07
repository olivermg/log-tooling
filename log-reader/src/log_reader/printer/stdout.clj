(ns log-reader.printer.stdout
  (:require [log-reader.printer :as p]))

(defrecord StdoutPrinter []

  p/Printer

  (print-line-xf [this]
    (map #(do (println %) %))))

(defn construct []
  (map->StdoutPrinter {}))
